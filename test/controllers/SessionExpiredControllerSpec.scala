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
import forms.AcceptanceDateFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.errors.SessionTimeoutView

import scala.concurrent.Future


class SessionExpiredControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val timoutPageView: SessionTimeoutView = app.injector.instanceOf[SessionTimeoutView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

    val formProvider: AcceptanceDateFormProvider = injector.instanceOf[AcceptanceDateFormProvider]
    val form: AcceptanceDateFormProvider = formProvider

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

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new SessionExpiredController(messagesControllerComponents, appConfig, timoutPageView)
  }

  "GET keepAlive" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.keepAlive()(fakeRequest)
      status(result) mustBe Status.NO_CONTENT
    }
  }

  "GET timeout" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.timeout()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.timeout()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

}
