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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.RepresentativeNameFormProvider
import models.UserAnswers
import pages.RepresentativeNamePage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.RepresentativeNameView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class RepresentativeNameController @Inject()(identity: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             sessionRepository: SessionRepository,
                                             mcc: MessagesControllerComponents,
                                             formProvider: RepresentativeNameFormProvider,
                                             view: RepresentativeNameView
                                            ) extends FrontendController(mcc) with I18nSupport {


  def onLoad(): Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(RepresentativeNamePage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, controllers.routes.UserTypeController.onLoad)))
  }

  def onSubmit(): Action[AnyContent] = (identity andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, controllers.routes.UserTypeController.onLoad))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(RepresentativeNamePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.RepresentativeNameController.onLoad())
        }
      }
    )
  }

}
