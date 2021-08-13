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

package controllers.serviceEntry

import base.SpecBase
import forms.serviceEntry.IndividualContinueWithCredentialsFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.serviceEntry.IndividualContinueWithCredentialsPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import views.html.serviceEntry.IndividualContinueWithCredentialsView

import scala.concurrent.Future

class IndividualContinueWithCredentialsControllerSpec extends SpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: IndividualContinueWithCredentialsView = app.injector.instanceOf[IndividualContinueWithCredentialsView]

    val formProvider: IndividualContinueWithCredentialsFormProvider = injector.instanceOf[IndividualContinueWithCredentialsFormProvider]

    MockedSessionRepository.set(Future.successful(true))
    MockedSessionRepository.get(Future.successful(Some(UserAnswers("credId"))))

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

    lazy val controller = new IndividualContinueWithCredentialsController(
      mockSessionRepository, messagesControllerComponents, view, formProvider, appConfig, errorHandler, ec
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
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
          .set(IndividualContinueWithCredentialsPage, true).success.value)
        val result: Future[Result] = controller.onLoad()(fakeRequest)
        status(result) mustBe Status.OK
      }

    }

  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "redirect on Yes to ecc subscribe page" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "true"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(appConfig.eccSubscribeUrl)
      }

      "redirect on No to you need to contact the team directly page" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "false"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.SignOutController.signOut().url)
      }

    }

    "payload contains invalid data" should {

      "return a bad request when no data is sent" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody()
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
