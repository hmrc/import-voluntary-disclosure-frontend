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

package controllers.cancelCase

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.http.Status
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.cancelCase.CancelCaseDisclosureNotFoundView

import scala.concurrent.Future

class CancelCaseDisclosureNotFoundControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  val view: CancelCaseDisclosureNotFoundView = app.injector.instanceOf[CancelCaseDisclosureNotFoundView]

  val userAnswers: Option[UserAnswers] = Some(
    UserAnswers("credId").set(DisclosureReferenceNumberPage, "C18").success.value
  )

  def controller(ua: Option[UserAnswers] = userAnswers): CancelCaseDisclosureNotFoundController = {
    new CancelCaseDisclosureNotFoundController(
      authenticatedAction,
      new FakeDataRetrievalAction(ua),
      dataRequiredAction,
      messagesControllerComponents,
      view,
      errorHandler
    )
  }

  override def beforeEach(): Unit =
    when(mockSessionRepository.remove(any())(any())).thenReturn(Future.successful("OK"))

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

    "return Internal Server Error (ISE) when failed to find caseId" in {
      val ua: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
      val result                  = controller(ua).onLoad()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }
}
