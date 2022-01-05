/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.serviceEntry

import base.ControllerSpecBase
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import views.html.serviceEntry.IndividualHandoffView

import scala.concurrent.Future

class IndividualHandoffControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: IndividualHandoffView = app.injector.instanceOf[IndividualHandoffView]
    val userAnswers: Option[UserAnswers]         = Some(UserAnswers("credId"))
    lazy val controller                          = new IndividualHandoffController(messagesControllerComponents, view)
  }

  "GET onLoad" when {
    "userAnswers doesn't exist" should {
      "return OK" in new Test {
        val result: Future[Result] = controller.onLoad()(fakeRequest)
        status(result) mustBe Status.OK
      }
    }
  }

}
