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

import config.AppConfig
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.UserAnswers
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.errors.SessionTimeoutView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SessionExpiredController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: SessionTimeoutView,
  implicit val ec: ExecutionContext,
  implicit val appConfig: AppConfig
) extends FrontendController(mcc)
    with I18nSupport {

  def keepAlive(): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    extendUserAnswersTimeout(request.userAnswers).map(_ => NoContent)
  }

  def timeout: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    sessionRepository.remove(request.credId).map(_ =>
      Redirect(
        appConfig.signOutUrl,
        Map("continue" -> Seq(controllers.serviceEntry.routes.SessionExpiredController.showView().url))
      )
    )
  }

  def showView: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(view()))
  }

  private def extendUserAnswersTimeout(answers: Option[UserAnswers]): Future[Boolean] = answers match {
    case Some(answers) => sessionRepository.set(answers)
    case None          => Future.successful(false)
  }
}
