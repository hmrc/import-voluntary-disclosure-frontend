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
import forms.ImporterEORINumberFormProvider
import javax.inject.{Inject, Singleton}
import models.requests.DataRequest
import pages.ImporterEORINumberPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ImporterEORINumberView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ImporterEORINumberController @Inject()(identify: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             sessionRepository: SessionRepository,
                                             mcc: MessagesControllerComponents,
                                             formProvider: ImporterEORINumberFormProvider,
                                             view: ImporterEORINumberView
                                            ) extends FrontendController(mcc) with I18nSupport {

  private[controllers] def backLink()(implicit request: DataRequest[_]): Option[Call] = {
    if (request.checkMode) {
      None
    } else {
      Some(controllers.routes.ImporterEORIExistsController.onLoad())
    }
  }

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(ImporterEORINumberPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form,backLink)))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,backLink))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(ImporterEORINumberPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.ImporterVatRegisteredController.onLoad())
        }
      }
    )
  }
}
