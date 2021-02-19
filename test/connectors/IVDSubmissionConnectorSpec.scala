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

package connectors

import base.SpecBase
import mocks.MockHttp
import models._
import utils.ReusableValues

import java.time.LocalDate

class IVDSubmissionConnectorSpec extends SpecBase with MockHttp with ReusableValues {

  lazy val target = new IVDSubmissionConnector(mockHttp, appConfig)

  "Importer Address Connector" should {

    "return the Right response" in {
      setupMockHttpGet(target.getAddressUrl(idOne))(Right(traderAddress))
      await(target.getAddress(idOne)) mustBe Right(traderAddress)
    }

    "return the error response" in {
      setupMockHttpGet(target.getAddressUrl(idOne))(Left(errorModel))
      await(target.getAddress(idOne)) mustBe Left(errorModel)
    }

  }

  "called to post the Submission" should {

    val submission = IVDSubmission(
      userType = UserType.Importer,
      numEntries = NumberOfEntries.OneEntry,
      acceptanceDate = None,
      additionalInfo = None,
      entryDetails = EntryDetails("123", "123456Q", LocalDate.parse("2020-01-12")),
      originalCpc = "cpc",
      declarantContactDetails = ContactDetails("name", "email", "phone"),
      declarantAddress = traderAddress,
      defermentType = None,
      defermentAccountNumber = None,
      additionalDefermentNumber = None
    )

    val submissionResponse = SubmissionResponse("1234")

    "return the Right response" in {
      setupMockHttpPost(target.postSubmissionUrl)(Right(submissionResponse))
      await(target.postSubmission(submission)) mustBe Right(submissionResponse)
    }
  }

}
