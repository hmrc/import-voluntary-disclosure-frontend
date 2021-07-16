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

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.TraderAddressCorrectFormProvider
import mocks.repositories.MockSessionRepository
import mocks.services.MockEoriDetailsService
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.{ErrorModel, UserAnswers}
import pages.serviceEntry.KnownEoriDetailsPage
import pages.{CheckModePage, TraderAddressCorrectPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.ReusableValues
import views.html.TraderAddressCorrectView

import scala.concurrent.Future

class TraderAddressCorrectControllerSpec extends ControllerSpecBase with MockEoriDetailsService with ReusableValues {

  trait Test extends MockSessionRepository {
    lazy val controller = new TraderAddressCorrectController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      mockEoriDetailsService,
      errorHandler,
      messagesControllerComponents,
      form,
      traderAddressCorrectView,
      ec
    )

    private lazy val traderAddressCorrectView: TraderAddressCorrectView = app.injector.instanceOf[TraderAddressCorrectView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    val formProvider: TraderAddressCorrectFormProvider = injector.instanceOf[TraderAddressCorrectFormProvider]
    val form: TraderAddressCorrectFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    implicit lazy val dataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(
      OptionalDataRequest(
        IdentifierRequest(fakeRequest, "credId", "eori"),
        "credId",
        "eori",
        userAnswers
      ),
      "credId",
      "eori",
      userAnswers.get
    )

    val importerAddressYes: Boolean = true

  }

  "GET onLoad" should {
    "return OK" in new Test {
      setupMockRetrieveAddress(Right(eoriDetails))
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return error model" in new Test {
      setupMockRetrieveAddress(Left(ErrorModel(404, "")))
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.NOT_FOUND
    }

    "return HTML" in new Test {
      setupMockRetrieveAddress(Right(eoriDetails))
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id").set(TraderAddressCorrectPage, importerAddressYes).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "redirect to the deferment page if choosing to use the known address and checkMode is false" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(KnownEoriDetailsPage, eoriDetails).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.DefermentController.onLoad().url)
      }

      "redirect to the check your answers page if choosing to use the known address and checkMode is true" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(KnownEoriDetailsPage, eoriDetails).success.value
            .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "handoff to the address lookup frontend if choosing to use a different address" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(KnownEoriDetailsPage, eoriDetails).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AddressLookupController.initialiseJourney().url)
      }

      "update the UserAnswers in session when Trader Address is correct" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(KnownEoriDetailsPage, eoriDetails).success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
        verifyCalls()
      }

      "update the UserAnswers in session Trader Address is incorrect" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(KnownEoriDetailsPage, eoriDetails).success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        await(controller.onSubmit(request))
        verifyCalls()
      }

      "payload contains invalid data" should {
        "return a BAD REQUEST" in new Test {
          override val userAnswers: Option[UserAnswers] = Some(
            UserAnswers("some-cred-id").set(KnownEoriDetailsPage, eoriDetails).success.value
          )
          val result: Future[Result] = controller.onSubmit(fakeRequest)
          status(result) mustBe Status.BAD_REQUEST
        }
      }
    }
  }

  "backLink" when {

    "not in change mode" should {
      "when loading page back button should take you to declarant contact details page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.routes.DeclarantContactDetailsController.onLoad()
      }
    }

    "in change mode" should {
      "when loading page back button should take you to Check your answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }

  }
}
