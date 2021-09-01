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

package controllers.updateCase

import config.AppConfig
import controllers.FileUploadHandler
import controllers.actions._
import forms.shared.UploadFileFormProvider
import models.requests.DataRequest
import models.upscan._
import models.{FileUploadInfo, UserAnswers}
import pages.updateCase.UploadSupportingDocumentationPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import repositories.{FileUploadRepository, SessionRepository}
import services.UpScanService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.shared.FileUploadProgressView
import views.html.updateCase.UploadSupportingDocumentationView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.util.Try

@Singleton
class UploadSupportingDocumentationController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  mcc: MessagesControllerComponents,
  val fileUploadRepository: FileUploadRepository,
  val sessionRepository: SessionRepository,
  upScanService: UpScanService,
  view: UploadSupportingDocumentationView,
  progressView: FileUploadProgressView,
  formProvider: UploadFileFormProvider,
  implicit val appConfig: AppConfig,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport
    with FileUploadHandler[FileUploadInfo] {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.flash.get("uploadError") match {
      case Some("TooSmall")    => formProvider().withError("file", Messages("uploadFile.error.tooSmall"))
      case Some("TooBig")      => formProvider().withError("file", Messages("uploadFile.error.tooBig"))
      case Some("Unknown")     => formProvider().withError("file", Messages("uploadFile.error.unknown"))
      case Some("Rejected")    => formProvider().withError("file", Messages("uploadFile.error.rejected"))
      case Some("Quarantined") => formProvider().withError("file", Messages("uploadFile.error.quarantined"))
      case _                   => formProvider()
    }

    val numberOfFilesUploaded: Int =
      request.userAnswers.get(UploadSupportingDocumentationPage).getOrElse(Seq.empty).length

    upScanService.initiateSupportingDocJourney().map { response =>
      Ok(view(form, response, backLink(), numberOfFilesUploaded, request.checkMode))
        .removingFromSession("SupportingDocumentationUpscanReference")
        .addingToSession("SupportingDocumentationUpscanReference" -> response.reference.value)
    }
  }

  def upscanResponseHandler(
    key: Option[String] = None,
    errorCode: Option[String] = None,
    errorMessage: Option[String] = None,
    errorResource: Option[String] = None,
    errorRequestId: Option[String] = None
  ): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val upscanError  = buildUpscanError(errorCode, errorMessage, errorResource, errorRequestId)
    val errorRoute   = Redirect(controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad())
    val successRoute = Redirect(
      controllers.updateCase.routes.UploadSupportingDocumentationController
        .uploadProgress(key.getOrElse("this will never be used"))
    )

    handleUpscanResponse(key, upscanError, successRoute, errorRoute)
  }

  def uploadProgress(key: String): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val uploadCompleteRoute   =
        Redirect(controllers.updateCase.routes.UploadSupportingDocumentationSummaryController.onLoad())
      val uploadFailedRoute     = Redirect(controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad())
      val uploadInProgressRoute = Ok(
        progressView(
          key = key,
          action = controllers.updateCase.routes.UploadSupportingDocumentationController.uploadProgress(key).url
        )
      )
      val updateFilesList: FileUpload => Seq[FileUploadInfo] = { file =>
        val upload = extractFileDetails(file, key)
        request.userAnswers.get(UploadSupportingDocumentationPage).getOrElse(Seq.empty) :+ upload
      }
      val saveFilesList: Seq[FileUploadInfo] => Try[UserAnswers] = { list =>
        request.userAnswers.set(UploadSupportingDocumentationPage, list)
      }

      handleUpscanFileProcessing(
        key,
        uploadCompleteRoute,
        uploadInProgressRoute,
        uploadFailedRoute,
        updateFilesList,
        saveFilesList
      )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call =
    request.userAnswers.get(UploadSupportingDocumentationPage) match {
      case Some(files) if files.nonEmpty =>
        controllers.updateCase.routes.UploadSupportingDocumentationSummaryController.onLoad()
      case _                             => controllers.updateCase.routes.MoreDocumentationController.onLoad()
    }

}
