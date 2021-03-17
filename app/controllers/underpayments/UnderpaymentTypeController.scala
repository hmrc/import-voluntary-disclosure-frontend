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
import pages.ExciseDutyPage
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
            case "B00" => Redirect(controllers.underpayments.routes.UnderpaymentTypeController.onLoad())
            case "A00" => Redirect(controllers.underpayments.routes.UnderpaymentTypeController.onLoad())
            case "E00" => Redirect(controllers.underpayments.routes.UnderpaymentTypeController.onLoad())
          }
        }
      }
    )
  }

  private[underpayments] def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = {
    Seq(
      RadioItem(
        value = Some("B00"),
        content = Text(messages("underpaymentType.importVAT")),
        checked = form("value").value.contains("B00")
      ),
      RadioItem(
        value = Some("A00"),
        content = Text(messages("underpaymentType.customsDuty")),
        checked = form("value").value.contains("A00")
      ),
      RadioItem(
        value = Some("E00"),
        content = Text(messages("underpaymentType.exciseDuty")),
        checked = form("value").value.contains("E00")
      ),
      RadioItem(
        value = Some("A20"),
        content = Text(messages("underpaymentType.additionalDuty")),
        checked = form("value").value.contains("A20")
      ),
      RadioItem(
        value = Some("A30"),
        content = Text(messages("underpaymentType.definitiveAntiDumpingDuty")),
        checked = form("value").value.contains("A30")
      ),
      RadioItem(
        value = Some("A35"),
        content = Text(messages("underpaymentType.provisionalAntiDumpingDuty")),
        checked = form("value").value.contains("A35")
      ),
      RadioItem(
        value = Some("A40"),
        content = Text(messages("underpaymentType.definitiveCountervailingDuty")),
        checked = form("value").value.contains("A40")
      ),
      RadioItem(
        value = Some("A10"),
        content = Text(messages("underpaymentType.compensatoryDuty")),
        checked = form("value").value.contains("A10")
      )

    )
  }

}
