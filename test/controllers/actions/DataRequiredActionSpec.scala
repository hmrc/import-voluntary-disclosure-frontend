/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.actions

import base.SpecBase
import models.{SubmissionType, UserAnswers}
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import org.scalatest.EitherValues
import pages.serviceEntry.SubmissionTypePage
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._

import scala.concurrent.Future
class DataRequiredActionSpec extends SpecBase with EitherValues {
  class Harness extends DataRequiredActionImpl {
    def actionRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  val identifierRequest: IdentifierRequest[AnyContentAsEmpty.type] = IdentifierRequest(fakeRequest, "id", "eori")
  val userAnswers: UserAnswers                                     = UserAnswers("id")

  "DataRequiredAction" must {
    "redirect to IndexController when userAnswers is `None`" in {
      val harness = new Harness

      val result =
        harness.actionRefine(OptionalDataRequest(identifierRequest, "id", "eori", None)).futureValue.left.value.header

      result.status mustBe SEE_OTHER
      result.headers.get(LOCATION) mustBe Some("/disclose-import-taxes-underpayment")
    }

    "return userAnswers when userAnswers data exists" in {
      val harness = new Harness

      val result =
        harness.actionRefine(OptionalDataRequest(identifierRequest, "id", "eori", Some(userAnswers))).futureValue

      result.isRight mustBe true
    }

    "redirect to 'already submitted' page when submissionType is 'create'" in {

      val harness = new Harness

      val ua = userAnswers.set(SubmissionTypePage, SubmissionType.CreateCase).success.value

      val result = harness.actionRefine(
        OptionalDataRequest(identifierRequest, "id", "eori", Some(ua))
      ).futureValue.left.value.header

      result.status mustBe SEE_OTHER
      result.headers.get(LOCATION) mustBe Some("/disclose-import-taxes-underpayment/already-submitted")
    }

    "redirect to 'already submitted' page when submissionType is 'amend'" in {

      val harness = new Harness

      val ua = userAnswers.set(SubmissionTypePage, SubmissionType.UpdateCase).success.value

      val result = harness.actionRefine(
        OptionalDataRequest(identifierRequest, "id", "eori", Some(ua))
      ).futureValue.left.value.header

      result.status mustBe SEE_OTHER
      result.headers.get(LOCATION) mustBe Some("/disclose-import-taxes-underpayment/already-added")
    }

    "redirect to 'already submitted' page when submissionType is 'cancel'" in {

      val harness = new Harness

      val ua = userAnswers.set(SubmissionTypePage, SubmissionType.CancelCase).success.value

      val result = harness.actionRefine(
        OptionalDataRequest(identifierRequest, "id", "eori", Some(ua))
      ).futureValue.left.value.header

      result.status mustBe SEE_OTHER
      result.headers.get(LOCATION) mustBe Some("/disclose-import-taxes-underpayment/already-cancelled")
    }

  }

}
