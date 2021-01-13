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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.CustomsDutyFormProvider
import models.UnderpaymentType
import pages.{CustomsDutyPage, UnderpaymentTypePage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.CustomsDutyView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsDutyController @Inject()(identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      sessionRepository: SessionRepository,
                                      mcc: MessagesControllerComponents,
                                      view: CustomsDutyView,
                                      formProvider: CustomsDutyFormProvider
                                     ) extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    // TODO - add conditions for the form
    // TODO - test errors
    // TODO - add tests
    val form = request.userAnswers.get(CustomsDutyPage).fold(formProvider()) {
      formProvider().fill
    }

    form.value.get.amended
    Future.successful(Ok(view(form)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(CustomsDutyPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          redirect(request.userAnswers.get(UnderpaymentTypePage))
        }
      }
    )
  }

  private[controllers] def redirect(underpaymentType: Option[UnderpaymentType]): Result =
    underpaymentType.map {
      case UnderpaymentType(true, true, _) => Redirect(controllers.routes.CustomsDutyController.onLoad()) // Import VAT
      case UnderpaymentType(true, false, true) => Redirect(controllers.routes.CustomsDutyController.onLoad()) // Excise Duty
      case _ => Redirect(controllers.routes.CustomsDutyController.onLoad()) // Ians page
    }.head

}
