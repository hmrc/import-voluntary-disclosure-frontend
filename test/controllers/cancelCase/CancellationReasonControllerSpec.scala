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

package controllers.cancelCase

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.cancelCase.CancellationReasonFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.requests._
import pages.CheckModePage
import pages.updateCase.UpdateAdditionalInformationPage
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.cancelCase.CancellationReasonView

import scala.concurrent.Future

class CancellationReasonControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: CancellationReasonView = app.injector.instanceOf[CancellationReasonView]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(CheckModePage, false).success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

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

    val formProvider: CancellationReasonFormProvider = injector.instanceOf[CancellationReasonFormProvider]

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new CancellationReasonController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      formProvider,
      view,
      ec
    )
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(UserAnswers("some-cred-id").set(UpdateAdditionalInformationPage, "some text").success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data when check mode is false" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> "some text")
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "rediret to CYA" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(UpdateAdditionalInformationPage, "some text").success.value
            .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> "some text")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad().url
        )
      }

      "return the correct location header for the response" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(UpdateAdditionalInformationPage, "some text").success.value
            .set(CheckModePage, false).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> "some text")
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.AnyOtherSupportingCancellationDocsController.onLoad().url
        )
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "some text")
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

  "backLink" when {
    "not in change mode" should {
      "point to Disclosure Referencer Number Page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id").set(CheckModePage, false).success.value)
        lazy val result: Option[Call] = controller.backLink
        result mustBe Some(controllers.cancelCase.routes.CancelCaseReferenceNumberController.onLoad())

      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id").set(CheckModePage, true).success.value)
        lazy val result: Option[Call] = controller.backLink
        result mustBe Some(controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad())
      }
    }

  }

}
