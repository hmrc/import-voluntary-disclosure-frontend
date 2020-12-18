/*
 * Copyright 2020 HM Revenue & Customs
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

import config.AppConfig
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import forms.UserTypeFormProvider
import models.UserAnswers
import pages.UserTypePage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UserTypeView
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Format.GenericFormat

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class UserTypeController @Inject()(identity: IdentifierAction,
                                   getData: DataRetrievalAction,
                                   sessionRepository: SessionRepository,
                                   appConfig: AppConfig,
                                   mcc: MessagesControllerComponents,
                                   formProvider: UserTypeFormProvider,
                                   view: UserTypeView)
  extends FrontendController(mcc) with I18nSupport {

  implicit val config: AppConfig = appConfig

  val onLoad: Action[AnyContent] = (identity andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))

    Future.successful(Ok(view(formProvider(), userAnswers)))

  }

  def onSubmit: Action[AnyContent] = (identity andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))

    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, userAnswers))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(UserTypePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.UserTypeController.onLoad())
        }
      }
    )
  }

}