/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.reasons

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.reasons.BoxNumberFormProvider
import models.UserAnswers
import models.reasons.BoxNumber
import models.reasons.BoxNumber.BoxNumber
import pages.reasons.{UnderpaymentReasonAmendmentPage, UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage, UnderpaymentReasonsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.reasons.BoxNumberView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BoxNumberController @Inject() (
  appConfig: AppConfig,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: BoxNumberFormProvider,
  view: BoxNumberView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  private lazy val backLink: Call = controllers.reasons.routes.BoxGuidanceController.onLoad()
  private val boxNumbers = {
    if (appConfig.otherItemEnabled) BoxNumber.values
    else BoxNumber.values - BoxNumber.OtherItem
  }

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(UnderpaymentReasonBoxNumberPage).fold(formProvider()) {
      formProvider().fill
    }

    val filteredBoxNumbers =
      boxNumbers.filterNot(boxNumber => underpaymentReasonSelected(request.userAnswers, boxNumber))
    val isFirstBox         = filteredBoxNumbers.size == boxNumbers.size
    Future.successful(Ok(view(form, backLink, createRadioButtons(form, filteredBoxNumbers), isFirstBox)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider()
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val filteredBoxNumbers =
            boxNumbers.filterNot(boxNumber => underpaymentReasonSelected(request.userAnswers, boxNumber))
          val isFirstBox         = filteredBoxNumbers.size == boxNumbers.size
          Future.successful(
            BadRequest(
              view(formWithErrors, backLink, createRadioButtons(formWithErrors, filteredBoxNumbers), isFirstBox)
            )
          )
        },
        value =>
          request.userAnswers.get(UnderpaymentReasonBoxNumberPage) match {
            case Some(oldValue) if oldValue != value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonBoxNumberPage, value))
                updatedAnswers <- Future.fromTry(updatedAnswers.remove(UnderpaymentReasonItemNumberPage))
                updatedAnswers <- Future.fromTry(updatedAnswers.remove(UnderpaymentReasonAmendmentPage))
                _              <- sessionRepository.set(updatedAnswers)
              } yield navigateTo(value)
            case _                                   =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonBoxNumberPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield navigateTo(value)
          }
      )
  }

  private[controllers] def navigateTo(value: BoxNumber) =
    value match {
      case BoxNumber.Box22 | BoxNumber.Box62 | BoxNumber.Box63 | BoxNumber.Box66 | BoxNumber.Box67 | BoxNumber.Box68 |
          BoxNumber.OtherItem =>
        Redirect(controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(value.id))
      case _ => Redirect(controllers.reasons.routes.ItemNumberController.onLoad())
    }

  private def createRadioButtons(form: Form[_], boxNumbers: Set[BoxNumber])(implicit
    messages: Messages
  ): Seq[RadioItem] = {
    def radioItem(boxNumber: BoxNumber): RadioItem =
      RadioItem(
        value = Some(boxNumber.id.toString),
        content = Text(messages(s"boxNumber.${boxNumber.id}.radio")),
        checked = form("value").value.contains(boxNumber.id.toString)
      )

    val radioButtons = (boxNumbers - BoxNumber.OtherItem).toList.map(radioItem)

    if (boxNumbers.contains(BoxNumber.OtherItem)) {
      val divider         = RadioItem(divider = Some("or"))
      val otherItemButton = radioItem(BoxNumber.OtherItem)

      radioButtons :+ divider :+ otherItemButton
    } else {
      radioButtons
    }
  }

  private[controllers] def underpaymentReasonSelected(userAnswers: UserAnswers, boxNumber: BoxNumber): Boolean =
    userAnswers
      .get(UnderpaymentReasonsPage)
      .getOrElse(Seq.empty)
      .exists(reason => reason.boxNumber == boxNumber && reason.itemNumber == 0)
}
