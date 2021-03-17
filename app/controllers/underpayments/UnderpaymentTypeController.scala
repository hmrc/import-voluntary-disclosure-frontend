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

package controllers.underpayments

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.underpayments.UnderpaymentTypeFormProvider
import pages.underpayments.TempUnderpaymentTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.underpayments.UnderpaymentTypeView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnderpaymentTypeController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           sessionRepository: SessionRepository,
                                           mcc: MessagesControllerComponents,
                                           underpaymentTypeView: UnderpaymentTypeView,
                                           formProvider: UnderpaymentTypeFormProvider)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.UnderpaymentStartController.onLoad()

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(TempUnderpaymentTypePage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(
      Ok(underpaymentTypeView(form, backLink, options(form)))
    )
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(underpaymentTypeView(formWithErrors, backLink, options(formWithErrors))))
      },
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(TempUnderpaymentTypePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          value match {
            case _ => Redirect(controllers.underpayments.routes.UnderpaymentTypeController.onLoad())
          }
        }
      }
    )
  }

  private[underpayments] def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = {
    Seq(
      createRadioButton(form, "B00", "underpaymentType.importVAT"),
      createRadioButton(form, "A00", "underpaymentType.customsDuty"),
      createRadioButton(form, "E00", "underpaymentType.exciseDuty"),
      createRadioButton(form, "A20", "underpaymentType.additionalDuty"),
      createRadioButton(form, "A30", "underpaymentType.definitiveAntiDumpingDuty"),
      createRadioButton(form, "A35", "underpaymentType.provisionalAntiDumpingDuty"),
      createRadioButton(form, "A40", "underpaymentType.definitiveCountervailingDuty"),
      createRadioButton(form, "A45", "underpaymentType.provisionalCountervailingDuty"),
      createRadioButton(form, "A10", "underpaymentType.agriculturalDuty"),
      createRadioButton(form, "D10", "underpaymentType.compensatoryDuty"),
    )
  }

  private[underpayments] def createRadioButton(
                                                form: Form[_],
                                                value: String,
                                                messageKey: String)(implicit messages: Messages): RadioItem = {
    RadioItem(
      value = Some(value),
      content = Text(messages(messageKey)),
      checked = form("value").value.contains(value)
    )
  }

}
