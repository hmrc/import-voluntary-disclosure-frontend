/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.serviceEntry

import config.ErrorHandler
import controllers.IVDFrontendController
import controllers.actions.{DataRetrievalAction, PrivateIndividualAuthAction}
import forms.serviceEntry.CustomsDeclarationFormProvider
import models.UserAnswers
import pages.serviceEntry.CustomsDeclarationPage
import play.api.mvc._
import repositories.SessionRepository
import views.html.serviceEntry.CustomsDeclarationView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CustomsDeclarationController @Inject() (
  privateIndividualAuth: PrivateIndividualAuthAction,
  getData: DataRetrievalAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: CustomsDeclarationFormProvider,
  view: CustomsDeclarationView,
  val errorHandler: ErrorHandler,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad(): Action[AnyContent] = (privateIndividualAuth andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))
    val form = userAnswers.get(CustomsDeclarationPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form)))
  }

  def onSubmit(): Action[AnyContent] = (privateIndividualAuth andThen getData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
      value => {
        val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))
        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(CustomsDeclarationPage, value))
          _              <- sessionRepository.set(updatedAnswers)
        } yield {
          if (value) {
            Redirect(controllers.serviceEntry.routes.IndividualContinueWithCredentialsController.onLoad())
          } else {
            Redirect(controllers.serviceEntry.routes.IndividualHandoffController.onLoad())
          }
        }
      }
    )
  }
}
