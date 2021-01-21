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

package services

import com.google.inject.Inject
import config.AppConfig
import connectors.UpScanConnector
import models.ErrorResponse
import models.upscan.{UpScanInitiateRequest, UpScanInitiateResponse}
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}

import scala.concurrent.{ExecutionContext, Future}

class UpScanService @Inject()(upScanConnector: UpScanConnector,
                              appConfig: AppConfig)
                             (implicit ec: ExecutionContext) {

  lazy val buildInitiateRequest: UpScanInitiateRequest = UpScanInitiateRequest(
    appConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload,
    appConfig.upScanSuccessRedirectForUser,
    appConfig.upScanErrorRedirectForUser,
    appConfig.upScanMinFileSize,
    appConfig.upScanMaxFileSize
  )

  def initiateNewJourney(upScanInitiateBody: UpScanInitiateRequest = buildInitiateRequest)
                        (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[UpScanInitiateResponse] = {
    upScanConnector.postToInitiate(upScanInitiateBody).map {
      case Right(upScanInitiateResponse: UpScanInitiateResponse) => upScanInitiateResponse
      case Left(e: ErrorResponse) => throw new InternalServerException(e.message)
    }
  }

}
