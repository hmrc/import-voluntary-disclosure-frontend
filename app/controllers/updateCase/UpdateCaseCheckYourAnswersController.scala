/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.updateCase

import config.ErrorHandler
import controllers.IVDFrontendController
import controllers.actions._
import models.UpdateCaseError
import pages._
import pages.serviceEntry.KnownEoriDetailsPage
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.mvc._
import repositories.SessionRepository
import services.UpdateCaseService
import viewmodels.cya.CYAUpdateCaseSummaryListHelper
import views.html.updateCase.{UpdateCaseCheckYourAnswersView, UpdateCaseConfirmationView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateCaseCheckYourAnswersController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  mcc: MessagesControllerComponents,
  sessionRepository: SessionRepository,
  updateCaseService: UpdateCaseService,
  view: UpdateCaseCheckYourAnswersView,
  confirmationView: UpdateCaseConfirmationView,
  errorHandler: ErrorHandler,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc)
    with CYAUpdateCaseSummaryListHelper {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(CheckModePage, true))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Ok(view(buildUpdateCaseSummaryList))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(DisclosureReferenceNumberPage) match {
      case Some(caseId) =>
        updateCaseService.updateCase.flatMap {
          case Left(UpdateCaseError.InvalidCaseId) =>
            Future.successful(Redirect(controllers.updateCase.routes.DisclosureNotFoundController.onLoad()))
          case Left(UpdateCaseError.CaseAlreadyClosed) =>
            Future.successful(Redirect(controllers.updateCase.routes.DisclosureClosedController.onLoad()))
          case Left(_) =>
            Future.successful(errorHandler.showInternalServerError)
          case Right(_) =>
            sessionRepository
              .set(request.userAnswers.preserve(Seq(KnownEoriDetailsPage)))
              .map(_ => Ok(confirmationView(caseId)))
        }
      case None =>
        Future.successful(errorHandler.showInternalServerError)
    }
  }
}
