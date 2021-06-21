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

class BuildSupportingDocumentationSpec extends SpecBase with MockIvdSubmissionConnector with MockAuditService with SubmissionServiceTestData with SubmissionServiceTestJson {

  val service = new SubmissionService(mockIVDSubmissionConnector, mockAuditService)

  "buildSupportingDocumentation" when {

    "called with valid User Answers for Importer deferment" should {
      "return expect json" in {
        lazy val result = service.buildSupportingDocumentation(importerSubmission)

        result mustBe Json.parse(supportingDocumentationJson)
      }
    }

    "called with valid User Answers for Importer deferment for bulk entry" should {
      "return expect json" in {
        lazy val result = service.buildSupportingDocumentation(bulkImporterSubmission)

        result mustBe Json.parse(bulkSupportingDocumentationJson)
      }
    }

    "called with valid User Answers for Duty only deferment" should {
      "return expect json" in {
        lazy val result = service.buildSupportingDocumentation(dutyOnlyDefermentSubmission)

        result mustBe Json.parse(singleUnderpaymentDefermentSupportingDocumentationJson)
      }
    }

    "called with valid User Answers for Vat only deferment" should {
      "return expect json" in {
        lazy val result = service.buildSupportingDocumentation(vatOnlyDefermentSubmission)

        result mustBe Json.parse(singleUnderpaymentDefermentSupportingDocumentationJson)
      }
    }

    "called with valid User Answers for no split deferment" should {
      "return expect json" in {
        lazy val result = service.buildSupportingDocumentation(representativeNoSplitDefermentSubmission)

        result mustBe Json.parse(noSplitDefermentSupportingDocumentationJson)
      }
    }

    "called with valid User Answers for split deferment" should {
      "return expect json" in {
        lazy val result = service.buildSupportingDocumentation(completeSubmission)

        result mustBe Json.parse(splitDefermentSupportingDocumentationJson)
      }
    }

    "called with valid User Answers for split deferment for bulk entry" should {
      "return expect json" in {
        lazy val result = service.buildSupportingDocumentation(bulkCompleteSubmission)

        result mustBe Json.parse(bulkSplitDefermentSupportingDocumentationJson)
      }
    }

  }

}
