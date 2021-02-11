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
import forms.BoxNumberFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.UnderpaymentReasonBoxNumberPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.BoxNumberView

import scala.concurrent.Future

class BoxNumberControllerSpec extends ControllerSpecBase {

  val underpaymentReasonBoxNumber: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
    .set(
      UnderpaymentReasonBoxNumberPage,
      5
    ).success.value
  )

  private def fakeRequestGenerator(value: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "value" -> value.toString
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new BoxNumberController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      boxNumberView
    )
    private lazy val boxNumberView = app.injector.instanceOf[BoxNumberView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: BoxNumberFormProvider = injector.instanceOf[BoxNumberFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: BoxNumberFormProvider = formProvider
  }

  "GET /" when {

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

  "POST /" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = underpaymentReasonBoxNumber
        lazy val result: Future[Result] = controller.onSubmit(
          fakeRequestGenerator("5")
        )
        status(result) mustBe Status.SEE_OTHER
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = underpaymentReasonBoxNumber
        await(controller.onSubmit(fakeRequestGenerator("5")))
        verifyCalls()
      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST when no value is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = underpaymentReasonBoxNumber
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator(""))
        status(result) mustBe Status.BAD_REQUEST
      }

    }

  }

}
