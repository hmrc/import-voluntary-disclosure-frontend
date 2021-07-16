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

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.UploadFileFormProvider
import models.requests.DataRequest
import models.upscan.FileUpload
import models.{FileUploadInfo, UserAnswers}
import pages.FileUploadPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import repositories.{FileUploadRepository, SessionRepository}
import services.UpScanService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{BulkUploadFileView, FileUploadProgressView, FileUploadSuccessView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class BulkUploadFileController @Inject()(identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         mcc: MessagesControllerComponents,
                                         val fileUploadRepository: FileUploadRepository,
                                         val sessionRepository: SessionRepository,
                                         upScanService: UpScanService,
                                         view: BulkUploadFileView,
                                         progressView: FileUploadProgressView,
                                         formProvider: UploadFileFormProvider,
                                         successView: FileUploadSuccessView,
                                         implicit val appConfig: AppConfig,
                                         implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with FileUploadHandler[FileUploadInfo] {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val form = request.flash.get("uploadError") match {
      case Some("TooSmall") => formProvider().withError("file", Messages("uploadFile.error.tooSmall"))
      case Some("TooBig") => formProvider().withError("file", Messages("uploadFile.error.tooBig"))
      case Some("Unknown") => formProvider().withError("file", Messages("uploadFile.error.unknown"))
      case Some("Rejected") => formProvider().withError("file", Messages("uploadFile.error.rejected"))
      case Some("Quarantined") => formProvider().withError("file", Messages("uploadFile.error.quarantined"))
      case _ => formProvider()
    }

    upScanService.initiateBulkJourney().map { response =>
      Ok(view(form, response, backLink))
        .removingFromSession("UpscanReference")
        .addingToSession("UpscanReference" -> response.reference.value)
    }
  }

  def upscanResponseHandler(key: Option[String] = None,
                            errorCode: Option[String] = None,
                            errorMessage: Option[String] = None,
                            errorResource: Option[String] = None,
                            errorRequestId: Option[String] = None
                           ): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val upscanError = buildUpscanError(errorCode, errorMessage, errorResource, errorRequestId)
    val errorRoute = Redirect(controllers.routes.BulkUploadFileController.onLoad())
    val successRoute = Redirect(controllers.routes.BulkUploadFileController.uploadProgress(key.getOrElse("this will never be used")))

    handleUpscanResponse(key, upscanError, successRoute, errorRoute)
  }

  def uploadProgress(key: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val uploadCompleteRoute = Redirect(controllers.routes.BulkUploadFileController.onSuccess())
    val uploadFailedRoute = Redirect(controllers.routes.BulkUploadFileController.onLoad())
    val uploadInProgressRoute = Ok(
      progressView(
        key = key,
        action = controllers.routes.BulkUploadFileController.uploadProgress(key).url
      )
    )
    val updateFilesList: FileUpload => Seq[FileUploadInfo] = { file =>
      val upload = extractFileDetails(file, key)
      Seq(upload)
    }
    val saveFilesList: Seq[FileUploadInfo] => Try[UserAnswers] = { list =>
      request.userAnswers.set(FileUploadPage, list)(FileUploadPage.queryWrites)
    }

    handleUpscanFileProcessing(key,
      uploadCompleteRoute,
      uploadInProgressRoute,
      uploadFailedRoute,
      updateFilesList,
      saveFilesList
    )
  }

  def onSuccess(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val action = if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad().url
    } else {
      controllers.reasons.routes.MoreInformationController.onLoad().url
    }

    val filename = request.userAnswers.get(FileUploadPage).getOrElse(Seq.empty)
      .map(_.fileName)
      .headOption.getOrElse("No filename")

    Future.successful(
      Ok(successView(
        fileName = filename,
        action = action)
      )
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
    }
  }

}
