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

package services.submissionService

import base.SpecBase
import mocks.connectors.MockIvdSubmissionConnector
import mocks.services.MockAuditService
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.{ErrorModel, SubmissionData, SubmissionResponse, UserAnswers}
import pages.importDetails.EnterCustomsProcedureCodePage
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import services.SubmissionService

class SubmissionServiceSpec extends SpecBase
  with MockIvdSubmissionConnector
  with MockAuditService
  with SubmissionServiceTestData
  with SubmissionServiceTestJson {

  trait Test {
    def setupMock(response: Either[ErrorModel, SubmissionResponse]): Unit = {
      setupMockCreateCase(response)
    }

    implicit lazy val dataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(
      OptionalDataRequest(
        IdentifierRequest(fakeRequest, "credId", "eori"),
        "credId",
        "eori",
        Some(userAnswers)
      ),
      "credId",
      "eori",
      userAnswers
    )

    val successfulCreateCase: SubmissionResponse = SubmissionResponse("case-Id")
    val failedCreateCaseConnectorCall: ErrorModel = ErrorModel(Status.BAD_REQUEST, "Downstream error returned when retrieving SubmissionResponse from back end")
    val invalidUserAnswersError: ErrorModel = ErrorModel(-1, "Invalid User Answers data. Failed to read into SubmissionData model")

    val submission: SubmissionData = completeSubmission
    val userAnswers: UserAnswers = completeUserAnswers

    val outputJson: String = completeOutputJson

    stubAudit()
    val service = new SubmissionService(mockIVDSubmissionConnector, mockAuditService)

  }

  "createCase" when {
    "called with a valid User Answers" should {
      "return successful SubmissionResponse" in new Test {
        setupMock(Right(successfulCreateCase))
        private val result = await(service.createCase()(dataRequest, hc, ec))

        result mustBe Right(successfulCreateCase)
      }

      "return error if connector call fails" in new Test {
        setupMock(Left(failedCreateCaseConnectorCall))
        private val result = await(service.createCase()(dataRequest, hc, ec))

        result mustBe Left(failedCreateCaseConnectorCall)
      }
    }

    "called with an invalid User Answers" should {
      "return error - unable to parse to model" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
        private val result = await(service.createCase()(dataRequest, hc, ec))

        result.isLeft mustBe true
        val Left(error) = result
        error.message must include("user-type")
      }
    }
  }

  "buildSubmission" when {

    "called with a complete User Answers" should {
      "return expect json" in new Test {
        private val result = service.buildSubmission(userAnswers)

        result mustBe Right(Json.parse(completeOutputJson))
      }
    }

    "called with an invalid User Answers" should {
      "return error - unable to parse to model" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
        private val result = await(service.createCase()(dataRequest, hc, ec))

        result.isLeft mustBe true
        val Left(error) = result
        error.message must include("user-type")
      }

      "return error - parsed model is not valid" in new Test {
        override val userAnswers: UserAnswers =
          completeUserAnswers.remove(EnterCustomsProcedureCodePage).success.value
        private val result = await(service.createCase()(dataRequest, hc, ec))

        result.isLeft mustBe true
        val Left(error) = result
        error.message must include("CPC missing from user answers")
      }
    }
  }

}
