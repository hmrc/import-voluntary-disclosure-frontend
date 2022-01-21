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

package mocks.services

import models.upscan.UpScanInitiateResponse
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import services.UpScanService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockUpScanService extends MockFactory {

  val mockUpScanService: UpScanService = mock[UpScanService]

  object MockedUpScanService {
    def initiateNewJourney(response: Future[UpScanInitiateResponse]): CallHandler[Future[UpScanInitiateResponse]] =
      (mockUpScanService.initiateNewJourney()(_: ExecutionContext, _: HeaderCarrier))
        .expects(*, *)
        .returns(response)

    def initiateBulkJourney(response: Future[UpScanInitiateResponse]): CallHandler[Future[UpScanInitiateResponse]] =
      (mockUpScanService.initiateBulkJourney()(_: ExecutionContext, _: HeaderCarrier))
        .expects(*, *)
        .returns(response)

    def initiateAuthorityJourney(
      response: Future[UpScanInitiateResponse]
    ): CallHandler[Future[UpScanInitiateResponse]] =
      (mockUpScanService.initiateAuthorityJourney(_: String)(_: ExecutionContext, _: HeaderCarrier))
        .expects(*, *, *)
        .returns(response)

    def initiateSupportingDocJourney(
      response: Future[UpScanInitiateResponse]
    ): CallHandler[Future[UpScanInitiateResponse]] =
      (mockUpScanService.initiateSupportingDocJourney()(_: ExecutionContext, _: HeaderCarrier))
        .expects(*, *)
        .returns(response)

    def initiateCancelCaseJourney(
      response: Future[UpScanInitiateResponse]
    ): CallHandler[Future[UpScanInitiateResponse]] =
      (mockUpScanService.initiateCancelCaseJourney()(_: ExecutionContext, _: HeaderCarrier))
        .expects(*, *)
        .returns(response)

  }

}
