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

package controllers.errors

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.errors.InformationCannotBeAddedView

import scala.concurrent.Future

class InformationCannotBeAddedControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {

    MockedSessionRepository.remove(Future.successful("OK"))

    val view = injector.instanceOf[InformationCannotBeAddedView]

    lazy val controller = new InformationCannotBeAddedController(authenticatedAction, dataRetrievalAction,
      dataRequiredAction, messagesControllerComponents, mockSessionRepository, view, appConfig, ec)

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

    val caseId: String = "C181234567891234567891"

  }

  "GET onLoad" should {
    "return 200" in new Test {
      val result: Future[Result] = controller.onLoad(caseId)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(caseId)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

}
