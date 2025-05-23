/*
 * Copyright 2025 HM Revenue & Customs
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

import config.{AppConfig, ErrorHandler}
import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, PrivateIndividualAuthAction}
import forms.serviceEntry.IndividualContinueWithCredentialsFormProvider
import pages.serviceEntry.IndividualContinueWithCredentialsPage
import play.api.mvc._
import repositories.SessionRepository
import views.html.serviceEntry.IndividualContinueWithCredentialsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndividualContinueWithCredentialsController @Inject() (
  privateIndividualAuth: PrivateIndividualAuthAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: IndividualContinueWithCredentialsView,
  formProvider: IndividualContinueWithCredentialsFormProvider,
  appConfig: AppConfig,
  val errorHandler: ErrorHandler,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad(): Action[AnyContent] = (privateIndividualAuth andThen getData andThen requireData).async {
    implicit request =>
      val form = request.userAnswers.get(IndividualContinueWithCredentialsPage).fold(formProvider()) {
        formProvider().fill
      }
      Future.successful(Ok(view(form, backLink())))
  }

  def onSubmit(): Action[AnyContent] = (privateIndividualAuth andThen getData andThen requireData).async {
    implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink()))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IndividualContinueWithCredentialsPage, value))
            _ <- if (value) sessionRepository.set(updatedAnswers) else sessionRepository.remove(request.credId)
          } yield {
            if (value) {
              Redirect(appConfig.eccSubscribeUrl)
            } else {
              Redirect(appConfig.signOutUrl)
            }
          }
      )
  }

  private[serviceEntry] def backLink(): Option[Call] =
    Some(controllers.serviceEntry.routes.CustomsDeclarationController.onLoad())

}
