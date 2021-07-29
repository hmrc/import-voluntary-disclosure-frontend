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

package models.audit

import base.ModelSpecBase
import models.UserAnswers
import models.requests._
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys

class CreateCaseAuditEventSpec extends ModelSpecBase with AuditTestData {

  "A valid CreateCaseAuditEvent model" should {

    val userAnswers = UserAnswers("credId")

    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest("GET", "/foo").withSession(SessionKeys.sessionId -> "foo")
    implicit lazy val dataRequest = DataRequest(
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

    "contain correct details" in {
      val event = CreateCaseAuditEvent(submissionResponse, Json.parse(completeSubmissionJson))

      event.detail mustBe Json.parse(auditOutputJson)
    }

  }
}
