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
                          additionalInfo: String = "Not Applicable",
                          entryDetails: EntryDetails,
                          originalCpc: String,
                          declarantContactDetails: ContactDetails,
                          traderAddress: ContactAddress,
                          importerEori: Option[String] = None,
                          importerName: Option[String] = None,
                          importerAddress: Option[ContactAddress] = None,
                          paymentByDeferment: Boolean,
                          defermentType: Option[String] = None,
                          defermentAccountNumber: Option[String] = None,
                          additionalDefermentAccountNumber: Option[String] = None,
                          additionalDefermentType: Option[String] = None,
                          amendedItems: Seq[UnderpaymentReason] = Seq.empty,
                          underpaymentDetails: Seq[UnderpaymentDetail] = Seq.empty,
                          optionalDocumentsSupplied: Option[Seq[String]] = None,
                          supportingDocuments: Seq[FileUploadInfo] = Seq.empty,
                          splitDeferment: Boolean = false,
                          authorityDocuments: Seq[UploadAuthority] = Seq.empty,
                          isImporterVatRegistered: Option[Boolean]
                        )

object SubmissionData extends FixedConfig {

  implicit val reads: Reads[SubmissionData] =
    for {
      userType <- UserTypePage.path.read[UserType]
      knownDetails <- KnownEoriDetails.path.read[EoriDetails]
      numEntries <- NumberOfEntriesPage.path.read[NumberOfEntries]
      acceptanceDate <- AcceptanceDatePage.path.readNullable[Boolean]
      entryDetails <- EntryDetailsPage.path.read[EntryDetails]
      oneCpc <- OneCustomsProcedureCodePage.path.read[Boolean]
      originalCpc <- EnterCustomsProcedureCodePage.path.readNullable[String]
      declarantContactDetails <- DeclarantContactDetailsPage.path.read[ContactDetails]
      traderAddress <- TraderAddressPage.path.read[ContactAddress]
      importerEori <- ImporterEORINumberPage.path.readNullable[String]
      importerName <- ImporterNamePage.path.readNullable[String]
      importerAddress <- ImporterAddressPage.path.readNullable[ContactAddress]
      underpaymentDetails <- UnderpaymentDetailSummaryPage.path.readNullable[Seq[UnderpaymentDetail]]
      supportingDocuments <- FileUploadPage.path.read[Seq[FileUploadInfo]]
      paymentByDeferment <- DefermentPage.path.read[Boolean]
      defermentType <- DefermentTypePage.path.readNullable[String]
      defermentAccountNumber <- DefermentAccountPage.path.readNullable[String]
      additionalDefermentNumber <- AdditionalDefermentNumberPage.path.readNullable[String]
      additionalDefermentType <- AdditionalDefermentTypePage.path.readNullable[String]
      additionalInfo <- MoreInformationPage.path.readNullable[String]
      amendedItems <- UnderpaymentReasonsPage.path.read[Seq[UnderpaymentReason]]
      splitDeferment <- SplitPaymentPage.path.readNullable[Boolean]
      authorityDocuments <- UploadAuthorityPage.path.readNullable[Seq[UploadAuthority]]
      optionalDocumentsSupplied <- OptionalSupportingDocsPage.path.readNullable[Seq[String]]
      isImporterVatRegistered <- ImporterVatRegisteredPage.path.readNullable[Boolean]
    } yield {
      SubmissionData(
        userType = userType,
        knownDetails = knownDetails,
        numEntries = numEntries,
        acceptedBeforeBrexit = acceptanceDate.getOrElse(false),
        entryDetails = entryDetails,
        originalCpc = if (oneCpc) originalCpc.getOrElse("cpcError") else "VARIOUS",
        declarantContactDetails = declarantContactDetails,
        traderAddress = traderAddress,
        importerEori = importerEori,
        importerName = importerName,
        importerAddress = importerAddress,
        underpaymentDetails = underpaymentDetails.getOrElse(Seq.empty),
        supportingDocuments = supportingDocuments,
        paymentByDeferment = paymentByDeferment,
        defermentType = defermentType,
        defermentAccountNumber = defermentAccountNumber,
        additionalDefermentAccountNumber = additionalDefermentNumber,
        additionalDefermentType = additionalDefermentType,
        additionalInfo = additionalInfo.getOrElse("Not Applicable"),
        amendedItems = amendedItems,
        splitDeferment = splitDeferment.getOrElse(false),
        authorityDocuments = authorityDocuments.getOrElse(Seq.empty),
        optionalDocumentsSupplied = optionalDocumentsSupplied,
        isImporterVatRegistered = isImporterVatRegistered
      )
    }
}

