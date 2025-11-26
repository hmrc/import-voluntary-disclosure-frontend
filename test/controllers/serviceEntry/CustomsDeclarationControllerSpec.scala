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

package controllers.serviceEntry

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.serviceEntry.CustomsDeclarationFormProvider
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.serviceEntry.CustomsDeclarationPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import repositories.SessionRepository
import views.html.serviceEntry.CustomsDeclarationView

import scala.concurrent.Future

class CustomsDeclarationControllerSpec extends ControllerSpecBase {

  trait Test {
    private lazy val view: CustomsDeclarationView = app.injector.instanceOf[CustomsDeclarationView]

    val formProvider: CustomsDeclarationFormProvider = injector.instanceOf[CustomsDeclarationFormProvider]

    val mockSessionRepository: SessionRepository = mock[SessionRepository]
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
    when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(Some(UserAnswers("credId"))))

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new CustomsDeclarationController(
      privateIndividualAuthAction,
      dataRetrievalAction,
      mockSessionRepository,
      messagesControllerComponents,
      formProvider,
      view,
      errorHandler,
      ec
    )
  }

  "GET onLoad" when {

    "userAnswers doesn't exist" should {
      "return OK" in new Test {
        val result: Future[Result] = controller.onLoad()(fakeRequest)
        status(result) mustBe Status.OK
      }

    }

    "userAnswers exist" should {
      "return OK" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(CustomsDeclarationPage, true).success.value
        )
        val result: Future[Result] = controller.onLoad()(fakeRequest)
        status(result) mustBe Status.OK
      }

    }

  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "redirect on Yes to continue with current credentials page" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "true"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.serviceEntry.routes.IndividualContinueWithCredentialsController.onLoad().url
        )
      }

      "redirect on No to you need to contact the team directly page" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "false"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.serviceEntry.routes.IndividualHandoffController.onLoad().url)
      }

    }

    "payload contains invalid data" should {

      "return a bad request when no data is sent" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody()
        lazy val result: Future[Result]                      = controller.onSubmit()(request)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
