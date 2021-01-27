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
import models.FileUploadInfo
import models.upscan._
import play.api.i18n.I18nSupport
import play.api.libs.json.JsValue
import play.api.mvc._
import queries.FileUploadQuery
import repositories.{FileUploadRepository, SessionRepository}
import services.UpScanService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{UploadFileView, UploadProgressView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class UploadFileController @Inject()(identify: IdentifierAction,
                                     getData: DataRetrievalAction,
                                     requireData: DataRequiredAction,
                                     mcc: MessagesControllerComponents,
                                     fileUploadRepository: FileUploadRepository,
                                     sessionRepository: SessionRepository,
                                     upScanService: UpScanService,
                                     view: UploadFileView,
                                     progressView: UploadProgressView,
                                     implicit val appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    upScanService.initiateNewJourney().map { response =>
      Ok(view(response, controllers.routes.SupportingDocController.onLoad))
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

    if (errorCode.isDefined) {
      // TODO: Redirect to synchronous error page
      Future.successful(
        Redirect(controllers.routes.UploadFileController.onLoad)
          .flashing(
            "key" -> key.getOrElse(""),
            "errorCode" -> errorCode.getOrElse(""),
            "errorMessage" -> errorMessage.getOrElse(""),
            "errorResource" -> errorResource.getOrElse(""),
            "errorRequestId" -> errorRequestId.getOrElse("")
          )
      )
    } else {
      key match {
        case Some(key) => fileUploadRepository.updateRecord(FileUpload(key, Some(request.credId))).map { _ =>
          // TODO: Add delay to give upscan time to process file
          Redirect(controllers.routes.UploadFileController.uploadProgress(key))
        }
        case _ => throw new RuntimeException("No key returned for successful upload")
      }
    }
  }

  def uploadProgress(key: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    fileUploadRepository.getRecord(key).flatMap(_ match {
      case Some(doc) => doc.fileStatus match {
        case Some(status) if (status == FileStatusEnum.READY) => {
          val newFile: FileUploadInfo = FileUploadInfo(
            fileName = doc.fileName.get,
            downloadUrl = doc.downloadUrl.get,
            uploadTimestamp = doc.uploadDetails.get.uploadTimestamp,
            checksum = doc.uploadDetails.get.checksum,
            fileMimeType = doc.uploadDetails.get.fileMimeType,
          )

          val updatedListFiles = request.userAnswers.get(FileUploadQuery).getOrElse(Seq.empty) ++ Seq(newFile)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(FileUploadQuery, updatedListFiles)(FileUploadQuery.queryWrites))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.UploadAnotherFileController.onLoad())
          }
        }
        case Some(status) => Future.successful(Redirect(controllers.routes.UploadFileController.onLoad())) // TODO: Failure
        case None => Future.successful(Ok(progressView(key, controllers.routes.UploadFileController.onLoad)))
      }
      case None => Future.successful(InternalServerError)
    })
  }

  def showProgress(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.flash.get("key") match {
      case Some(reference) => Future.successful(Ok(progressView(reference, controllers.routes.UploadFileController.onLoad)))
      case None => Future.successful(InternalServerError)
    }
  }

  def callbackHandler(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[FileUpload] { fileUploadResponse =>
      fileUploadRepository.updateRecord(deriveFileStatus(fileUploadResponse)).map { isOk =>
        if (isOk) NoContent else InternalServerError
      }
    }
  }

  private[controllers] def deriveFileStatus(fileUpload: FileUpload): FileUpload = {
    fileUpload.failureDetails match {
      case Some(details) if(details.failureReason=="QUARANTINE") =>
        fileUpload.copy(fileStatus = Some(FileStatusEnum.FAILED_QUARANTINE))
      case Some(details) if(details.failureReason=="REJECTED") =>
        fileUpload.copy(fileStatus = Some(FileStatusEnum.FAILED_REJECTED))
      case Some(details) =>
        fileUpload.copy(fileStatus = Some(FileStatusEnum.FAILED_UNKNOWN))
      case None => fileUpload
    }
  }

}
