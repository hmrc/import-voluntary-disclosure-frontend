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
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.cancelCase.CancelCaseDisclosureClosedView
import scala.concurrent.Future

class CancelCaseDisclosureClosedControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {

    MockedSessionRepository.remove(Future.successful("OK"))

    val view = injector.instanceOf[CancelCaseDisclosureClosedView]

    lazy val controller = new CancelCaseDisclosureClosedController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      messagesControllerComponents,
      mockSessionRepository,
      view,
      errorHandler,
      ec
    )

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("some-cred-id").set(DisclosureReferenceNumberPage, "C182107152124AQYVM6E34").success.value
    )

  }

  "GET onLoad" should {
    "return 200" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return Internal Server Error (ISE) when failed to find caseId" in new Test {

      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
      val result: Future[Result]                    = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }
}
