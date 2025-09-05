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

package controllers.paymentInfo

import com.google.inject.Inject
import config.ErrorHandler
import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.paymentInfo.ImporterDanFormProvider
import models.requests.DataRequest
import pages.paymentInfo.DefermentAccountPage
import play.api.mvc._
import repositories.SessionRepository
import views.html.paymentInfo.ImporterDanView

import scala.concurrent.{ExecutionContext, Future}

class ImporterDanController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  errorHandler: ErrorHandler,
  formProvider: ImporterDanFormProvider,
  view: ImporterDanView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(DefermentAccountPage).fold(formProvider()) {
      formProvider().fill
    }
    request.getImporterName.fold(errorHandler.showInternalServerError) { name =>
      Future.successful(Ok(view(form, name, backLink())))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors =>
        request.getImporterName.fold(errorHandler.showInternalServerError) { name =>
          Future.successful(BadRequest(view(formWithErrors, name, backLink())))
        },
      value =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(DefermentAccountPage, value))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Option[Call] = {
    if (request.checkMode) {
      None
    } else {
      Some(controllers.paymentInfo.routes.DefermentController.onLoad())
    }
  }

}
