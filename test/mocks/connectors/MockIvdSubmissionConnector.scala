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

package mocks.connectors

import connectors.IvdSubmissionConnector
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock

import scala.concurrent.Future

trait MockIvdSubmissionConnector {

  val mockIVDSubmissionConnector: IvdSubmissionConnector = mock[IvdSubmissionConnector]

  type EoriDetailsResponse = Either[ErrorModel, EoriDetails]

  def setupMockGetEoriDetails(response: Either[ErrorModel, EoriDetails]): Unit =
    when(mockIVDSubmissionConnector.getEoriDetails(any())(any()))
      .thenReturn(Future.successful(response))

  def setupMockCreateCase(response: Either[ErrorModel, SubmissionResponse]): Unit =
    when(mockIVDSubmissionConnector.createCase(any())(any()))
      .thenReturn(Future.successful(response))

  def setupMockUpdateCase(response: Either[UpdateCaseError, UpdateCaseResponse]): Unit =
    when(mockIVDSubmissionConnector.updateCase(any())(any()))
      .thenReturn(Future.successful(response))
}
