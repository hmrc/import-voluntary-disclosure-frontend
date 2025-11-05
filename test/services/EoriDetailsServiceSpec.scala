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

package services

import base.SpecBase
import connectors.IvdSubmissionConnector
import models.{ErrorModel, UserAnswers}
import models.audit.EoriDetailsAuditEvent
import models.requests.{IdentifierRequest, OptionalDataRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{atMostOnce, when}
import play.api.mvc.AnyContentAsEmpty
import utils.ReusableValues

import scala.concurrent.Future

class EoriDetailsServiceSpec extends SpecBase with ReusableValues {

  val mockIVDSubmissionConnector: IvdSubmissionConnector = mock[IvdSubmissionConnector]
  val mockAuditService: AuditService                     = mock[AuditService]
  val service = new EoriDetailsService(mockIVDSubmissionConnector, mockAuditService, messagesApi, appConfig)

  implicit val request: OptionalDataRequest[AnyContentAsEmpty.type] = OptionalDataRequest(
    IdentifierRequest(fakeRequest, "credId", "eori"),
    "credId",
    "eori",
    Some(UserAnswers("credId"))
  )

  "connector call is successful" should {
    when(mockIVDSubmissionConnector.getEoriDetails(any())(any())).thenReturn(Future.successful(Right(eoriDetails)))

    val result = service.retrieveEoriDetails("eori")

    "return successful RetrieveEoriDetailsResponse" in {
      await(result) mustBe Right(eoriDetails)
      Mockito.verify(mockAuditService, atMostOnce()).audit(EoriDetailsAuditEvent("eori", "credId"))
    }
  }

  "connector call in unsuccessful" should {
    "return Left with error" in {
      Mockito.reset(mockAuditService)
      val errorResponse = ErrorModel(500, "server down")
      when(mockIVDSubmissionConnector.getEoriDetails(any())(any())).thenReturn(Future.successful(Left(errorResponse)))

      val result = service.retrieveEoriDetails("eori")

      await(result) mustBe Left(errorResponse)
      Mockito.verifyNoInteractions(mockAuditService)
    }
  }
}
