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
import forms.WhatDoYouWantToDoFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import pages.WhatDoYouWantToDoPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.WhatDoYouWantToDoView

import scala.concurrent.Future

class WhatDoYouWantToDoControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: WhatDoYouWantToDoView = app.injector.instanceOf[WhatDoYouWantToDoView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

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

    val formProvider: WhatDoYouWantToDoFormProvider = injector.instanceOf[WhatDoYouWantToDoFormProvider]
    val form: WhatDoYouWantToDoFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new WhatDoYouWantToDoController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, form, view)
  }

  "GET onLoad" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
        .set(WhatDoYouWantToDoPage, true).success.value)
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER then current saved value is true and submitted is true" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(WhatDoYouWantToDoPage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "true"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UserTypeController.onLoad().url)
      }

      "return a SEE OTHER when there's no current saved value and submitted is false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "false"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.WhatDoYouWantToDoController.onLoad().url)
      }

      "return a SEE OTHER when there's current saved value is true and submitted is false" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(WhatDoYouWantToDoPage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "false"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.WhatDoYouWantToDoController.onLoad().url)
      }

    }

    "payload contains valid data" should {

      "return a bad request when no data is sent" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody()
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
