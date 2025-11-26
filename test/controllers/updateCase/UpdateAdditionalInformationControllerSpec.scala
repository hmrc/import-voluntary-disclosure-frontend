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

package controllers.updateCase

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.updateCase.UpdateAdditionalInformationFormProvider
import models.UserAnswers
import models.requests._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.CheckModePage
import pages.shared.MoreDocumentationPage
import pages.updateCase.UpdateAdditionalInformationPage
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.updateCase.UpdateAdditionalInformationView

import scala.concurrent.Future

class UpdateAdditionalInformationControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private lazy val view: UpdateAdditionalInformationView = app.injector.instanceOf[UpdateAdditionalInformationView]

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

  val formProvider: UpdateAdditionalInformationFormProvider =
    injector.instanceOf[UpdateAdditionalInformationFormProvider]
  val form: UpdateAdditionalInformationFormProvider = formProvider

  def controller(ua: Option[UserAnswers] = userAnswers): UpdateAdditionalInformationController = {
    new UpdateAdditionalInformationController(
      authenticatedAction,
      new FakeDataRetrievalAction(ua),
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      view,
      ec
    )
  }

  override protected def beforeEach(): Unit =
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

  "GET onLoad" should {
    "return OK" in {
      val result: Future[Result] = controller().onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val ua: Option[UserAnswers] =
        Some(UserAnswers("some-cred-id").set(UpdateAdditionalInformationPage, "some text").success.value)
      val result: Future[Result] = controller(ua).onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data when check mode is false" should {

      "return a SEE OTHER response" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> "some text")
        lazy val result: Future[Result] = controller().onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header for the response" in {
        val ua: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(UpdateAdditionalInformationPage, "some text").success.value
            .set(CheckModePage, false).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> "some text")
        lazy val result: Future[Result] = controller(ua).onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.updateCase.routes.UpdateCaseCheckYourAnswersController.onLoad().url
        )
      }

      "update the UserAnswers in session" in {
        val request = fakeRequest.withFormUrlEncodedBody("value" -> "some text")
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

    "not in change mode and user selected yes on More Documentation page" should {
      "point to upload summary page" in {
        val ua: UserAnswers =
          UserAnswers("some-cred-id")
            .set(CheckModePage, false).success.value
            .set(MoreDocumentationPage, true).success.value
        lazy val result: Option[Call] = controller().backLink(dataRequest.copy(userAnswers = ua))
        result mustBe Some(controllers.updateCase.routes.UploadSupportingDocumentationSummaryController.onLoad())

      }
    }

    "not in change mode and user selected no on More Documentation page" should {
      "point to upload summary page" in {
        val ua: UserAnswers =
          UserAnswers("some-cred-id")
            .set(CheckModePage, false).success.value
            .set(MoreDocumentationPage, false).success.value
        lazy val result: Option[Call] = controller().backLink(dataRequest.copy(userAnswers = ua))
        result mustBe Some(controllers.updateCase.routes.MoreDocumentationController.onLoad())

      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in {
        val ua: UserAnswers =
          UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
        lazy val result: Option[Call] = controller().backLink(dataRequest.copy(userAnswers = ua))
        result mustBe Some(controllers.updateCase.routes.UpdateCaseCheckYourAnswersController.onLoad())
      }
    }

  }

}
