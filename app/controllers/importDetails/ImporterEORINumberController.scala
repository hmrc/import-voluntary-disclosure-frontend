/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.importDetails

import config.ErrorHandler
import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.importDetails.ImporterEORINumberFormProvider
import models.requests.DataRequest
import pages.importDetails.ImporterEORINumberPage
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.importDetails.ImporterEORINumberView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ImporterEORINumberController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  errorHandler: ErrorHandler,
  formProvider: ImporterEORINumberFormProvider,
  view: ImporterEORINumberView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    request.getImporterName.fold(errorHandler.showInternalServerError) { importerName =>
      val form = request.userAnswers.get(ImporterEORINumberPage).fold(formProvider(importerName)) {
        formProvider(importerName).fill
      }
      Ok(view(form, importerName, backLink()))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.getImporterName.fold(Future.successful(errorHandler.showInternalServerError)) { importerName =>
      formProvider(importerName).bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, importerName, backLink()))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ImporterEORINumberPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(controllers.importDetails.routes.ImporterVatRegisteredController.onLoad())
      )
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Option[Call] = {
    if (request.checkMode) {
      None
    } else {
      Some(controllers.importDetails.routes.ImporterEORIExistsController.onLoad())
    }
  }
}
