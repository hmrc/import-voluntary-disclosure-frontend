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

import javax.inject.{Inject, Singleton}
import pages.CheckModePage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import services.SubmissionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.cya.CYASummaryListHelper
import views.html.{CheckYourAnswersView, ConfirmationView}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           mcc: MessagesControllerComponents,
                                           sessionRepository: SessionRepository,
                                           submissionService: SubmissionService,
                                           view: CheckYourAnswersView,
                                           confirmationView: ConfirmationView,
                                           implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with CYASummaryListHelper {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(CheckModePage, true))
      _ <- sessionRepository.set(updatedAnswers)
    } yield {
      Ok(view(
          buildImporterDetailsSummaryList ++
          buildEntryDetailsSummaryList ++
          buildUnderpaymentDetailsSummaryList ++
          buildYourDetailsSummaryList ++
          buildPaymentDetailsSummaryList ++
          buildDefermentDutySummaryList ++
          buildDefermentImportVatSummaryList
      ))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    submissionService.createCase.map {
      case Right(value) => Ok(confirmationView(value.id))
      case Left(error) => InternalServerError(error.message)
    }

  }

}
