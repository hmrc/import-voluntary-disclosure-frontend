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
import forms.UnderpaymentTypeFormProvider
import mocks.repositories.MockSessionRepository
import models.{UnderpaymentType, UserAnswers}
import pages.UnderpaymentTypePage
import play.api.http.Status
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.UnderpaymentTypeView

import scala.concurrent.Future

class UnderpaymentTypeControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    lazy val controller = new UnderpaymentTypeController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      underpaymentTypeView,
      form
    )
    private lazy val underpaymentTypeView = app.injector.instanceOf[UnderpaymentTypeView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: UnderpaymentTypeFormProvider = injector.instanceOf[UnderpaymentTypeFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: UnderpaymentTypeFormProvider = formProvider
  }

  "GET /" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Option(
        UserAnswers("some-cred-id").set(
          UnderpaymentTypePage,
          UnderpaymentType(false, false, false)
        ).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

}
