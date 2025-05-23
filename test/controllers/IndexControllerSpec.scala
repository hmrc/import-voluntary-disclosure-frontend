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
import play.api.http.Status
import play.api.test.Helpers._

class IndexControllerSpec extends ControllerSpecBase {

  private lazy val controller = new IndexController(appConfig, messagesControllerComponents)

  "GET onPageLoad" should {
    "return 303" in {
      val result = controller.onPageLoad(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.serviceEntry.routes.ConfirmEORIDetailsController.onLoad().url)
    }

  }
}
