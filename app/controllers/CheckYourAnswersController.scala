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

import connectors.IvdSubmissionConnector
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import javax.inject.{Inject, Singleton}
import models.IvdSubmission
import pages.CheckModePage
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.CYASummaryListHelper
import views.html.{CheckYourAnswersView, ConfirmationView}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           mcc: MessagesControllerComponents,
                                           sessionRepository: SessionRepository,
                                           ivdSubmissionConnector: IvdSubmissionConnector,
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
          buildAboutImporterSummaryList(request.userAnswers) ++
          buildDisclosureDetailsSummaryList(request.userAnswers) ++
          buildAmendmentDetailsSummaryList(request.userAnswers) ++
          buildSupportingDocumentsSummaryList(request.userAnswers) ++
          buildYourDetailsSummaryList(request.userAnswers) ++
          buildPaymentInformationSummaryList(request.userAnswers)
      ))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    Json.fromJson[IvdSubmission](request.userAnswers.data) match {
      case JsSuccess(submission, _) => {
        ivdSubmissionConnector.postSubmission(submission).flatMap {
          case Right(value) => Future.successful(Ok(confirmationView(value.id)))
          case Left(error) => Future.successful(InternalServerError)
        }
      }
      case JsError(_) => throw new RuntimeException("Completed journey answers does not parse to IVDSubmission model")
    }
  }

}
