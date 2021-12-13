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
import mocks.config.MockAppConfig
import play.api.http.Status
import play.api.test.Helpers._
import views.html.errors._

class UnauthorisedControllerSpec extends ControllerSpecBase {

  lazy val view: UnauthorisedView = app.injector.instanceOf[UnauthorisedView]
  lazy val unauthorisedAgentAccessView: UnauthorisedAgentAccessView =
    app.injector.instanceOf[UnauthorisedAgentAccessView]

  private lazy val controller = new UnauthorisedController(
    messagesControllerComponents,
    view,
    unauthorisedAgentAccessView,
    MockAppConfig
  )

  "onPageLoad" should {
    "return 200" in {
      val result = controller.onPageLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val result = controller.onPageLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "unauthorisedAgentAccess" should {
    "return 200" in {
      val result = controller.unauthorisedAgentAccess(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val result = controller.unauthorisedAgentAccess(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }
}
