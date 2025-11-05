/*
 * Copyright 2025 HM Revenue & Customs
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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar.mock
import services.UpScanService

import scala.concurrent.Future

trait MockUpScanService {

  val mockUpScanService: UpScanService = mock[UpScanService]

  object MockedUpScanService {
    def initiateNewJourney(response: Future[UpScanInitiateResponse]): OngoingStubbing[Future[UpScanInitiateResponse]] =
      when(mockUpScanService.initiateNewJourney()(any(), any()))
        .thenReturn(response)

    def initiateBulkJourney(response: Future[UpScanInitiateResponse]): OngoingStubbing[Future[UpScanInitiateResponse]] =
      when(mockUpScanService.initiateBulkJourney()(any(), any()))
        .thenReturn(response)

    def initiateAuthorityJourney(
      response: Future[UpScanInitiateResponse]
    ): OngoingStubbing[Future[UpScanInitiateResponse]] =
      when(mockUpScanService.initiateAuthorityJourney(any())(any(), any()))
        .thenReturn(response)

    def initiateSupportingDocJourney(
      response: Future[UpScanInitiateResponse]
    ): OngoingStubbing[Future[UpScanInitiateResponse]] =
      when(mockUpScanService.initiateSupportingDocJourney()(any(), any()))
        .thenReturn(response)

    def initiateCancelCaseJourney(
      response: Future[UpScanInitiateResponse]
    ): OngoingStubbing[Future[UpScanInitiateResponse]] =
      when(mockUpScanService.initiateCancelCaseJourney()(any(), any()))
        .thenReturn(response)

  }

}
