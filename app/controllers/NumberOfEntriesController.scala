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
import forms.NumberOfEntriesFormProvider
import models.{NumberOfEntries, UserAnswers}
import pages.NumberOfEntriesPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.NumberOfEntriesView
import javax.inject.{Inject, Singleton}
import models.NumberOfEntries.{MoreThanOneEntry, OneEntry}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class NumberOfEntriesController @Inject()(identity: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          sessionRepository: SessionRepository,
                                          appConfig: AppConfig,
                                          mcc: MessagesControllerComponents,
                                          formProvider: NumberOfEntriesFormProvider,
                                          view: NumberOfEntriesView)
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
          updatedAnswers <- Future.fromTry(userAnswers.set(NumberOfEntriesPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          redirect(value)
        }
      }
    )
  }

  private def redirect (entries: NumberOfEntries) : Result = entries match {
    case OneEntry => Redirect(controllers.routes.NumberOfEntriesController.onLoad())
    case MoreThanOneEntry => Redirect(controllers.routes.NumberOfEntriesController.onLoad())
  }
}
