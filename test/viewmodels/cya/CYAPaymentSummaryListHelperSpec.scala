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
import models._
import models.importDetails._
import models.reasons.{BoxNumber, UnderpaymentReason}
import models.requests._
import models.underpayments.UnderpaymentDetail
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import pages.contactDetails._
import pages.docUpload.FileUploadPage
import pages.importDetails._
import pages.paymentInfo._
import pages.reasons.{MoreInformationPage, UnderpaymentReasonsPage}
import pages.underpayments.UnderpaymentDetailSummaryPage
import views.data.cya.CheckYourAnswersPaymentData._

import java.time.{LocalDate, LocalDateTime}

class CYAPaymentSummaryListHelperSpec
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
      .set(TraderAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
      .set(OneCustomsProcedureCodePage, true).success.value
      .set(EnterCustomsProcedureCodePage, "4000C09").success.value
      .set(DefermentPage, true).success.value
      .set(ImporterEORIExistsPage, true).success.value
      .set(ImporterEORINumberPage, "GB345834921000").success.value
      .set(ImporterVatRegisteredPage, true).success.value
      .set(UserTypePage, UserType.Representative).success.value
      .set(ImporterNamePage, "First Second").success.value
      .set(ImporterAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
      .set(
        UnderpaymentDetailSummaryPage,
        Seq(UnderpaymentDetail("B00", 0.0, 1.0), UnderpaymentDetail("A00", 0.0, 1.0))
      ).success.value
      .set(
        UnderpaymentReasonsPage,
        Seq(UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60"))
      ).success.value
      .set(MoreInformationPage, "Stock losses in warehouse.").success.value
      .set(SplitPaymentPage, true).success.value
      .set(DefermentAccountPage, "1284958").success.value
      .set(DefermentTypePage, "B").success.value
      .set(
        UploadAuthorityPage,
        Seq(
          UploadAuthority(
            "1284958",
            SelectedDutyTypes.Duty,
            FileUploadInfo(
              "file-ref-1",
              "DutyFileExample.pdf",
              "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
              LocalDateTime.now,
              "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
              "application/pdf"
            )
          ),
          UploadAuthority(
            "5293747",
            SelectedDutyTypes.Vat,
            FileUploadInfo(
              "file-ref-1",
              "VATFileExample.pdf",
              "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
              LocalDateTime.now,
              "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
              "application/pdf"
            )
          )
        )
      ).success.value
      .set(AdditionalDefermentNumberPage, "5293747").success.value
      .set(AdditionalDefermentTypePage, "B").success.value

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

  "Representative wants to split deferment payment and pay by DAN type B for both accounts" when {

    "buildPaymentDetails" should {

      "produce a valid model when all answers are provided" in new Test {
        buildPaymentDetailsSummaryList mustBe Seq(
          paymentDetailsAnswers(Seq(paymentMethodDefermentRow, splitDefermentYesRow))
        )
      }
    }

    "buildDefermentDuty" should {

      "produce a valid model when all answers are provided" in new Test {
        buildDefermentDutySummaryList mustBe Seq(
          defermentDutyAnswers(Seq(repAccountNumberDutyRow, accountOwnerTypeBDutyRow, proofOfAuthorityDuty))
        )
      }
    }

    "buildDefermentVAT" should {

      "produce a valid model when all answers are provided" in new Test {
        buildDefermentImportVatSummaryList mustBe Seq(
          defermentVATAnswers(Seq(repAccountNumberVATRow, accountOwnerTypeBVATRow, proofOfAuthorityVat))
        )
      }
    }

  }

  "Representative chooses to not pay by deferment" when {

    "buildPaymentDetails" should {

      "produce a valid model when all answers are provided" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(DefermentPage, false).success.value

        buildPaymentDetailsSummaryList mustBe Seq(paymentDetailsAnswers(Seq(paymentMethodOtherRow)))
      }
    }

  }

  "Representative wants to split deferment payment and chooses DAN type A for Duty and DAN type C for VAT" when {

    "buildPaymentDetails, buildDefermentDuty, buildDefermentVAT" should {

      "produce a valid model when all answers are provided" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
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
          .set(
            DeclarantContactDetailsPage,
            ContactDetails("First Second", "email@email.com", "1234567890")
          ).success.value
          .set(TraderAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(OneCustomsProcedureCodePage, true).success.value
          .set(EnterCustomsProcedureCodePage, "4000C09").success.value
          .set(DefermentPage, true).success.value
          .set(ImporterEORIExistsPage, true).success.value
          .set(ImporterEORINumberPage, "GB345834921000").success.value
          .set(ImporterVatRegisteredPage, true).success.value
          .set(UserTypePage, UserType.Representative).success.value
          .set(ImporterNamePage, "First Second").success.value
          .set(ImporterAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(
            UnderpaymentDetailSummaryPage,
            Seq(UnderpaymentDetail("B00", 0.0, 1.0), UnderpaymentDetail("A00", 0.0, 1.0))
          ).success.value
          .set(
            UnderpaymentReasonsPage,
            Seq(UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60"))
          ).success.value
          .set(MoreInformationPage, "Stock losses in warehouse.").success.value
          .set(SplitPaymentPage, true).success.value
          .set(DefermentAccountPage, "1284958").success.value
          .set(DefermentTypePage, "A").success.value
          .set(AdditionalDefermentNumberPage, "5293747").success.value
          .set(AdditionalDefermentTypePage, "C").success.value

        buildPaymentDetailsSummaryList mustBe Seq(
          paymentDetailsAnswers(Seq(paymentMethodDefermentRow, splitDefermentYesRow))
        )
        buildDefermentDutySummaryList mustBe Seq(
          defermentDutyAnswers(Seq(repAccountNumberDutyRow, accountOwnerTypeADutyRow))
        )
        buildDefermentImportVatSummaryList mustBe Seq(
          defermentVATAnswers(Seq(repAccountNumberVATRow, accountOwnerTypeCVATRow))
        )
      }
    }

  }

  "Representative wants to split deferment payment and chooses DAN type C for Duty and DAN type A for VAT" when {

    "buildPaymentDetails, buildDefermentDuty, buildDefermentVAT" should {

      "produce a valid model when all answers are provided" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
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
          .set(
            DeclarantContactDetailsPage,
            ContactDetails("First Second", "email@email.com", "1234567890")
          ).success.value
          .set(TraderAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(OneCustomsProcedureCodePage, true).success.value
          .set(EnterCustomsProcedureCodePage, "4000C09").success.value
          .set(DefermentPage, true).success.value
          .set(ImporterEORIExistsPage, true).success.value
          .set(ImporterEORINumberPage, "GB345834921000").success.value
          .set(ImporterVatRegisteredPage, true).success.value
          .set(UserTypePage, UserType.Representative).success.value
          .set(ImporterNamePage, "First Second").success.value
          .set(ImporterAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(
            UnderpaymentDetailSummaryPage,
            Seq(UnderpaymentDetail("B00", 0.0, 1.0), UnderpaymentDetail("A00", 0.0, 1.0))
          ).success.value
          .set(
            UnderpaymentReasonsPage,
            Seq(UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60"))
          ).success.value
          .set(MoreInformationPage, "Stock losses in warehouse.").success.value
          .set(SplitPaymentPage, true).success.value
          .set(DefermentAccountPage, "1284958").success.value
          .set(DefermentTypePage, "C").success.value
          .set(AdditionalDefermentNumberPage, "5293747").success.value
          .set(AdditionalDefermentTypePage, "A").success.value

        buildPaymentDetailsSummaryList mustBe Seq(
          paymentDetailsAnswers(Seq(paymentMethodDefermentRow, splitDefermentYesRow))
        )
        buildDefermentDutySummaryList mustBe Seq(
          defermentDutyAnswers(Seq(repAccountNumberDutyRow, accountOwnerTypeCDutyRow))
        )
        buildDefermentImportVatSummaryList mustBe Seq(
          defermentVATAnswers(Seq(repAccountNumberVATRow, accountOwnerTypeAVATRow))
        )
      }
    }

  }

  "Representative one duty type chooses DAN type B" when {

    "buildPaymentDetails" should {

      "produce a valid model when all answers are provided" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
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
          .set(
            DeclarantContactDetailsPage,
            ContactDetails("First Second", "email@email.com", "1234567890")
          ).success.value
          .set(TraderAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(OneCustomsProcedureCodePage, true).success.value
          .set(EnterCustomsProcedureCodePage, "4000C09").success.value
          .set(DefermentPage, true).success.value
          .set(ImporterEORIExistsPage, true).success.value
          .set(ImporterEORINumberPage, "GB345834921000").success.value
          .set(ImporterVatRegisteredPage, true).success.value
          .set(UserTypePage, UserType.Representative).success.value
          .set(ImporterNamePage, "First Second").success.value
          .set(ImporterAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
          .set(
            UnderpaymentReasonsPage,
            Seq(UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60"))
          ).success.value
          .set(MoreInformationPage, "Stock losses in warehouse.").success.value
          .set(DefermentAccountPage, "1284958").success.value
          .set(DefermentTypePage, "B").success.value
          .set(
            UploadAuthorityPage,
            Seq(
              UploadAuthority(
                "1284958",
                SelectedDutyTypes.Duty,
                FileUploadInfo(
                  "file-ref-1",
                  "DutyFileExample.pdf",
                  "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
                  LocalDateTime.now,
                  "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
                  "application/pdf"
                )
              )
            )
          ).success.value

        buildPaymentDetailsSummaryList mustBe Seq(
          paymentDetailsAnswers(
            Seq(
              paymentMethodDefermentRow,
              repAccountNumberRow,
              accountOwnerRow,
              proofOfAuthority
            )
          )
        )

      }
    }

  }

  "Representative does not want to split deferment payment and chooses DAN type B" when {

    "buildPaymentDetails" should {

      "produce a valid model when all answers are provided" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
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
          .set(
            DeclarantContactDetailsPage,
            ContactDetails("First Second", "email@email.com", "1234567890")
          ).success.value
          .set(TraderAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(OneCustomsProcedureCodePage, true).success.value
          .set(EnterCustomsProcedureCodePage, "4000C09").success.value
          .set(DefermentPage, true).success.value
          .set(ImporterEORIExistsPage, true).success.value
          .set(ImporterEORINumberPage, "GB345834921000").success.value
          .set(ImporterVatRegisteredPage, true).success.value
          .set(UserTypePage, UserType.Representative).success.value
          .set(ImporterNamePage, "First Second").success.value
          .set(ImporterAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(
            UnderpaymentDetailSummaryPage,
            Seq(UnderpaymentDetail("B00", 0.0, 1.0), UnderpaymentDetail("A00", 0.0, 1.0))
          ).success.value
          .set(
            UnderpaymentReasonsPage,
            Seq(UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60"))
          ).success.value
          .set(MoreInformationPage, "Stock losses in warehouse.").success.value
          .set(SplitPaymentPage, false).success.value
          .set(DefermentAccountPage, "1284958").success.value
          .set(DefermentTypePage, "B").success.value
          .set(
            UploadAuthorityPage,
            Seq(
              UploadAuthority(
                "1284958",
                SelectedDutyTypes.Both,
                FileUploadInfo(
                  "file-ref-1",
                  "FileExample.pdf",
                  "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
                  LocalDateTime.now,
                  "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
                  "application/pdf"
                )
              )
            )
          ).success.value

        buildPaymentDetailsSummaryList mustBe Seq(
          paymentDetailsAnswers(
            Seq(
              paymentMethodDefermentRow,
              splitDefermentNoRow,
              repAccountNumberRow,
              accountOwnerRow,
              proofOfAuthorityBoth
            )
          )
        )

      }
    }

  }

  "Importer chooses not to pay by deferment" when {

    "buildPaymentDetails" should {

      "produce a valid model when all answers are provided" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Importer).success.value
          .set(DefermentPage, false).success.value

        buildPaymentDetailsSummaryList mustBe Seq(
          paymentDetailsAnswers(
            Seq(
              paymentMethodOtherRow
            )
          )
        )

      }
    }

  }

  "Importer chooses to pay by deferment" when {

    "buildPaymentDetails" should {

      "produce a valid model when all answers are provided" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("some-cred-id")
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
          .set(
            DeclarantContactDetailsPage,
            ContactDetails("First Second", "email@email.com", "1234567890")
          ).success.value
          .set(TraderAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(OneCustomsProcedureCodePage, true).success.value
          .set(EnterCustomsProcedureCodePage, "4000C09").success.value
          .set(DefermentPage, true).success.value
          .set(ImporterEORIExistsPage, true).success.value
          .set(ImporterEORINumberPage, "GB345834921000").success.value
          .set(ImporterVatRegisteredPage, true).success.value
          .set(UserTypePage, UserType.Importer).success.value
          .set(ImporterNamePage, "First Second").success.value
          .set(ImporterAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
          .set(
            UnderpaymentReasonsPage,
            Seq(UnderpaymentReason(boxNumber = BoxNumber.Box22, original = "50", amended = "60"))
          ).success.value
          .set(MoreInformationPage, "Stock losses in warehouse.").success.value
          .set(DefermentAccountPage, "1284958").success.value

        buildPaymentDetailsSummaryList mustBe Seq(
          paymentDetailsAnswers(Seq(paymentMethodDefermentRow, importerAccountNumberRow))
        )
      }

      "produce a valid model when no answers are provided" in new Test {
        override val userAnswers: UserAnswers = UserAnswers("")
        buildPaymentDetailsSummaryList mustBe List.empty
      }

    }

  }

}
