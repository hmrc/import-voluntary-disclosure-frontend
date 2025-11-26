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

package services

import assets.AddressLookupTestConstants._
import base.SpecBase
import connectors.AddressLookupConnector
import models.ErrorModel
import models.addressLookup.{AddressLookupOnRampModel, AddressModel}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

import scala.concurrent.Future

class AddressLookupServiceSpec extends SpecBase {

  type AddressLookupGetAddressResponse = Either[ErrorModel, AddressModel]
  type AddressLookupInitialiseResponse = Either[ErrorModel, AddressLookupOnRampModel]

  val mockAddressLookupConnector: AddressLookupConnector = mock[AddressLookupConnector]
  val service = new AddressLookupService(mockAddressLookupConnector, messagesApi, appConfig)

  "Calling .getAddress" must {

    "connector call is successful" when {
      when(mockAddressLookupConnector.getAddress(any())(any())).thenReturn(Future.successful(Right(customerAddressMax)))
      val result = service.retrieveAddress("12345")

      "return successful SubscriptionUpdateResponseModel" in {
        await(result) mustBe Right(customerAddressMax)
      }
    }
  }

  "Calling initialiseJourney" must {

    "connector call is successful" when {
      when(mockAddressLookupConnector.initialiseJourney(any())(any())).thenReturn(
        Future.successful(Right(AddressLookupOnRampModel("redirect-url")))
      )

      val result = service.initialiseJourney("Fast Food ltd")(hc, ec)

      "return successful SubscriptionUpdateResponseModel" in {
        await(result) mustBe Right(AddressLookupOnRampModel("redirect-url"))
      }
    }
  }

  "Calling initialiseImporterJourney" must {

    "connector call is successful" when {
      when(mockAddressLookupConnector.initialiseJourney(any())(any())).thenReturn(
        Future.successful(Right(AddressLookupOnRampModel("redirect-url")))
      )

      val result = service.initialiseImporterJourney("importer")(hc, ec)

      "return successful SubscriptionUpdateResponseModel" in {
        await(result) mustBe Right(AddressLookupOnRampModel("redirect-url"))
      }
    }
  }
}
