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
import play.api.libs.json.Json
import services.SubmissionService

class BuildReasonsDetailsSpec extends SpecBase with MockIvdSubmissionConnector with SubmissionServiceTestData with SubmissionServiceTestJson {

  trait Test {

    val service = new SubmissionService(mockIVDSubmissionConnector)

  }

  "buildReasonsDetails" when {

    "called with valid User Answers" should {
      "return expect json" in new Test {
        lazy val result = service.buildReasonsDetails(importerSubmission)

        result mustBe Json.parse(importerReasonsDetailsJson)
      }
    }

  }

}
