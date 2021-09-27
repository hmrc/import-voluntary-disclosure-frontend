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

package controllers.serviceEntry

import config.{AppConfig, ErrorHandler}
import forms.serviceEntry.IndividualContinueWithCredentialsFormProvider
import models.UserAnswers
import pages.serviceEntry.IndividualContinueWithCredentialsPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.serviceEntry.IndividualContinueWithCredentialsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndividualContinueWithCredentialsController @Inject() (
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: IndividualContinueWithCredentialsView,
  formProvider: IndividualContinueWithCredentialsFormProvider,
  appConfig: AppConfig,
  val errorHandler: ErrorHandler,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  def onLoad(): Action[AnyContent] = Action.async { implicit request =>
    getUserAnswers(getCredId(request)).map { userAnswers =>
      val form = userAnswers.get(IndividualContinueWithCredentialsPage).fold(formProvider()) {
        formProvider().fill
      }
      Ok(view(form, backLink()))
    }
  }

  def onSubmit(): Action[AnyContent] = Action.async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink()))),
      value =>
        getUserAnswers(getCredId(request)).flatMap {
          case userAnswers: UserAnswers =>
            for {
              updatedAnswers <- Future.fromTry(userAnswers.set(IndividualContinueWithCredentialsPage, value))
              _ <- if (value) sessionRepository.set(updatedAnswers) else sessionRepository.remove(getCredId(request))
            } yield {
              if (value) {
                Redirect(appConfig.eccSubscribeUrl)
              } else {
                Redirect(appConfig.signOutUrl)
              }
            }
          case _ => Future.successful(errorHandler.showInternalServerError)
        }
    )
  }

  private[serviceEntry] def backLink(): Option[Call] =
    Some(controllers.serviceEntry.routes.CustomsDeclarationController.onLoad())

  def getCredId(request: Request[_]): String =
    request.session.apply("credId")

  def getUserAnswers(credId: String): Future[UserAnswers] =
    sessionRepository.get(credId).map {
      case Some(existingUserAnswers) => existingUserAnswers
      case None                      => UserAnswers(credId)
    }

}
