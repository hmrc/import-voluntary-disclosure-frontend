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

package controllers.importDetails

import config.ErrorHandler
import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.importDetails.ImporterVatRegisteredFormProvider
import models.requests.DataRequest
import pages.importDetails.ImporterVatRegisteredPage
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import views.html.importDetails.ImporterVatRegisteredView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ImporterVatRegisteredController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  errorHandler: ErrorHandler,
  formProvider: ImporterVatRegisteredFormProvider,
  view: ImporterVatRegisteredView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    request.getImporterName.fold(errorHandler.showInternalServerError) { importerName =>
      val form = request.userAnswers.get(ImporterVatRegisteredPage).fold(formProvider(importerName)) {
        formProvider(importerName).fill
      }
      Ok(view(form, importerName, backLink()))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.getImporterName.fold(Future.successful(errorHandler.showInternalServerError)) { importerName =>
      formProvider(importerName).bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, importerName, backLink()))),
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ImporterVatRegisteredPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield {
            if (request.checkMode) {
              Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
            } else {
              Redirect(controllers.importDetails.routes.NumberOfEntriesController.onLoad())
            }
          }
        }
      )
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Option[Call] = {
    if (request.checkMode) {
      None
    } else {
      Some(controllers.importDetails.routes.ImporterEORINumberController.onLoad())
    }
  }
}
