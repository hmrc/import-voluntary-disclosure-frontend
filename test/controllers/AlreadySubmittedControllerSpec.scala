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

package controllers

import base.ControllerSpecBase
import play.api.test.Helpers._
import views.html.shared.AlreadySubmittedView

class AlreadySubmittedControllerSpec extends ControllerSpecBase {
  private lazy val submittedView: AlreadySubmittedView = app.injector.instanceOf[AlreadySubmittedView]

  lazy val controller: AlreadySubmittedController = new AlreadySubmittedController(
    authenticatedAction,
    messagesControllerComponents,
    submittedView,
    ec
  )

  "AlreadySubmittedController" must {

    "load create case already submitted view" in {
      val result = controller.createSubmitted()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustEqual submittedView("createCase")(fakeRequest, messages).toString
    }

    "load amend case already submitted view" in {
      val result = controller.amendSubmitted()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustEqual submittedView("updateCase")(fakeRequest, messages).toString
    }

    "load cancel case already submitted view" in {
      val result = controller.cancelSubmitted()(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustEqual submittedView("cancelCase")(fakeRequest, messages).toString
    }

  }

}
