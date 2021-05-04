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

import java.time.{LocalDate, LocalDateTime}

import base.SpecBase
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.{ContactAddress, ContactDetails, EntryDetails, FileUploadInfo, NumberOfEntries, UserAnswers, UserType}
import org.scalatest.{MustMatchers, OptionValues, TryValues}
import pages._
import views.data.CheckYourAnswersData._


class CYASummaryListHelperSpec extends SpecBase with MustMatchers with TryValues with OptionValues with CYASummaryListHelper {

  trait Test {


    val userAnswers: UserAnswers = UserAnswers("some-cred-id")
      .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
      .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate.of(2020, 12, 1))).success.value
      .set(AcceptanceDatePage, true).success.value
      .set(FileUploadPage, Seq(FileUploadInfo(
        "Example.pdf",
        "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
        LocalDateTime.now,
        "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
        "application/pdf"))).success.value
      .set(DeclarantContactDetailsPage, ContactDetails(
        "First Second",
        "email@email.com",
        "1234567890")).success.value
      .set(TraderAddressPage, ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")).success.value
      .set(EnterCustomsProcedureCodePage, "4000C09").success.value
      .set(DefermentPage, false).success.value
      .set(ImporterEORIExistsPage, true).success.value
      .set(ImporterEORINumberPage, "GB345834921000").success.value
      .set(ImporterVatRegisteredPage, true).success.value
      .set(UserTypePage, UserType.Representative).success.value
      .set(ImporterNamePage, "First Second").success.value
      .set(ImporterAddressPage, ContactAddress(
        "21 Street", None, "London", Some("SN6PY"), "UK")).success.value

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

  "buildEntryDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildEntryDetailsSummaryList mustBe Seq(entryDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildEntryDetailsSummaryList mustBe List.empty
    }

  }

  "buildUnderpaymentDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildUnderpaymentDetailsSummaryList mustBe Seq(underpaymentDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildUnderpaymentDetailsSummaryList mustBe List.empty
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

  "buildPaymentDetails" should {

    "produce a valid model when all answers are provided" in new Test {
      buildPaymentDetailsSummaryList mustBe Seq(paymentDetailsAnswers)
    }

    "produce a valid model when no answers are provided" in new Test {
      override val userAnswers: UserAnswers = UserAnswers("")
      buildPaymentDetailsSummaryList mustBe List.empty
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
        .set(ImporterAddressPage, ContactAddress(
          "21 Street", None, "London", Some("SN6PY"), "UK")).success.value

      buildImporterDetailsSummaryList mustBe List.empty
    }

  }
}