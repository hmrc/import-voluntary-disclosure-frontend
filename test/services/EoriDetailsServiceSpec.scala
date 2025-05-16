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
import mocks.connectors.MockIvdSubmissionConnector
import mocks.services.MockAuditService
import models.UserAnswers
import models.audit.EoriDetailsAuditEvent
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.AnyContentAsEmpty
import utils.ReusableValues

class EoriDetailsServiceSpec
    extends SpecBase
    with MockIvdSubmissionConnector
    with MockAuditService
    with ReusableValues {

  def setup(eoriDetailsResponse: EoriDetailsResponse): EoriDetailsService = {
    setupMockGetEoriDetails(eoriDetailsResponse)
    verifyAudit(EoriDetailsAuditEvent("eori", "credId"))
    new EoriDetailsService(mockIVDSubmissionConnector, mockAuditService, messagesApi, appConfig)
  }

  implicit val request: OptionalDataRequest[AnyContentAsEmpty.type] = OptionalDataRequest(
    IdentifierRequest(fakeRequest, "credId", "eori"),
    "credId",
    "eori",
    Some(UserAnswers("credId"))
  )

  "connector call is successful" should {
    lazy val service = setup(Right(eoriDetails))
    lazy val result  = service.retrieveEoriDetails("eori")

    "return successful RetrieveEoriDetailsResponse" in {
      await(result) mustBe Right(eoriDetails)
    }
  }
}
