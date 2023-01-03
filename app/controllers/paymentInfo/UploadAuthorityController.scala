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

package controllers.paymentInfo

import config.{AppConfig, ErrorHandler}
import controllers.actions._
import controllers.{FileUploadHandler, IVDFrontendController}
import forms.shared.UploadFileFormProvider
import models.SelectedDutyTypes._
import models.requests.DataRequest
import models.upscan._
import models.{UploadAuthority, UserAnswers}
import pages.paymentInfo._
import play.api.Logger
import play.api.i18n.Messages
import play.api.mvc._
import repositories.{FileUploadRepository, SessionRepository}
import services.UpScanService
import views.html.paymentInfo.UploadAuthorityView
import views.html.shared.{FileUploadProgressView, FileUploadSuccessView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class UploadAuthorityController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  mcc: MessagesControllerComponents,
  val fileUploadRepository: FileUploadRepository,
  val sessionRepository: SessionRepository,
  upScanService: UpScanService,
  view: UploadAuthorityView,
  progressView: FileUploadProgressView,
  formProvider: UploadFileFormProvider,
  successView: FileUploadSuccessView,
  errorHandler: ErrorHandler,
  implicit val appConfig: AppConfig,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc)
    with FileUploadHandler[UploadAuthority] {

  private val logger = Logger("application." + getClass.getCanonicalName)

  def onLoad(dutyType: SelectedDutyType): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val splitPayment = request.userAnswers.get(SplitPaymentPage).getOrElse(false)
      val dutyTypeKey = dutyType match {
        case Vat  => "uploadAuthority.vat"
        case Duty => "uploadAuthority.duty"
        case Both => "uploadAuthority.both"
        case _    => "Underpayment Type not found"
      }

      val form = request.flash.get("uploadError") match {
        case Some("TooSmall")    => formProvider().withError("file", Messages("uploadFile.error.tooSmall"))
        case Some("TooBig")      => formProvider().withError("file", Messages("uploadFile.error.tooBig"))
        case Some("Unknown")     => formProvider().withError("file", Messages("uploadFile.error.unknown"))
        case Some("Rejected")    => formProvider().withError("file", Messages("uploadFile.error.rejected"))
        case Some("Quarantined") => formProvider().withError("file", Messages("uploadFile.error.quarantined"))
        case _                   => formProvider()
      }

      request.getImporterName match {
        case Some(importerName) =>
          upScanService.initiateAuthorityJourney(dutyType.toString).map { response =>
            Ok(view(form, response, backLink(dutyType, request.dutyType, splitPayment), importerName, dutyTypeKey))
              .removingFromSession("AuthorityUpscanReference")
              .addingToSession("AuthorityUpscanReference" -> response.reference.value)
          }
        case None =>
          Future.successful(errorHandler.showInternalServerError)
      }

    }

  def upscanResponseHandler(
    dutyType: SelectedDutyType,
    key: Option[String] = None,
    errorCode: Option[String] = None,
    errorMessage: Option[String] = None,
    errorResource: Option[String] = None,
    errorRequestId: Option[String] = None
  ): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val upscanError = buildUpscanError(errorCode, errorMessage, errorResource, errorRequestId)
    val errorRoute =
      Redirect(controllers.paymentInfo.routes.UploadAuthorityController.onLoad(dutyType))
    val successRoute =
      Redirect(
        controllers.paymentInfo.routes.UploadAuthorityController.uploadProgress(
          dutyType,
          key.getOrElse("this will never be used")
        )
      )

    handleUpscanResponse(key, upscanError, successRoute, errorRoute)
  }

  def uploadProgress(dutyType: SelectedDutyType, key: String): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val splitPayment = request.userAnswers.get(SplitPaymentPage).getOrElse(false)
      val uploadCompleteRoute =
        Redirect(controllers.paymentInfo.routes.UploadAuthorityController.onSuccess(dutyType))
      val uploadFailedRoute = Redirect(controllers.paymentInfo.routes.UploadAuthorityController.onLoad(dutyType))
      val uploadInProgressRoute = Ok(
        progressView(
          key = key,
          action = controllers.paymentInfo.routes.UploadAuthorityController.uploadProgress(dutyType, key).url
        )
      )
      getDanNumber(dutyType, splitPayment) match {
        case Some(dan) =>
          val updateFilesList: FileUpload => Seq[UploadAuthority] = { file =>
            val upload                        = extractFileDetails(file, key)
            val newAuthority: UploadAuthority = UploadAuthority(dan, dutyType, upload)
            request.userAnswers.get(UploadAuthorityPage).getOrElse(Seq.empty).filterNot(
              _.dutyType == dutyType
            ) :+ newAuthority
          }
          val saveFilesList: Seq[UploadAuthority] => Try[UserAnswers] = { list =>
            request.userAnswers.set(UploadAuthorityPage, list)(UploadAuthorityPage.queryWrites)
          }

          handleUpscanFileProcessing(
            key,
            uploadCompleteRoute,
            uploadInProgressRoute,
            uploadFailedRoute,
            updateFilesList,
            saveFilesList
          )
        case None =>
          logger.error(s"DAN not found for $dutyType")
          Future.successful(errorHandler.showInternalServerError)
      }

    }

  def onSuccess(dutyType: SelectedDutyType): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val splitPayment = request.userAnswers.get(SplitPaymentPage).getOrElse(false)

      val action = request.dutyType match {
        case Both if !request.checkMode && splitPayment && dutyType == Duty =>
          controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad().url
        case _ => controllers.cya.routes.CheckYourAnswersController.onLoad().url
      }

      val filename = request.userAnswers.get(UploadAuthorityPage).getOrElse(Seq.empty)
        .filter(_.dutyType == dutyType)
        .map(_.file.fileName)
        .headOption.getOrElse("No filename")

      Future.successful(
        Ok(successView(fileName = filename, action = action))
      )
    }

  private[controllers] def backLink(
    currentDutyType: SelectedDutyType,
    selectedDutyTypes: SelectedDutyType,
    splitPayment: Boolean
  )(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      selectedDutyTypes match {
        case Both if splitPayment && currentDutyType == Duty =>
          controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad()
        case Both if splitPayment && currentDutyType == Vat =>
          controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad()
        case _ => controllers.paymentInfo.routes.RepresentativeDanController.onLoad()
      }
    }
  }

  private def getDanNumber(dutyType: SelectedDutyType, splitPayment: Boolean)(implicit
    request: DataRequest[_]
  ): Option[String] =
    dutyType match {
      case Vat if splitPayment => request.userAnswers.get(AdditionalDefermentNumberPage)
      case _                   => request.userAnswers.get(DefermentAccountPage)
    }

}
