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
import forms.ImporterAddressFormProvider
import mocks.repositories.MockSessionRepository
import mocks.services.MockImporterAddressService
import models.{ErrorModel, UserAnswers}
import pages.{ImporterAddressPage, ImporterAddressTemporaryPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.ReusableValues
import views.html.ImporterAddressView

import scala.concurrent.Future

class ImporterAddressControllerSpec extends ControllerSpecBase with MockImporterAddressService with ReusableValues {

  trait Test extends MockSessionRepository {
    lazy val controller = new ImporterAddressController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      mockImporterAddressService,
      errorHandler,
      messagesControllerComponents,
      form,
      importerAddressView)

    private lazy val importerAddressView: ImporterAddressView = app.injector.instanceOf[ImporterAddressView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    val formProvider: ImporterAddressFormProvider = injector.instanceOf[ImporterAddressFormProvider]
    val form: ImporterAddressFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    val importerAddressYes: Boolean = true

  }

  "GET /" should {
    "return OK" in new Test {
      setupMockRetrieveAddress(Right(traderAddress))
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return error model" in new Test {
      setupMockRetrieveAddress(Left(ErrorModel(404, "")))
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.NOT_FOUND
    }

    "return HTML" in new Test {
      setupMockRetrieveAddress(Right(traderAddress))
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(ImporterAddressPage, importerAddressYes).success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST /" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(ImporterAddressTemporaryPage, traderAddress).success.value)
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "update the UserAnswers in session when Trader Address is correct" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(ImporterAddressTemporaryPage, traderAddress).success.value)
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
        MockedSessionRepository.verifyCalls()
      }

      "update the UserAnswers in session Trader Address is incorrect" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(ImporterAddressTemporaryPage, traderAddress).success.value)
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        await(controller.onSubmit(request))
        MockedSessionRepository.verifyCalls()
      }

      "payload contains invalid data" should {
        "return a BAD REQUEST" in new Test {
          override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(ImporterAddressTemporaryPage, traderAddress).success.value)
          val result: Future[Result] = controller.onSubmit(fakeRequest)
          status(result) mustBe Status.BAD_REQUEST
        }
      }
    }
  }
}
