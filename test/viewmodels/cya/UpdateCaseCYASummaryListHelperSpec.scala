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

package viewmodels.cya

import base.SpecBase
import models.SubmissionType.CreateCase
import models._
import models.requests._
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages.serviceEntry.WhatDoYouWantToDoPage
import pages.shared.MoreDocumentationPage
import pages.updateCase._
import play.api.mvc.AnyContentAsEmpty
import views.data.cya.UpdateCaseCheckYourAnswersData._

import java.time.Instant

class UpdateCaseCYASummaryListHelperSpec
    extends SpecBase
    with Matchers
    with TryValues
    with OptionValues
    with CYAUpdateCaseSummaryListHelper {

  trait Test {

    val userAnswers: UserAnswers = UserAnswers("some-cred-id")
      .set(WhatDoYouWantToDoPage, CreateCase).success.value
      .set(DisclosureReferenceNumberPage, "C184567898765333333333").success.value
      .set(MoreDocumentationPage, true).success.value
      .set(
        UploadSupportingDocumentationPage,
        Seq(
          FileUploadInfo(
            "file-ref-1",
            "Example.pdf",
            "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
            Instant.now,
            "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
            "application/pdf"
          )
        )
      ).success.value
      .set(UpdateAdditionalInformationPage, "Hello World").success.value

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

  }

  "buildUpdateCase with file uploaded" should {

    "produce a valid model when all answers are provided" in new Test {
      buildUpdateCaseSummaryList mustBe Seq(
        updateCaseAnswers(
          Seq(
            referenceNumberRow,
            moreDocumentationRow(true),
            fileUploadRow(),
            additionalInformationRow
          )
        )
      )
    }

    "produce a valid model when all answers are provided and has more than one file" in new Test {
      val ua: UserAnswers = userAnswers
        .set(
          UploadSupportingDocumentationPage,
          Seq(
            FileUploadInfo(
              "file-ref-1",
              "Example.pdf",
              "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
              Instant.now,
              "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
              "application/pdf"
            ),
            FileUploadInfo(
              "file-ref-2",
              "Example.pdf",
              "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
              Instant.now,
              "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
              "application/pdf"
            )
          )
        ).success.value

      val request = dataRequest.copy(userAnswers = ua)

      buildUpdateCaseSummaryList(messages, request) mustBe Seq(
        updateCaseAnswers(
          Seq(
            referenceNumberRow,
            moreDocumentationRow(true),
            fileUploadRow(s"$file<br/>$file", 2),
            additionalInformationRow
          )
        )
      )
    }
  }

  "buildUpdateCase with no file uploaded" should {

    "produce a valid model when all answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
        .set(WhatDoYouWantToDoPage, CreateCase).success.value
        .set(DisclosureReferenceNumberPage, "C184567898765333333333").success.value
        .set(MoreDocumentationPage, false).success.value
        .set(UpdateAdditionalInformationPage, "Hello World").success.value

      buildUpdateCaseSummaryList mustBe Seq(
        updateCaseAnswers(
          Seq(
            referenceNumberRow,
            moreDocumentationRow(false),
            additionalInformationRow
          )
        )
      )
    }
  }

}
