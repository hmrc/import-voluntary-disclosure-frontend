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

package controllers.reasons

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.reasons.{BoxNumber, UnderpaymentReason, UnderpaymentReasonValue}
import pages.reasons.{UnderpaymentReasonAmendmentPage, UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage, UnderpaymentReasonsPage}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import views.data.reasons.ConfirmReasonData
import views.html.reasons.ConfirmReasonDetailView

import scala.concurrent.Future

class ConfirmReasonDetailControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: ConfirmReasonDetailView = app.injector.instanceOf[ConfirmReasonDetailView]

    MockedSessionRepository.set(Future.successful(true))

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(UnderpaymentReasonBoxNumberPage, BoxNumber.Box22).success.value
        .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("1806321000", "2204109400X411")).success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new ConfirmReasonDetailController(
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
      val result = controller.buildSummaryList(
        UserAnswers("some-cred-id")
          .set(UnderpaymentReasonBoxNumberPage, BoxNumber.Box33).success.value
          .set(UnderpaymentReasonItemNumberPage, 1).success.value
          .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("1806321000", "2204109400X411")).success.value,
        boxNumber = BoxNumber.Box33
      )

      val expectedResult = ConfirmReasonData.reasons(33, Some(1), "1806321000", "2204109400X411")
      result mustBe expectedResult
    }

    "produce correct summary list for Other Reason" in new Test {
      val result = controller.buildSummaryList(
        UserAnswers("some-cred-id")
          .set(UnderpaymentReasonBoxNumberPage, BoxNumber.OtherItem).success.value
          .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("Other Reason", "")).success.value,
        boxNumber = BoxNumber.OtherItem
      )

      val rows = result.head.rows
      rows.length mustBe 1
      rows.head.value.content.asHtml.toString() mustBe "Other Reason"
      rows.head.key.content.asHtml.toString() mustBe "Other reason"
    }
  }

  "GET onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER entry level response when correct data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad().url
        )
      }

      "return a SEE OTHER item level response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentReasonBoxNumberPage, BoxNumber.Box33).success.value
            .set(UnderpaymentReasonItemNumberPage, 1).success.value
            .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("1806321000", "2204109400X411")).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad().url
        )
        verifyCalls()
      }

      "return a SEE OTHER when existing reason are present" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentReasonBoxNumberPage, BoxNumber.Box33).success.value
            .set(UnderpaymentReasonItemNumberPage, 1).success.value
            .set(UnderpaymentReasonAmendmentPage, UnderpaymentReasonValue("1806321000", "2204109400X411")).success.value
            .set(
              UnderpaymentReasonsPage,
              Seq(UnderpaymentReason(BoxNumber.Box22, 0, "GBP871.12", "EUR2908946"))
            ).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result).get mustBe controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad().url
        verifyCalls()
      }

      "payload contains no data" should {
        "produce an empty summary list" in new Test {
          val result = controller.buildSummaryList(UserAnswers("some-cred-id"), BoxNumber.Box22)
          result mustBe Seq(SummaryList())
        }
      }
    }
  }
}
