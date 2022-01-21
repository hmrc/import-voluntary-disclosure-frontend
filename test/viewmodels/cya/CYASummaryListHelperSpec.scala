/*
 * Copyright 2022 HM Revenue & Customs
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
import models.importDetails._
import models.reasons.{BoxNumber, UnderpaymentReason}
import models.requests._
import models.underpayments.UnderpaymentDetail
import models.{ContactAddress, ContactDetails, FileUploadInfo, UserAnswers}
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages.contactDetails._
import pages.docUpload.FileUploadPage
import pages.importDetails._
import pages.paymentInfo.DefermentPage
import pages.reasons.{MoreInformationPage, UnderpaymentReasonsPage}
import pages.underpayments.{PostponedVatAccountingPage, UnderpaymentDetailSummaryPage}
import views.data.CheckYourAnswersData._

import java.time.{LocalDate, LocalDateTime}

class CYASummaryListHelperSpec
    extends SpecBase
    with Matchers
    with TryValues
    with OptionValues
    with CYASummaryListHelper {

  trait Test {

    val userAnswers: UserAnswers = UserAnswers("some-cred-id")
      .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
      .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate.of(2020, 12, 1))).success.value
      .set(AcceptanceDatePage, true).success.value
      .set(
        FileUploadPage,
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
      .set(DeclarantContactDetailsPage, ContactDetails("First Second", "email@email.com", "1234567890")).success.value
      .set(TraderAddressPage, ContactAddress("21 Street", Some("Mayfair"), "London", Some("SN6PY"), "UK")).success.value
      .set(OneCustomsProcedureCodePage, true).success.value
      .set(EnterCustomsProcedureCodePage, "4000C09").success.value
      .set(DefermentPage, false).success.value
      .set(ImporterEORIExistsPage, true).success.value
      .set(ImporterEORINumberPage, "GB345834921000").success.value
      .set(ImporterVatRegisteredPage, true).success.value
      .set(UserTypePage, UserType.Representative).success.value
      .set(ImporterNamePage, "First Second").success.value
      .set(ImporterAddressPage, ContactAddress("21 Street", Some("Mayfair"), "London", None, "UK")).success.value
      .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
      .set(
        UnderpaymentReasonsPage,
        Seq(UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60"))
      ).success.value
      .set(MoreInformationPage, "Stock losses in warehouse.").success.value
      .set(PostponedVatAccountingPage, false).success.value

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

  "buildSummaryListForPrint" should {
    "produce rows without links" in new Test {
      buildSummaryListForPrint(caseId, dateSubmitted).flatMap(_.summaryList.rows.flatMap(_.actions)) mustBe Seq.empty
    }

    "produce a valid model for disclosure summary" in new Test {
      buildDisclosureSummaryList(caseId, dateSubmitted) mustBe disclosureSummaryList
    }
  }

  "buildEntryDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildEntryDetailsSummaryList mustBe Seq(entryDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildEntryDetailsSummaryList mustBe List.empty
    }

    "not produce Entry Details list when Number of Entries is Bulk" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
        .set(NumberOfEntriesPage, NumberOfEntries.MoreThanOneEntry).success.value
      buildEntryDetailsSummaryList mustBe List.empty
    }

  }

  "buildUnderpaymentDetails" should {

    "produce a valid model when all answers are provided for one entry" in new Test {
      buildUnderpaymentDetailsSummaryList mustBe Seq(underpaymentDetailsSingleAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildUnderpaymentDetailsSummaryList mustBe List.empty
    }

    "produce a valid model when all answers are provided for multiple entries" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
        .set(NumberOfEntriesPage, NumberOfEntries.MoreThanOneEntry).success.value
        .set(AcceptanceDatePage, true).success.value
        .set(
          FileUploadPage,
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
        .set(MoreInformationPage, "Stock losses in warehouse across multiple entries.").success.value
        .set(UserTypePage, UserType.Importer).success.value
        .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
        .set(DefermentPage, false).success.value
      buildUnderpaymentDetailsSummaryList mustBe Seq(underpaymentDetailsBulkAnswers)
    }

    "produce a valid model when Duty is used and PVA is not an option" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
        .set(NumberOfEntriesPage, NumberOfEntries.MoreThanOneEntry).success.value
        .set(AcceptanceDatePage, true).success.value
        .set(
          FileUploadPage,
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
        .set(MoreInformationPage, "Stock losses in warehouse across multiple entries.").success.value
        .set(UserTypePage, UserType.Importer).success.value
        .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
        .set(DefermentPage, false).success.value
      buildUnderpaymentDetailsSummaryList mustBe Seq(underpaymentDetailsBulkAnswers)
    }

  }

  "buildYourDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildYourDetailsSummaryList mustBe Seq(yourDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildYourDetailsSummaryList mustBe List.empty
    }

  }

  "buildImporterDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildImporterDetailsSummaryList mustBe Seq(importerDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildImporterDetailsSummaryList mustBe List.empty
    }

    "produce no model when userType is Importer" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
        .set(ImporterEORIExistsPage, true).success.value
        .set(ImporterEORINumberPage, "GB345834921000").success.value
        .set(ImporterVatRegisteredPage, true).success.value
        .set(UserTypePage, UserType.Importer).success.value
        .set(ImporterNamePage, "First Second").success.value
        .set(ImporterAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value

      buildImporterDetailsSummaryList mustBe List.empty
    }

  }
}
