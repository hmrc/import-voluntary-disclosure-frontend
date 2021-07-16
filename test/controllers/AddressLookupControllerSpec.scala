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

import assets.AddressLookupTestConstants.{customerAddressMax, customerAddressMissingLine3}
import assets.BaseTestConstants.errorModel
import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import mocks.services.MockAddressLookupService
import models.UserAnswers
import models.addressLookup.AddressLookupOnRampModel
import models.importDetails.UserType
import pages.CheckModePage
import pages.importDetails.UserTypePage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{redirectLocation, _}

import scala.concurrent.Future

class AddressLookupControllerSpec extends ControllerSpecBase {

  trait Test extends MockAddressLookupService with MockSessionRepository {
    lazy val dataRetrievalAction = new FakeDataRetrievalAction(Some(UserAnswers("some-cred-id")))

    lazy val controller = new AddressLookupController(
        authenticatedAction,
        dataRetrievalAction,
        dataRequiredAction,
        mockSessionRepository,
        mockAddressLookupService,
        errorHandler,
        messagesControllerComponents,
        appConfig,
        ec)
  }

  "Calling .callback" must {

    "correctly redirect if the address lookup service returns success" when {

      "for an Individual with full address" should {
        "redirect the user to the deferment page if checkMode is false" in new Test {
          MockedSessionRepository.set(Future.successful(true))
          setupMockRetrieveAddress(Right(customerAddressMax))
          val result: Future[Result] = controller.callback("12345")(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.routes.DefermentController.onLoad.url)
          verifyCalls()
        }

        "redirect the user to the check your answers page if checkMode is true" in new Test {
          override lazy val dataRetrievalAction = new FakeDataRetrievalAction(
            Some(UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
            )
          )
          MockedSessionRepository.set(Future.successful(true))
          setupMockRetrieveAddress(Right(customerAddressMax))
          val result: Future[Result] = controller.callback("12345")(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad.url)
          verifyCalls()
        }
      }
    }

    "produce error if business address lookup service returns an error" should {
      "return InternalServerError" in new Test {
        setupMockRetrieveAddress(Left(errorModel))
        val result: Future[Result] = controller.callback("12345")(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR

      }
    }
  }

  "Calling .importerCallback" must {

    "correctly redirect if the address lookup service returns success" when {

      "for a Representative entering partial address for Importer" should {
        "redirect the user to the deferment page" in new Test {
          override lazy val dataRetrievalAction = new FakeDataRetrievalAction(
            Some(UserAnswers("some-cred-id")
              .set(UserTypePage, UserType.Representative).success.value
            )
          )
          MockedSessionRepository.set(Future.successful(true))
          setupMockRetrieveAddress(Right(customerAddressMissingLine3))
          val result: Future[Result] = controller.importerCallback("12345")(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.importDetails.routes.ImporterEORIExistsController.onLoad.url)
          verifyCalls()
        }

        "redirect the user to the check your answer page in check mode" in new Test {
          override lazy val dataRetrievalAction = new FakeDataRetrievalAction(
            Some(UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
              .set(UserTypePage, UserType.Representative).success.value
            )
          )
          MockedSessionRepository.set(Future.successful(true))
          setupMockRetrieveAddress(Right(customerAddressMissingLine3))
          val result: Future[Result] = controller.importerCallback("12345")(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad.url)
          verifyCalls()
        }
      }
    }

    "produce error if business address lookup service returns an error" should {
      "return InternalServerError" in new Test {
        setupMockRetrieveAddress(Left(errorModel))
        val result: Future[Result] = controller.importerCallback("12345")(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR

      }
    }
  }

  "Calling .initialiseJourney" when {

    "address lookup service returns success" when {

        "return redirect to the url returned" in new Test {
          setupMockInitialiseJourney(Right(AddressLookupOnRampModel("redirect-url")))
          val result: Future[Result] = controller.initialiseJourney()(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
        }

        "redirect to url returned" in new Test {
          setupMockInitialiseJourney(Right(AddressLookupOnRampModel("redirect-url")))
          val result: Future[Result] = controller.initialiseJourney()(fakeRequest)
          redirectLocation(result) mustBe Some("redirect-url")
        }
    }

    "address lookup service returns an error" should {

      "return InternalServerError" in new Test {
        setupMockInitialiseJourney(Left(errorModel))
        val result: Future[Result] = controller.initialiseJourney()(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "Calling .initialiseImporterJourney" when {

    "address lookup service returns success" when {

        "return redirect to the url returned" in new Test {
          setupMockInitialiseImporterJourney(Right(AddressLookupOnRampModel("redirect-url")))
          val result: Future[Result] = controller.initialiseImporterJourney()(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
        }

        "redirect to url returned" in new Test {
          setupMockInitialiseImporterJourney(Right(AddressLookupOnRampModel("redirect-url")))
          val result: Future[Result] = controller.initialiseImporterJourney()(fakeRequest)
          redirectLocation(result) mustBe Some("redirect-url")
        }
    }

    "address lookup service returns an error" should {

      "return InternalServerError" in new Test {
        setupMockInitialiseImporterJourney(Left(errorModel))
        val result: Future[Result] = controller.initialiseImporterJourney()(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
