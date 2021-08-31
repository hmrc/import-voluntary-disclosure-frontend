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

package controllers.cancelCase

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import mocks.services.MockUpdateCaseService
import models._
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.cancelCase.{CancelCaseCheckYourAnswersView, CancelCaseConfirmationView}

import scala.concurrent.Future

class CancelCaseCheckYourAnswersControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository with MockUpdateCaseService {

    private lazy val cancelCaseCheckYourAnswersView: CancelCaseCheckYourAnswersView = app.injector.instanceOf[CancelCaseCheckYourAnswersView]
    private lazy val cancelCaseConfirmationView: CancelCaseConfirmationView = app.injector.instanceOf[CancelCaseConfirmationView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    def repositoryExpectation(): Unit = {
      MockedSessionRepository.set(Future.successful(true))
    }

    lazy val controller = new CancelCaseCheckYourAnswersController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      messagesControllerComponents,
      mockSessionRepository,
      cancelCaseCheckYourAnswersView,
      cancelCaseConfirmationView,
      errorHandler,
      ec
    )

    repositoryExpectation()
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "GET onSubmit" should {
    "return Redirect to the confirmation view" in new Test {
      override def repositoryExpectation(): Unit = {
        MockedSessionRepository.remove(Future.successful("some-cred-id"))
      }

      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
        .set(DisclosureReferenceNumberPage, "C181234567890123456789").success.value
      )
      val result: Future[Result] = controller.onSubmit()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return Internal Server error when user answers incomplete for confirmation view" in new Test {
      override def repositoryExpectation(): Unit = {
        MockedSessionRepository.remove(Future.successful("some-cred-id"))
      }

      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
      val result: Future[Result] = controller.onSubmit()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }
}
