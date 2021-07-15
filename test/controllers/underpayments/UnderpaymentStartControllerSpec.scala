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

package controllers.underpayments

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.importDetails.NumberOfEntries.{MoreThanOneEntry, OneEntry}
import models.UserAnswers
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import pages.underpayments.UnderpaymentDetailSummaryPage
import pages.importDetails.{EnterCustomsProcedureCodePage, NumberOfEntriesPage}
import play.api.http.Status
import play.api.mvc.{Call, Result}
import play.api.test.Helpers._
import utils.ReusableValues
import views.html.underpayments.UnderpaymentStartView

import scala.concurrent.Future

class UnderpaymentStartControllerSpec extends ControllerSpecBase with ReusableValues {

  trait Test extends MockSessionRepository {

    implicit lazy val dataRequest = new DataRequest(
      new OptionalDataRequest(
        new IdentifierRequest(fakeRequest, "credId", "eori"),
        "credId",
        "eori",
        userAnswers
      ),
      "credId",
      "eori",
      userAnswers.get
    )

    lazy val controller = new UnderpaymentStartController(authenticatedAction, dataRetrievalAction,
      messagesControllerComponents, dataRequiredAction, view, ec)
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val view: UnderpaymentStartView = injector.instanceOf[UnderpaymentStartView]
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
  }

  "GET onLoad" should {
    "return 200" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return 303" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UnderpaymentDetailSummaryPage, allUnderpaymentDetailsSelected()).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "backLink" when {
    "in Single Entry mode" should {
      "point to Enter CPC page if it is defined" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(NumberOfEntriesPage, OneEntry).success.value
            .set(EnterCustomsProcedureCodePage, "cpc").success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad()
      }
      "point to One CPC page if no CPC previously captured" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(NumberOfEntriesPage, OneEntry).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.importDetails.routes.OneCustomsProcedureCodeController.onLoad()
      }
    }

    "in Bulk Entry mode" should {
      "point to Check Your Answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(NumberOfEntriesPage, MoreThanOneEntry).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.importDetails.routes.AcceptanceDateController.onLoad()
      }
    }
  }

}
