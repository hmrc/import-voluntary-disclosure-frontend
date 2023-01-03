/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.underpayments

import controllers.IVDFrontendController
import controllers.actions._
import forms.underpayments.UnderpaymentTypeFormProvider
import models.UserAnswers
import models.requests.DataRequest
import pages.underpayments._
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.html.underpayments.UnderpaymentTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnderpaymentTypeController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  underpaymentTypeView: UnderpaymentTypeView,
  formProvider: UnderpaymentTypeFormProvider,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  private val underpaymentTypes = Seq("B00", "A00", "E00", "A20", "A30", "A35", "A40", "A45", "A10", "D10")

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val availableUnderpaymentTypes: Seq[String] = getAvailableUnderpayments()
    if (availableUnderpaymentTypes.isEmpty) {
      Future.successful(Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()))
    } else {
      val form = request.userAnswers.get(UnderpaymentTypePage).fold(formProvider()) {
        formProvider().fill
      }
      val availableUnderPaymentTypesOptions = createRadioButton(form, availableUnderpaymentTypes)
      Future.successful(
        Ok(underpaymentTypeView(form, backLink(request.userAnswers), availableUnderPaymentTypesOptions, isFirstTime()))
      )
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        val availableUnderPaymentTypes: Seq[String] = getAvailableUnderpayments()
        val error                                   = formWithErrors.errors.head
        val replacementFormError = formWithErrors.copy(errors = Seq(error.copy(key = availableUnderPaymentTypes.head)))
        Future.successful(
          BadRequest(
            underpaymentTypeView(
              replacementFormError,
              backLink(request.userAnswers),
              createRadioButton(replacementFormError, availableUnderPaymentTypes),
              isFirstTime()
            )
          )
        )
      },
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentTypePage, value))
          updatedAnswers <- Future.fromTry(updatedAnswers.remove(UnderpaymentDetailsPage))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(controllers.underpayments.routes.UnderpaymentDetailsController.onLoad(value))
      }
    )
  }

  private[underpayments] def getAvailableUnderpayments()(implicit request: DataRequest[_]): Seq[String] = {
    val existingUnderpaymentDetails =
      request.userAnswers.get(UnderpaymentDetailSummaryPage).getOrElse(Seq.empty).map(item => item.duty)
    underpaymentTypes.filterNot(item => existingUnderpaymentDetails.contains(item))
  }

  private def createRadioButton(form: Form[_], values: Seq[String])(implicit messages: Messages): Seq[RadioItem] = {
    values.map(keyValue =>
      RadioItem(
        value = Some(keyValue),
        content = Text(messages(s"underpaymentType.$keyValue.radio")),
        checked = form("value").value.contains(keyValue),
        id = Some(keyValue)
      )
    )
  }

  private[controllers] def backLink(userAnswers: UserAnswers): Call = {
    val underpaymentDetailsList = userAnswers.get(UnderpaymentDetailSummaryPage).getOrElse(Seq.empty)
    if (underpaymentDetailsList.nonEmpty) {
      controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
    } else {
      controllers.underpayments.routes.UnderpaymentStartController.onLoad()
    }
  }

  private def isFirstTime()(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(UnderpaymentDetailSummaryPage) match {
      case Some(value) if value.nonEmpty => false
      case _                             => true
    }

}
