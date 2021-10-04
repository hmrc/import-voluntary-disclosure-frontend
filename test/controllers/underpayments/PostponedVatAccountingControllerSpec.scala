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

package controllers.underpayments

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.underpayments.PostponedVatAccountingFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.importDetails.UserType
import pages.importDetails.{ImporterNamePage, UserTypePage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.underpayments.PostponedVatAccountingView

import scala.concurrent.Future
import pages.importDetails.NumberOfEntriesPage
import models.importDetails.NumberOfEntries

class PostponedVatAccountingControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: PostponedVatAccountingView =
      app.injector.instanceOf[PostponedVatAccountingView]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(ImporterNamePage, "importerName")
        .success.value
        .set(UserTypePage, UserType.Representative)
        .success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: PostponedVatAccountingFormProvider = injector.instanceOf[PostponedVatAccountingFormProvider]

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new PostponedVatAccountingController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      formProvider,
      view,
      errorHandler,
      ec
    )
  }

  "GET PostponedVatAccounting Page" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST PostponedVatAccounting Page" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header when value is set to true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.underpayments.routes.PostponedVatAccountingController.onLoad().url
        )
      }

      "return the correct location header when value is set to false and we're not in bulk flow" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(ImporterNamePage, "importerName")
            .success.value
            .set(UserTypePage, UserType.Representative)
            .success.value
            .set(NumberOfEntriesPage, NumberOfEntries.OneEntry)
            .success.value
        )

        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.reasons.routes.BoxGuidanceController.onLoad().url)
      }

      "return the correct location header when value is set to false and we're in bulk flow" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(ImporterNamePage, "importerName")
            .success.value
            .set(UserTypePage, UserType.Representative)
            .success.value
            .set(NumberOfEntriesPage, NumberOfEntries.MoreThanOneEntry)
            .success.value
        )

        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.BulkUploadFileController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

}
