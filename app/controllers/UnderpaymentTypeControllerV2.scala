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

package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.UnderpaymentTypeFormProviderV2
import pages.UnderpaymentTypePageV2
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UnderpaymentTypeViewV2

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnderpaymentTypeControllerV2 @Inject()(identify: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             sessionRepository: SessionRepository,
                                             mcc: MessagesControllerComponents,
                                             underpaymentTypeView: UnderpaymentTypeViewV2,
                                             formProvider: UnderpaymentTypeFormProviderV2)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = controllers.routes.UnderpaymentStartController.onLoad()

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(UnderpaymentTypePageV2).fold(formProvider()) {
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
          updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentTypePageV2, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          value match {
            case _ => Redirect(controllers.routes.UnderpaymentTypeControllerV2.onLoad())
          }
        }
      }
    )
  }

  private def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = {
    Seq("B00", "A00", "E00", "A20", "A30", "A35", "A40", "A45", "A10", "D10").map(value => createRadioButton(form, value))
  }

  private def createRadioButton(form: Form[_], value: String)(implicit messages: Messages): RadioItem = {
    RadioItem(
      value = Some(value),
      content = Text(messages(s"underpaymentTypeTemp.$value.radio")),
      checked = form("value").value.contains(value)
    )
  }

}
