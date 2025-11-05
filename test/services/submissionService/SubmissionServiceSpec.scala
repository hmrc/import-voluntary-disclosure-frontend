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

package services.submissionService

import base.ServiceSpecBase
import connectors.IvdSubmissionConnector
import models.*
import models.requests.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.{BeforeAndAfterEach, TryValues}
import org.scalatestplus.mockito.MockitoSugar.mock
import pages.importDetails.EnterCustomsProcedureCodePage
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import services.{AuditService, SubmissionService}

import scala.concurrent.Future

class SubmissionServiceSpec
    extends ServiceSpecBase
    with TryValues
    with SubmissionServiceTestData
    with SubmissionServiceTestJson
    with BeforeAndAfterEach {

  val mockIVDSubmissionConnector: IvdSubmissionConnector = mock[IvdSubmissionConnector]
  val mockAuditService: AuditService                     = mock[AuditService]

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
  val failedCreateCaseConnectorCall: ErrorModel =
    ErrorModel(Status.BAD_REQUEST, "Downstream error returned when retrieving SubmissionResponse from back end")
  val invalidUserAnswersError: ErrorModel =
    ErrorModel(-1, "Invalid User Answers data. Failed to read into SubmissionData model")

  val submission: SubmissionData = completeSubmission
  val userAnswers: UserAnswers   = completeUserAnswers

  val outputJson: String = completeOutputJson

  val service = new SubmissionService(mockIVDSubmissionConnector, mockAuditService)

  "createCase" when {
    "called with a valid User Answers" should {
      "return successful SubmissionResponse" in {
        when(mockIVDSubmissionConnector.createCase(any())(any())).thenReturn(
          Future.successful(Right(successfulCreateCase))
        )
        val result = await(service.createCase)

        result mustBe Right(successfulCreateCase)
      }

      "return error if connector call fails" in {
        when(mockIVDSubmissionConnector.createCase(any())(any())).thenReturn(
          Future.successful(Left(failedCreateCaseConnectorCall))
        )
        val result = await(service.createCase)

        result mustBe Left(failedCreateCaseConnectorCall)
      }
    }

    "called with an invalid User Answers" should {
      "return error - unable to parse to model" in {
        val ua: UserAnswers = UserAnswers("some-cred-id")
        val result = await(service.createCase(dataRequest.copy(userAnswers = ua), headerCarrier, executionContext))

        result.isLeft mustBe true
        val Left(error) = result
        error.message must include("user-type")
      }
    }
  }

  "buildSubmission" when {

    "called with a complete User Answers" should {
      "return expect json" in {
        val result = service.buildSubmission(userAnswers)

        result mustBe Right(Json.parse(completeOutputJson))
      }
    }

    "called with an invalid User Answers" should {
      "return error - unable to parse to model" in {
        val ua: UserAnswers = UserAnswers("some-cred-id")
        val result = await(service.createCase(dataRequest.copy(userAnswers = ua), headerCarrier, executionContext))

        result.isLeft mustBe true
        val Left(error) = result
        error.message must include("user-type")
      }

      "return error - parsed model is not valid" in {
        val ua: UserAnswers =
          completeUserAnswers.remove(EnterCustomsProcedureCodePage).success.value
        val result = await(service.createCase(dataRequest.copy(userAnswers = ua), headerCarrier, executionContext))

        result.isLeft mustBe true
        val Left(error) = result
        error.message must include("CPC missing from user answers")
      }
    }
  }

}
