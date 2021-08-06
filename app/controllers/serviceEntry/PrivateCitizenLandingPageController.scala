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

import forms.serviceEntry.PrivateCitizenLandingPageFormProvider
import models.UserAnswers
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.serviceEntry.PrivateCitizenLandingPageView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrivateCitizenLandingPageController @Inject()(sessionRepository: SessionRepository,
                                                    mcc: MessagesControllerComponents,
                                                    formProvider: PrivateCitizenLandingPageFormProvider,
                                                    view: PrivateCitizenLandingPageView,
                                                    implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = Action.async { implicit request =>

    request.session.get("credId") match {
      case Some(credId) => {
        val userAnswers = sessionRepository.get(credId) match {
          case Some(answers) => answers
          case None => UserAnswers(credId)
        }
        val form = sessionRepository.get(credId)
      }
      case None => ???
    }

    Future.successful(Ok(view(formProvider())))
  }

  def onSubmit(): Action[AnyContent] = Action.async { implicit request =>

    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
      value =>
        if (value) {
          Future.successful(Redirect(controllers.serviceEntry.routes.PrivateCitizenLandingPageController.onLoad()))
        }
        else {
          Future.successful(Redirect(controllers.serviceEntry.routes.PrivateCitizenLandingPageController.onLoad()))
        }
    )
  }

}
