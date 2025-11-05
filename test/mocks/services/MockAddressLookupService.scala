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

import models.ErrorModel
import models.addressLookup.{AddressLookupOnRampModel, AddressModel}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import services.AddressLookupService

import scala.concurrent.Future

trait MockAddressLookupService {

  val mockAddressLookupService: AddressLookupService = mock[AddressLookupService]

  type RetrieveAddressResponse   = Either[ErrorModel, AddressModel]
  type InitialiseJourneyResponse = Either[ErrorModel, AddressLookupOnRampModel]

  def setupMockRetrieveAddress(response: RetrieveAddressResponse): Unit =
    when(mockAddressLookupService.retrieveAddress(any())(any(), any()))
      .thenReturn(Future.successful(response))

  def setupMockInitialiseJourney(response: InitialiseJourneyResponse): Unit =
    when(mockAddressLookupService.initialiseJourney(any())(any(), any()))
      .thenReturn(Future.successful(response))

  def setupMockInitialiseImporterJourney(response: InitialiseJourneyResponse): Unit =
    when(mockAddressLookupService.initialiseImporterJourney(any())(any(), any()))
      .thenReturn(Future.successful(response))
}
