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

package controllers.reasons

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, ChangeUnderpaymentReason, UnderpaymentReason}
import pages.reasons.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.data.reasons.ChangeUnderpaymentReasonData
import views.html.reasons.ChangeUnderpaymentReasonView

import scala.concurrent.Future

class ChangeUnderpaymentReasonControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: ChangeUnderpaymentReasonView = app.injector.instanceOf[ChangeUnderpaymentReasonView]

    def underpayment(box: BoxNumber, item: Int = 0, original: String = "50", amended: String = "60") =
      UnderpaymentReason(boxNumber = box, itemNumber = item, original = original, amended = amended)

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(
          UnderpaymentReasonsPage,
          Seq(
            underpayment(box = BoxNumber.Box33, item = 15, original = "50", amended = "60"),
            underpayment(box = BoxNumber.Box22, original = "50", amended = "60")
          )
        ).success.value
    )

    MockedSessionRepository.set(Future.successful(true))

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new ChangeUnderpaymentReasonController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      view,
      ec
    )
  }

  "GET onLoad" when {

    "return OK" in new Test {
      override val userAnswers = Some(
        UserAnswers("credId")
          .set(
            ChangeUnderpaymentReasonPage,
            ChangeUnderpaymentReason(
              original = underpayment(box = BoxNumber.Box22),
              changed = underpayment(box = BoxNumber.Box22)
            )
          ).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers = Some(
        UserAnswers("credId")
          .set(
            ChangeUnderpaymentReasonPage,
            ChangeUnderpaymentReason(
              original = underpayment(box = BoxNumber.Box22),
              changed = underpayment(box = BoxNumber.Box22)
            )
          ).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return Internal Server Error" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

  }

  "GET change" when {

    "return See Other" in new Test {
      val result: Future[Result] = controller.change(22, 0)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad().url)
    }

    "return Internal Server Error" in new Test {
      override val userAnswers   = Some(UserAnswers("credId"))
      val result: Future[Result] = controller.change(22, 0)(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "summaryList" when {

    "single item is passed" should {
      "produce summary list with one item" in new Test {
        controller.summaryList(
          ChangeUnderpaymentReasonData.singleItemReason.original
        ) mustBe ChangeUnderpaymentReasonData.summaryList(BoxNumber.Box35)
      }

      "produce summary list with Other Item" in new Test {
        controller.summaryList(
          ChangeUnderpaymentReasonData.otherItemReason.original
        ) mustBe ChangeUnderpaymentReasonData.otherItemSummaryList()
      }
    }

  }

}
