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

package viewmodels.cya

import base.SpecBase
import models.WhatDoYouWantToDo.CreateOption
import models._
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages._
import pages.serviceEntry.WhatDoYouWantToDoPage
import views.data.cya.UpdateCaseCheckYourAnswersData._

import java.time.LocalDateTime

class UpdateCaseCYASummaryListHelperSpec extends SpecBase with Matchers with TryValues with OptionValues with CYAUpdateCaseSummaryListHelper {

  trait Test {

    val userAnswers: UserAnswers = UserAnswers("some-cred-id")
      .set(WhatDoYouWantToDoPage, CreateOption).success.value
      .set(DisclosureReferenceNumberPage, "C184567898765333333333").success.value
      .set(MoreDocumentationPage, true).success.value
      .set(UploadSupportingDocumentationPage, Seq(FileUploadInfo(
        "file-ref-1",
        "Example.pdf",
        "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
        LocalDateTime.now,
        "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
        "application/pdf"))).success.value
      .set(UpdateAdditionalInformationPage, "Hello World").success.value

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

  }

  "buildUpdateCase with file uploaded" should {

    "produce a valid model when all answers are provided" in new Test {
      buildUpdateCaseSummaryList mustBe Seq(updateCaseAnswers(
        Seq(
          referenceNumberRow,
          moreDocumentationRow(true),
          fileUploadRow,
          additionalInformationRow
        )))
    }
  }

  "buildUpdateCase with no file uploaded" should {

    "produce a valid model when all answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
        .set(WhatDoYouWantToDoPage, CreateOption).success.value
        .set(DisclosureReferenceNumberPage, "C184567898765333333333").success.value
        .set(MoreDocumentationPage, false).success.value
        .set(UpdateAdditionalInformationPage, "Hello World").success.value

      buildUpdateCaseSummaryList mustBe Seq(updateCaseAnswers(
        Seq(
          referenceNumberRow,
          moreDocumentationRow(false),
          additionalInformationRow
        )))
    }
  }

}
