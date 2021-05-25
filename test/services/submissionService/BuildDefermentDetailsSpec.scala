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
import play.api.libs.json.Json
import services.SubmissionService

class BuildDefermentDetailsSpec extends SpecBase with MockIvdSubmissionConnector with MockAuditService with SubmissionServiceTestData with SubmissionServiceTestJson {

  val service = new SubmissionService(mockIVDSubmissionConnector, mockAuditService)

  "buildDefermentDetails" when {

    "called with valid User Answers for importer deferment" should {
      "return expect json" in {
        lazy val result = service.buildDefermentDetails(importerDefermentSubmission)

        result mustBe Json.parse(importerDefermentDetailsJson)
      }
    }

    "called with valid User Answers for no deferment" should {
      "return expect json" in {
        lazy val result = service.buildDefermentDetails(importerSubmission)

        result mustBe Json.obj()
      }
    }

    "called with valid User Answers for representative no split deferment" should {
      "return expect json" in {
        lazy val result = service.buildDefermentDetails(representativeNoSplitDefermentSubmission)

        result mustBe Json.parse(representativeNoSplitDefermentDetailsJson)
      }
    }

    "called with valid User Answers for representative with split deferment" should {
      "return expect json" in {
        lazy val result = service.buildDefermentDetails(completeSubmission)

        result mustBe Json.parse(splitDefermentDetailsJson)
      }
    }

  }

}
