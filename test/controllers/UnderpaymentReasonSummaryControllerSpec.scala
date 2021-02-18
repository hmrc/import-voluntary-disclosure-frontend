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
import forms.UnderpaymentReasonSummaryFormProvider
import models.{UnderpaymentReason, UserAnswers}
import pages.UnderpaymentReasonsPage
import play.api.http.Status
import play.api.mvc.{Call, Result}
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import views.html.UnderpaymentReasonSummaryView

import scala.concurrent.Future

class UnderpaymentReasonSummaryControllerSpec extends ControllerSpecBase {

  trait Test {
    private lazy val view: UnderpaymentReasonSummaryView = app.injector.instanceOf[UnderpaymentReasonSummaryView]
    private lazy val formProvider: UnderpaymentReasonSummaryFormProvider = app.injector.instanceOf[UnderpaymentReasonSummaryFormProvider]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(
          UnderpaymentReasonsPage,
          Seq(
            UnderpaymentReason(boxNumber = 33, itemNumber = 15, original = "50", amended = "60"),
            UnderpaymentReason(boxNumber = 22, original = "50", amended = "60")
          )
        ).success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new UnderpaymentReasonSummaryController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      messagesControllerComponents, view, formProvider)
  }

  "GET /" when {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "the backLink functionality is called" should {
      "redirect to the Acceptance date page" in new Test {
        controller.backLink mustBe Call("GET", controllers.routes.BoxGuidanceController.onLoad().url)
      }
    }

  }

}
