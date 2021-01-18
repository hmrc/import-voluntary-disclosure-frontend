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
import forms.{CustomsDutyFormProvider, ExciseUnderpaymentFormProvider}
import models.UnderpaymentType
import pages.{CustomsDutyPage, EntryDetailsPage, ExciseUnderpaymentPage, UnderpaymentTypePage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ExciseUnderpaymentView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExciseUnderpaymentController @Inject()(identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      sessionRepository: SessionRepository,
                                      mcc: MessagesControllerComponents,
                                      view: ExciseUnderpaymentView,
                                      formProvider: ExciseUnderpaymentFormProvider
                                     ) extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(ExciseUnderpaymentPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(
      view(form,backLink(request.userAnswers.get(UnderpaymentTypePage))
      )
     )
    )
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,
        backLink(request.userAnswers.get(UnderpaymentTypePage))
      ))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(ExciseUnderpaymentPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.ExciseUnderpaymentController.onLoad())  // Ians Page
        }
      }
    )
  }



  private[controllers] def backLink(underpaymentType: Option[UnderpaymentType]): Call =
    underpaymentType.map {
      case UnderpaymentType(_, true, _) => Call("GET",controllers.routes.ImportVATController.onLoad().toString) // Import VAT
      case UnderpaymentType(true, false, _) => Call("GET",controllers.routes.CustomsDutyController.onLoad().toString) // Customs Duty
      case _ => Call("GET",controllers.routes.UnderpaymentTypeController.onLoad().toString) // Underpayment page
    }.head
}
