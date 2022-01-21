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

package controllers.cya

import config.ErrorHandler
import controllers.actions._
import pages._
import pages.importDetails._
import pages.serviceEntry.KnownEoriDetailsPage
import pages.underpayments.{TempUnderpaymentTypePage, UnderpaymentCheckModePage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import services.SubmissionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.cya.{CYASummaryListHelper, ConfirmationViewData}
import views.html.cya._

import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

@Singleton
class CheckYourAnswersController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  mcc: MessagesControllerComponents,
  sessionRepository: SessionRepository,
  submissionService: SubmissionService,
  view: CheckYourAnswersView,
  importerConfirmationView: ImporterConfirmationView,
  repConfirmationView: RepresentativeConfirmationView,
  errorHandler: ErrorHandler,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport
    with CYASummaryListHelper {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(CheckModePage, true))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(UnderpaymentCheckModePage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(TempUnderpaymentTypePage))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Ok(view(buildFullSummaryList()))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    submissionService.createCase.map {
      case Right(value) =>
        val confirmationData = {
          for {
            eoriDetails  <- request.userAnswers.get(KnownEoriDetailsPage)
            importerName <- Some(request.userAnswers.get(ImporterNamePage).getOrElse(eoriDetails.name))
            eoriNumber   <- Some(request.userAnswers.get(ImporterEORINumberPage).getOrElse(eoriDetails.eori))
            importerEORI <- Some(request.userAnswers.get(ImporterEORINumberPage).getOrElse(""))
            _            <- Some(sessionRepository.set(request.userAnswers.preserve(Seq(KnownEoriDetailsPage))))
          } yield {
            request.userAnswers.get(EntryDetailsPage) match {
              case Some(entryDetails) =>
                val formattedDate = entryDetails.entryDate.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"))
                ConfirmationViewData(
                  s"${entryDetails.epu}-${entryDetails.entryNumber}-$formattedDate",
                  importerName,
                  eoriNumber,
                  importerEORI
                )
              case _ =>
                ConfirmationViewData(
                  "VARIOUS - Bulk Entry",
                  importerName,
                  eoriNumber,
                  importerEORI
                )
            }
          }
        }

        val submittedDate = DateTime.now(DateTimeZone.forID("Europe/London"))
        val summaryList   = buildSummaryListForPrint(value.id, submittedDate)

        confirmationData match {
          case Some(data) =>
            if (request.isRepFlow) {
              Ok(repConfirmationView(value.id, request.isPayByDeferment, request.isOneEntry, data, summaryList))
            } else {
              Ok(importerConfirmationView(value.id, request.isPayByDeferment, request.isOneEntry, data, summaryList))
            }
          case _ => errorHandler.showInternalServerError
        }
      case Left(_) => errorHandler.showInternalServerError
    }
  }

}
