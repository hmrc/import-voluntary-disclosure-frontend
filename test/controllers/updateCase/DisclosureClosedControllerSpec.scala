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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.updateCase.DisclosureClosedView

import scala.concurrent.Future

class DisclosureClosedControllerSpec extends ControllerSpecBase {

  trait Test {

    val mockSessionRepository: SessionRepository = mock[SessionRepository]

    when(mockSessionRepository.remove(any())(any())).thenReturn(Future.successful("OK"))

    val view = injector.instanceOf[DisclosureClosedView]

    lazy val controller = new DisclosureClosedController(
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

    "return Internal Server Error" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("cred-id"))
      val result: Future[Result]                    = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }

}
