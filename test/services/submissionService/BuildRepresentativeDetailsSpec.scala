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

package services.submissionService

import base.ServiceSpecBase
import mocks.connectors.MockIvdSubmissionConnector
import mocks.services.MockAuditService
import play.api.libs.json.Json
import services.SubmissionService

class BuildRepresentativeDetailsSpec
    extends ServiceSpecBase
    with MockIvdSubmissionConnector
    with MockAuditService
    with SubmissionServiceTestData
    with SubmissionServiceTestJson {

  val service = new SubmissionService(mockIVDSubmissionConnector, mockAuditService)

  "buildRepresentativeDetails" when {

    "called with valid User Answers" should {
      "return expect json" in {
        lazy val result = service.buildRepresentativeDetails(completeSubmission)

        result mustBe Json.parse(representativeDetailsJson)
      }
    }

    "called with valid User Answers in Importer flow" should {
      "return expect json" in {
        lazy val result = service.buildRepresentativeDetails(importerSubmission)

        result mustBe Json.obj()
      }
    }

  }

}
