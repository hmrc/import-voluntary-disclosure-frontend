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
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.{FileUploadInfo, UserAnswers}
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages.shared.MoreDocumentationPage
import pages.updateCase.{DisclosureReferenceNumberPage, UpdateAdditionalInformationPage, UploadSupportingDocumentationPage}
import play.api.mvc.AnyContentAsEmpty
import views.data.cya.CancelCaseCheckYourAnswersData._

import java.time.LocalDateTime

class CYACancelCaseSummaryListHelperSpec
    extends SpecBase
    with Matchers
    with TryValues
    with OptionValues
    with CYACancelCaseSummaryListHelper {
  trait Test {

    val userAnswers: UserAnswers = UserAnswers("some-cred-id")
      .set(DisclosureReferenceNumberPage, "C184567898765333333333").success.value
      .set(UpdateAdditionalInformationPage, "Hello World").success.value
      .set(MoreDocumentationPage, true).success.value
      .set(
        UploadSupportingDocumentationPage,
        Seq(
          FileUploadInfo(
            "file-ref-1",
            "Example.pdf",
            "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
            LocalDateTime.now,
            "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
            "application/pdf"
          )
        )
      ).success.value

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

  "CYAPaymentDetailsSummaryListHelper" must {
    "display a valid model when all answers are provided with supporting documents" in new Test {
      buildCancelCaseSummaryList mustBe Seq(
        cancelCaseAnswers(
          Seq(
            referenceNumberRow,
            reasonCancellation,
            supportingDocumentationRow(true),
            fileUploadRow
          )
        )
      )
    }

    "display a valid model when all answers are provided" in new Test {
      override val userAnswers = UserAnswers("some-cred-id")
        .set(DisclosureReferenceNumberPage, "C184567898765333333333").success.value
        .set(UpdateAdditionalInformationPage, "Hello World").success.value
        .set(MoreDocumentationPage, false).success.value
        .set(
          UploadSupportingDocumentationPage,
          Seq(
            FileUploadInfo(
              "file-ref-1",
              "Example.pdf",
              "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
              LocalDateTime.now,
              "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
              "application/pdf"
            ),
            FileUploadInfo(
              "file-ref-2",
              "Example.pdf",
              "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
              LocalDateTime.now,
              "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
              "application/pdf"
            )
          )
        ).success.value

      buildCancelCaseSummaryList mustBe Seq(
        cancelCaseAnswers(
          Seq(
            referenceNumberRow,
            reasonCancellation,
            supportingDocumentationRow(false),
            fileUploadRows
          )
        )
      )
    }
  }
}
