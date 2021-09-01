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

package controllers.reasons

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import messages.ConfirmChangeReasonDetailMessages
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, ChangeUnderpaymentReason, UnderpaymentReason}
import pages.reasons.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import views.data.reasons.ConfirmChangeReasonData
import views.html.reasons.ConfirmChangeReasonDetailView

import scala.concurrent.Future

class ConfirmChangeReasonDetailControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    def underpayment(box: BoxNumber, item: Int = 0, original: String = "50", amended: String = "60") =
      UnderpaymentReason(boxNumber = box, itemNumber = item, original = original, amended = amended)

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(
          UnderpaymentReasonsPage,
          Seq(
            UnderpaymentReason(
              boxNumber = BoxNumber.Box33,
              itemNumber = 1,
              original = "1806321000",
              amended = "2204109400X411"
            )
          )
        )
        .success
        .value
        .set(
          ChangeUnderpaymentReasonPage,
          ChangeUnderpaymentReason(
            underpayment(box = BoxNumber.Box33, item = 1, original = "1806321000", amended = "2204109400X411"),
            underpayment(box = BoxNumber.Box33, item = 2, original = "1806321001", amended = "2204109400X412")
          )
        )
        .success
        .value
    )

    private lazy val view: ConfirmChangeReasonDetailView = app.injector.instanceOf[ConfirmChangeReasonDetailView]

    MockedSessionRepository.set(Future.successful(true))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new ConfirmChangeReasonDetailController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      view,
      ec
    )
  }

  "GET onLoad " should {

    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "produce correct summary list" in new Test {
      val result = controller.summaryList(
        UserAnswers("some-cred-id")
          .set(
            ChangeUnderpaymentReasonPage,
            ChangeUnderpaymentReason(
              underpayment(box = BoxNumber.Box33, item = 1, original = "1806321000", amended = "2204109400X411"),
              underpayment(box = BoxNumber.Box33, item = 2, original = "1806321001", amended = "2204109400X412")
            )
          )
          .success
          .value,
        boxNumber = BoxNumber.Box33
      )

      val expectedResult = ConfirmChangeReasonData.reasons(33, Some(2), "1806321001", "2204109400X412")
      result mustBe expectedResult
    }

    "produce correct summary list for Other Item" in new Test {
      val result = controller.summaryList(
        UserAnswers("some-cred-id")
          .set(
            ChangeUnderpaymentReasonPage,
            ChangeUnderpaymentReason(
              underpayment(box = BoxNumber.OtherItem, original = "Other reason", amended = ""),
              underpayment(box = BoxNumber.OtherItem, original = "New other reason", amended = "")
            )
          )
          .success
          .value,
        boxNumber = BoxNumber.OtherItem
      )

      val expectedResult = ConfirmChangeReasonData.otherItemReasons("New other reason")
      result mustBe expectedResult
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER entry level response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentReasonsPage,
              Seq(UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60"))
            )
            .success
            .value
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                underpayment(box = BoxNumber.Box22, original = "50", amended = "60"),
                underpayment(box = BoxNumber.Box22, original = "60", amended = "70")
              )
            )
            .success
            .value
        )
        lazy val result: Future[Result]               = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad().url
        )
      }

      "return a SEE OTHER item level response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentReasonsPage,
              Seq(UnderpaymentReason(boxNumber = BoxNumber.Box33, itemNumber = 1, original = "50", amended = "60"))
            )
            .success
            .value
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                underpayment(box = BoxNumber.Box33, item = 1, original = "50", amended = "60"),
                underpayment(box = BoxNumber.Box33, item = 2, original = "60", amended = "70")
              )
            )
            .success
            .value
        )
        lazy val result: Future[Result]               = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad().url
        )
        verifyCalls()
      }

      "return an Internal Server Error when Changed underpayment reasons are not present" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentReasonsPage,
              Seq(UnderpaymentReason(boxNumber = BoxNumber.Box33, itemNumber = 1, original = "50", amended = "60"))
            )
            .success
            .value
        )
        lazy val result: Future[Result]               = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }

      "return an Internal Server Error when Existing underpayment reasons are not present" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                underpayment(box = BoxNumber.Box33, item = 1, original = "50", amended = "60"),
                underpayment(box = BoxNumber.Box33, item = 2, original = "60", amended = "70")
              )
            )
            .success
            .value
        )
        lazy val result: Future[Result]               = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }

      "payload contains no data" should {
        "produce no summary list" in new Test {
          val result = controller.summaryList(UserAnswers("some-cred-id"), BoxNumber.Box22)
          result mustBe SummaryList(Seq.empty)
        }
      }
    }
  }

  "title and heading" when {
    "Other Item is selected" should {
      "render dedicated messages" in new Test {
        controller.pageTitle(BoxNumber.OtherItem) mustBe ConfirmChangeReasonDetailMessages.otherReasonTitle
        controller.pageHeading(BoxNumber.OtherItem) mustBe ConfirmChangeReasonDetailMessages.otherReasonHeading
      }
    }

    "Other Item is not selected" should {
      "render regular messages" in new Test {
        controller.pageTitle(BoxNumber.Box33) mustBe ConfirmChangeReasonDetailMessages.title(33)
        controller.pageHeading(BoxNumber.Box33) mustBe ConfirmChangeReasonDetailMessages.heading(33)
      }
    }
  }
}
