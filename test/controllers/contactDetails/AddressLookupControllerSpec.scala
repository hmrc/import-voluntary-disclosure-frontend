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

package controllers.contactDetails

import assets.AddressLookupTestConstants.{customerAddressMax, customerAddressMissingLine3}
import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import models.addressLookup.{AddressLookupOnRampModel, AddressModel}
import models.importDetails.UserType
import models.{ErrorModel, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.{ArgumentMatchers, Mockito}
import pages.CheckModePage
import pages.importDetails.{ImporterNamePage, UserTypePage}
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{redirectLocation, *}
import repositories.SessionRepository
import services.AddressLookupService
import utils.ReusableValues

import scala.concurrent.Future

class AddressLookupControllerSpec extends ControllerSpecBase with ReusableValues {

  type RetrieveAddressResponse   = Either[ErrorModel, AddressModel]
  type InitialiseJourneyResponse = Either[ErrorModel, AddressLookupOnRampModel]

  trait Test {
    val mockSessionRepository: SessionRepository       = mock[SessionRepository]
    val mockAddressLookupService: AddressLookupService = mock[AddressLookupService]

    lazy val dataRetrievalAction = new FakeDataRetrievalAction(
      Some(
        UserAnswers("some-cred-id")
          .set(KnownEoriDetailsPage, eoriDetails).success.value
          .set(UserTypePage, UserType.Representative).success.value
          .set(ImporterNamePage, "importer").success.value
      )
    )

    lazy val controller = new AddressLookupController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      mockAddressLookupService,
      errorHandler,
      messagesControllerComponents,
      ec
    )
  }

  val addressLookupErrorModel: ErrorModel = ErrorModel(Status.INTERNAL_SERVER_ERROR, "Some Error, oh no!")

  "Calling .callback" must {

    "correctly redirect if the address lookup service returns success" when {

      "for an Individual with full address" should {
        "redirect the user to the deferment page if checkMode is false" in new Test {
          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
          when(mockAddressLookupService.retrieveAddress(any())(any(), any())).thenReturn(
            Future.successful(Right(customerAddressMax))
          )
          val result: Future[Result] = controller.callback("12345")(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.paymentInfo.routes.DefermentController.onLoad().url)
        }

        "redirect the user to the check your answers page if checkMode is true" in new Test {
          override lazy val dataRetrievalAction = new FakeDataRetrievalAction(
            Some(
              UserAnswers("some-cred-id")
                .set(CheckModePage, true).success.value
            )
          )
          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
          when(mockAddressLookupService.retrieveAddress(any())(any(), any())).thenReturn(
            Future.successful(Right(customerAddressMax))
          )
          val result: Future[Result] = controller.callback("12345")(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
        }
      }
    }

    "produce error if business address lookup service returns an error" should {
      "return InternalServerError" in new Test {
        when(mockAddressLookupService.retrieveAddress(any())(any(), any())).thenReturn(
          Future.successful(Left(addressLookupErrorModel))
        )
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
            Some(
              UserAnswers("some-cred-id")
                .set(UserTypePage, UserType.Representative).success.value
            )
          )
          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
          when(mockAddressLookupService.retrieveAddress(any())(any(), any())).thenReturn(
            Future.successful(Right(customerAddressMissingLine3))
          )
          val result: Future[Result] = controller.importerCallback("12345")(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
          redirectLocation(result) mustBe Some(
            controllers.importDetails.routes.ImporterEORIExistsController.onLoad().url
          )
        }

        "redirect the user to the check your answer page in check mode" in new Test {
          override lazy val dataRetrievalAction = new FakeDataRetrievalAction(
            Some(
              UserAnswers("some-cred-id")
                .set(CheckModePage, true).success.value
                .set(UserTypePage, UserType.Representative).success.value
            )
          )
          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
          when(mockAddressLookupService.retrieveAddress(any())(any(), any())).thenReturn(
            Future.successful(Right(customerAddressMissingLine3))
          )
          val result: Future[Result] = controller.importerCallback("12345")(fakeRequest)
          status(result) mustBe Status.SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
        }
      }
    }

    "produce error if business address lookup service returns an error" should {
      "return InternalServerError" in new Test {
        when(mockAddressLookupService.retrieveAddress(any())(any(), any())).thenReturn(
          Future.successful(Left(addressLookupErrorModel))
        )
        val result: Future[Result] = controller.importerCallback("12345")(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR

      }
    }
  }

  "Calling .initialiseJourney" when {

    "address lookup service returns success" when {

      "return redirect to the url returned" in new Test {
        when(mockAddressLookupService.initialiseJourney(any())(any(), any())).thenReturn(
          Future.successful(Right(AddressLookupOnRampModel("redirect-url")))
        )
        val result: Future[Result] = controller.initialiseJourney()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
      }

      "redirect to url returned" in new Test {
        when(mockAddressLookupService.initialiseJourney(any())(any(), any())).thenReturn(
          Future.successful(Right(AddressLookupOnRampModel("redirect-url")))
        )
        val result: Future[Result] = controller.initialiseJourney()(fakeRequest)
        redirectLocation(result) mustBe Some("redirect-url")
      }
    }

    "address lookup service returns an error" should {

      "return InternalServerError" in new Test {
        when(mockAddressLookupService.initialiseJourney(any())(any(), any())).thenReturn(
          Future.successful(Left(addressLookupErrorModel))
        )
        val result: Future[Result] = controller.initialiseJourney()(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "Calling .initialiseImporterJourney" when {

    "address lookup service returns success" when {

      "return redirect to the url returned" in new Test {
        when(mockAddressLookupService.initialiseImporterJourney(any())(any(), any()))
          .thenReturn(Future.successful(Right(AddressLookupOnRampModel("redirect-url"))))
        val result: Future[Result] = controller.initialiseImporterJourney()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
      }

      "redirect to url returned" in new Test {
        when(mockAddressLookupService.initialiseImporterJourney(any())(any(), any()))
          .thenReturn(Future.successful(Right(AddressLookupOnRampModel("redirect-url"))))
        val result: Future[Result] = controller.initialiseImporterJourney()(fakeRequest)
        redirectLocation(result) mustBe Some("redirect-url")
      }
    }

    "address lookup service returns an error" should {

      "return InternalServerError" in new Test {
        when(mockAddressLookupService.initialiseImporterJourney(any())(any(), any()))
          .thenReturn(Future.successful(Left(addressLookupErrorModel)))
        val result: Future[Result] = controller.initialiseImporterJourney()(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
