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
import forms.cancelCase.AnyOtherSupportingCancellationDocsFormProvider
import models.UserAnswers
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.CheckModePage
import pages.shared.{AnyOtherSupportingDocsPage, MoreDocumentationPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import repositories.SessionRepository
import views.html.cancelCase.AnyOtherSupportingCancellationDocsView

import scala.concurrent.Future

class AnyOtherSupportingCancellationDocsControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private lazy val anyOtherSupportingDocsView: AnyOtherSupportingCancellationDocsView =
    app.injector.instanceOf[AnyOtherSupportingCancellationDocsView]

  val userAnswers: Option[UserAnswers] = Some(
    UserAnswers("credId")
      .set(CheckModePage, false).success.value
  )

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

  val formProvider: AnyOtherSupportingCancellationDocsFormProvider =
    injector.instanceOf[AnyOtherSupportingCancellationDocsFormProvider]
  val form: AnyOtherSupportingCancellationDocsFormProvider = formProvider

  def controller(ua: Option[UserAnswers] = userAnswers): AnyOtherSupportingCancellationDocsController = {
    new AnyOtherSupportingCancellationDocsController(
      authenticatedAction,
      new FakeDataRetrievalAction(ua),
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      anyOtherSupportingDocsView,
      ec
    )
  }

  override def beforeEach(): Unit =
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

  "GET onLoad" should {
    "return OK" in {
      val result: Future[Result] = controller().onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(MoreDocumentationPage, true).success.value
      )
      val result: Future[Result] = controller(userAnswers).onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller().onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header when value has not changed" in {
        val ua: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value.set(AnyOtherSupportingDocsPage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller(ua).onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad().url
        )
      }

      "return the correct location header when value is set to true" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller().onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationController.onLoad().url
        )
      }

      "return the correct location header when value is set to false" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller().onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad().url
        )
      }

      "update the UserAnswers in session" in {
        val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller().onSubmit(request))
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in {
        val result: Future[Result] = controller().onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

  "backLink" when {

    "in change mode" should {
      "point to CYA page" in {
        val ua: UserAnswers   = UserAnswers("some-cred-id").set(CheckModePage, true).success.value
        lazy val result: Call = controller().backLink(dataRequest.copy(userAnswers = ua))
        result mustBe controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad()
      }
    }

    "not in change mode" should {
      "point to cancellation reason page" in {
        val ua: UserAnswers   = UserAnswers("some-cred-id").set(CheckModePage, false).success.value
        lazy val result: Call = controller().backLink(dataRequest.copy(userAnswers = ua))
        result mustBe controllers.cancelCase.routes.CancellationReasonController.onLoad()
      }
    }
  }

}
