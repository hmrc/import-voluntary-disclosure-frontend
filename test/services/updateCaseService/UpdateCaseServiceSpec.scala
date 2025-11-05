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

package services.updateCaseService

import base.ServiceSpecBase
import connectors.IvdSubmissionConnector
import models._
import models.audit.{CancelCaseAuditEvent, UpdateCaseAuditEvent}
import models.requests._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{atMostOnce, reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import services.{AuditService, UpdateCaseService}

import scala.concurrent.Future

class UpdateCaseServiceSpec extends ServiceSpecBase with UpdateCaseServiceTestData with BeforeAndAfterEach {

  val mockIVDSubmissionConnector: IvdSubmissionConnector = mock[IvdSubmissionConnector]
  val mockAuditService: AuditService                     = mock[AuditService]

  val userAnswers: UserAnswers = completeUserAnswers

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

  val failedCreateCaseConnectorCall: UpdateCaseError =
    UpdateCaseError.UnexpectedError(
      Status.BAD_REQUEST,
      Some("Downstream error returned when retrieving SubmissionResponse from back end")
    )

  val service = new UpdateCaseService(mockIVDSubmissionConnector, mockAuditService)

  override protected def beforeEach(): Unit =
    when(mockIVDSubmissionConnector.updateCase(any())(any())).thenReturn(
      Future.successful(Right(UpdateCaseResponse("1234")))
    )

  "UpdateCaseService" when {
    "called with valid user answers" should {
      "return successful UpdateCaseResponse" in {
        val response: UpdateCaseResponse = UpdateCaseResponse("1234")
        val result                       = await(service.updateCase)
        result mustBe Right(response)
        verify(mockAuditService, atMostOnce()).audit(UpdateCaseAuditEvent(updateCaseJson))
      }

      "return successful UpdateCaseResponse for Cancel Case" in {
        val ua: UserAnswers              = cancelCaseCompleteUserAnswers
        val response: UpdateCaseResponse = UpdateCaseResponse("1234")
        val result = await(service.updateCase(dataRequest.copy(userAnswers = ua), headerCarrier, executionContext))
        result mustBe Right(response)
        verify(mockAuditService, atMostOnce()).audit(CancelCaseAuditEvent(cancelCaseJson))
      }

      "return error if connector call fails" in {
        reset(mockIVDSubmissionConnector)
        when(mockIVDSubmissionConnector.updateCase(any())(any())).thenReturn(
          Future.successful(Left(failedCreateCaseConnectorCall))
        )
        val result = await(service.updateCase)

        result mustBe Left(failedCreateCaseConnectorCall)
      }
    }

    "called with incomplete User Answers" should {
      "return error - unable to parse to model" in {
        val ua: UserAnswers = UserAnswers("some-cred-id")
        val result = await(service.updateCase(dataRequest.copy(userAnswers = ua), headerCarrier, executionContext))

        result must matchPattern { case Left(UpdateCaseError.UnexpectedError(_, _)) =>
        }
      }
    }
  }

  "buildUpdate" when {
    "called with a complete User Answers" should {
      "return expected JSON" in {
        val result = service.buildUpdate

        result mustBe Right(updateCaseJson)
      }
    }

    "called without supporting documents" should {
      "return expected JSON" in {
        val ua: UserAnswers = userAnswersWithoutDocs
        val result          = service.buildUpdate(dataRequest.copy(userAnswers = ua))

        result mustBe Right(updateCaseJsonWithoutDocs)
      }
    }
  }
}
