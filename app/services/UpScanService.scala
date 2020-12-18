/*
 * Copyright 2020 HM Revenue & Customs
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

package services

import com.google.inject.Inject
import config.AppConfig
import connectors.UpScanConnector
import play.api.Logger
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}

import scala.concurrent.{ExecutionContext, Future}

class UpScanService @Inject()(upScanConnector: UpScanConnector,
                              appConfig: AppConfig,
                              fileUploadRepository: FileUploadRepository)
                             (implicit ec: ExecutionContext) {

  lazy val callbackUrlForSuccessOrFailureOfFileUpload: String = appConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload
  lazy val successRedirectForUser: String = appConfig.upScanSuccessRedirectForUser
  lazy val errorRedirectForUser: String = appConfig.upScanErrorRedirectForUser
  lazy val minFileSize: Int = appConfig.upScanMinFileSize
  lazy val maxFileSize: Int = appConfig.upScanMaxFileSize

  lazy val buildUpScanBodyFromConfig: UpScanInitiateBody = {
    UpScanInitiateBody(
      callbackUrlForSuccessOrFailureOfFileUpload,
      successRedirectForUser,
      errorRedirectForUser,
      minFileSize,
      maxFileSize
    )
  }

  def initiateNewJourney(upScanInitiateBody: UpScanInitiateBody = buildUpScanBodyFromConfig)
                        (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[UpScanInitiateResponseModel] = {
    upScanConnector.postToInitiate(upScanInitiateBody).map {
      case Right(upScanInitiateResponseModel: UpScanInitiateResponseModel) => upScanInitiateResponseModel
      case Left(e: ErrorResponse) => throw new InternalServerException(e.body)
    }
  }

  def downloadStatus(reference: String): Future[Either[UpscanError, (UpscanSuccess, String)]] =
    fileUploadRepository.getRecord(reference).map {
      case Some(FileUpload(_, _, None)) =>
        Logger.info("[UpScanService][downloadStatus] No Response received")
        Left(NO_RESPONSE)
      case Some(FileUpload(_, _, Some(err: UpscanError))) =>
        Logger.info(s"[UpScanService][downloadStatus] Error Response received: $err")
        Left(err)
      case Some(FileUpload(_, Some(url), Some(READY))) =>
        Right(READY -> url)
      case _ =>
        Logger.info(s"[UpScanService][downloadStatus] No upscan reference held in session")
        throw new IllegalArgumentException(s"No file upload journey exists for reference $reference")
    }
}
