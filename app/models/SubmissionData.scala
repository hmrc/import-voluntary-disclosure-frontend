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

package models

import config.FixedConfig
import models.underpayments.UnderpaymentDetail
import pages.underpayments.UnderpaymentDetailSummaryPage
import pages._
import play.api.libs.json.Reads

case class SubmissionData(userType: UserType,
                          knownDetails: EoriDetails,
                          numEntries: NumberOfEntries,
                          acceptedBeforeBrexit: Boolean,
                          hasAdditionalInfo: Option[Boolean],
                          additionalInfo: Option[String],
                          entryDetails: Option[EntryDetails],
                          oneCpc: Option[Boolean],
                          originalCpc: Option[String],
                          declarantContactDetails: ContactDetails,
                          traderAddressCorrect: Boolean,
                          traderAddress: ContactAddress,
                          importerEoriExists: Option[Boolean],
                          importerEori: Option[String],
                          importerName: Option[String],
                          importerAddress: Option[ContactAddress],
                          paymentByDeferment: Boolean,
                          defermentType: Option[String],
                          defermentAccountNumber: Option[String],
                          additionalDefermentAccountNumber: Option[String],
                          additionalDefermentType: Option[String],
                          amendedItems: Option[Seq[UnderpaymentReason]],
                          underpaymentDetails: Seq[UnderpaymentDetail],
                          anyOtherSupportingDocs: Option[Boolean],
                          optionalDocumentsSupplied: Option[Seq[OptionalDocument]],
                          supportingDocuments: Seq[FileUploadInfo],
                          splitDeferment: Option[Boolean],
                          authorityDocuments: Option[Seq[UploadAuthority]],
                          isImporterVatRegistered: Option[Boolean]
                        )

object SubmissionData extends FixedConfig {

  implicit val reads: Reads[SubmissionData] =
    for {
      userType <- UserTypePage.path.read[UserType]
      knownDetails <- KnownEoriDetailsPage.path.read[EoriDetails]
      numEntries <- NumberOfEntriesPage.path.read[NumberOfEntries]
      acceptanceDate <- AcceptanceDatePage.path.read[Boolean]
      entryDetails <- EntryDetailsPage.path.readNullable[EntryDetails]
      oneCpc <- OneCustomsProcedureCodePage.path.readNullable[Boolean]
      originalCpc <- EnterCustomsProcedureCodePage.path.readNullable[String]
      declarantContactDetails <- DeclarantContactDetailsPage.path.read[ContactDetails]
      traderAddressCorrect <- TraderAddressCorrectPage.path.read[Boolean]
      traderAddress <- TraderAddressPage.path.read[ContactAddress]
      importerEoriExists <- ImporterEORIExistsPage.path.readNullable[Boolean]
      importerEori <- ImporterEORINumberPage.path.readNullable[String]
      importerName <- ImporterNamePage.path.readNullable[String]
      importerAddress <- ImporterAddressPage.path.readNullable[ContactAddress]
      underpaymentDetails <- UnderpaymentDetailSummaryPage.path.read[Seq[UnderpaymentDetail]]
      supportingDocuments <- FileUploadPage.path.read[Seq[FileUploadInfo]]
      paymentByDeferment <- DefermentPage.path.read[Boolean]
      defermentType <- DefermentTypePage.path.readNullable[String]
      defermentAccountNumber <- DefermentAccountPage.path.readNullable[String]
      additionalDefermentNumber <- AdditionalDefermentNumberPage.path.readNullable[String]
      additionalDefermentType <- AdditionalDefermentTypePage.path.readNullable[String]
      hasAdditionalInfo <- HasFurtherInformationPage.path.readNullable[Boolean]
      additionalInfo <- MoreInformationPage.path.readNullable[String]
      amendedItems <- UnderpaymentReasonsPage.path.readNullable[Seq[UnderpaymentReason]]
      splitDeferment <- SplitPaymentPage.path.readNullable[Boolean]
      authorityDocuments <- UploadAuthorityPage.path.readNullable[Seq[UploadAuthority]]
      anyOtherSupportingDocs <- AnyOtherSupportingDocsPage.path.readNullable[Boolean]
      optionalDocumentsSupplied <- OptionalSupportingDocsPage.path.readNullable[Seq[OptionalDocument]]
      isImporterVatRegistered <- ImporterVatRegisteredPage.path.readNullable[Boolean]
    } yield {
      SubmissionData(
        userType = userType,
        knownDetails = knownDetails,
        numEntries = numEntries,
        acceptedBeforeBrexit = acceptanceDate,
        entryDetails = entryDetails,
        oneCpc = oneCpc,
        originalCpc = originalCpc,
        declarantContactDetails = declarantContactDetails,
        traderAddressCorrect = traderAddressCorrect,
        traderAddress = traderAddress,
        importerEoriExists = importerEoriExists,
        importerEori = importerEori,
        importerName = importerName,
        importerAddress = importerAddress,
        underpaymentDetails = underpaymentDetails,
        supportingDocuments = supportingDocuments,
        paymentByDeferment = paymentByDeferment,
        defermentType = defermentType,
        defermentAccountNumber = defermentAccountNumber,
        additionalDefermentAccountNumber = additionalDefermentNumber,
        additionalDefermentType = additionalDefermentType,
        hasAdditionalInfo = hasAdditionalInfo,
        additionalInfo = additionalInfo,
        amendedItems = amendedItems,
        splitDeferment = splitDeferment,
        authorityDocuments = authorityDocuments,
        anyOtherSupportingDocs = anyOtherSupportingDocs,
        optionalDocumentsSupplied = optionalDocumentsSupplied,
        isImporterVatRegistered = isImporterVatRegistered
      )
    }
}

