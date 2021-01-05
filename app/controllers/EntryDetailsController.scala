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

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.EntryDetailsFormProvider
import javax.inject.{Inject, Singleton}
import models.UserAnswers
import pages.EntryDetailsPage
import play.api.i18n.Messages.Implicits.applicationMessages
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.EntryDetails

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EntryDetailsController @Inject()(identity: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       sessionRepository: SessionRepository,
                                       appConfig: AppConfig,
                                       mcc: MessagesControllerComponents,
                                       formProvider: EntryDetailsFormProvider,
                                       view: EntryDetails)
  extends FrontendController(mcc) with I18nSupport {

  implicit val config: AppConfig = appConfig

  //TODO Do we need check mode now or later
  def onLoad: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    Future.successful(Ok(view(formProvider())))
  }

  def onSubmit: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val userAnswers = request.userAnswers

    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(EntryDetailsPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.EntryDetailsController.onLoad())
        }
      }
    )
  }

}
