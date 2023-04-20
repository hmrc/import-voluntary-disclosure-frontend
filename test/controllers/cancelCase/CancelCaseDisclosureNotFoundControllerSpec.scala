/*
 * Copyright 2023 HM Revenue & Customs
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
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.http.Status
import play.api.test.Helpers._
import views.html.cancelCase.CancelCaseDisclosureNotFoundView
import scala.concurrent.Future

class CancelCaseDisclosureNotFoundControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {

    MockedSessionRepository.remove(Future.successful("OK"))

    val view: CancelCaseDisclosureNotFoundView = app.injector.instanceOf[CancelCaseDisclosureNotFoundView]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId").set(DisclosureReferenceNumberPage, "C18").success.value
    )

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new CancelCaseDisclosureNotFoundController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      messagesControllerComponents,
      view,
      errorHandler
    )
  }

  "onLoad" should {
    "return 200" in new Test {
      val result = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return Internal Server Error (ISE) when failed to find caseId" in new Test {

      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
      val result                                    = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }
}
