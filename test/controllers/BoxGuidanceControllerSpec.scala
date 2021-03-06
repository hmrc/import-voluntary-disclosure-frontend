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

package controllers

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.{UnderpaymentReason, UserAnswers}
import pages.UnderpaymentReasonsPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, status, _}
import views.html.BoxGuidanceView

import scala.concurrent.Future

class BoxGuidanceControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {

    lazy val controller = new BoxGuidanceController(authenticatedAction, dataRetrievalAction,
      messagesControllerComponents, dataRequiredAction, view, appConfig, ec)
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val view = injector.instanceOf[BoxGuidanceView]
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
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

    "redirect to the summary page when underpayment reasons already exist" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UnderpaymentReasonsPage, Seq(UnderpaymentReason(35, 1, "100", "350"))).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.UnderpaymentReasonSummaryController.onLoad().url)

    }

  }
}
