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

import config.ErrorHandler
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import pages._
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import services.UpdateCaseService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.cya.CYAUpdateCaseSummaryListHelper
import views.html.{UpdateCaseCheckYourAnswersView, UpdateCaseConfirmationView}
import javax.inject.{Inject, Singleton}
import models.UpdateCaseError
import views.html.errors.InformationCannotBeAddedView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateCaseCheckYourAnswersController @Inject()(identify: IdentifierAction,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     mcc: MessagesControllerComponents,
                                                     sessionRepository: SessionRepository,
                                                     updateCaseService: UpdateCaseService,
                                                     view: UpdateCaseCheckYourAnswersView,
                                                     informationCannotBeAddedView: InformationCannotBeAddedView,
                                                     confirmationView: UpdateCaseConfirmationView,
                                                     errorHandler: ErrorHandler,
                                                     implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with CYAUpdateCaseSummaryListHelper {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(CheckModePage, true))
      _ <- sessionRepository.set(updatedAnswers)
    } yield {
      Ok(view(buildUpdateCaseSummaryList))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(DisclosureReferenceNumberPage) match {
      case Some(caseId) =>
        updateCaseService.updateCase().flatMap {
          case Left(UpdateCaseError.CaseAlreadyClosed) =>
            Future.successful(InternalServerError(informationCannotBeAddedView(caseId)))
          case Left(_) =>
            Future.successful(errorHandler.showInternalServerError)
          case Right(_) =>
            sessionRepository
              .remove(request.credId)
              .map(_ => Ok(confirmationView(caseId)))
        }
      case None =>
        Future.successful(errorHandler.showInternalServerError)
    }
  }
}
