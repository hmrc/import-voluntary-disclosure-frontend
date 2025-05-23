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
import models.UserAnswers
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.http.Status
import play.api.test.Helpers._
import views.html.updateCase.DisclosureNotFoundView

class DisclosureNotFoundControllerSpec extends ControllerSpecBase {
  val view: DisclosureNotFoundView = app.injector.instanceOf[DisclosureNotFoundView]
  val userAnswers: Option[UserAnswers] = Some(
    UserAnswers("credId").set(DisclosureReferenceNumberPage, "C18").success.value
  )
  private def controller(userAnswers: Option[UserAnswers] = userAnswers) = {

    val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    new DisclosureNotFoundController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      messagesControllerComponents,
      view,
      errorHandler
    )
  }

  "onLoad" should {
    "return 200" in {
      val result = controller().onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val result = controller().onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return Internal Server Error" in {
      val emptyAnswer = Some(UserAnswers("credId"))
      val result      = controller(emptyAnswer).onLoad()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }
}
