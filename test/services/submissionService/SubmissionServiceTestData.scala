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

package services.submissionService

import models.importDetails.NumberOfEntries.MoreThanOneEntry
import models.{SubmissionData, UserAnswers}
import pages.contactDetails._
import pages.docUpload.{FileUploadPage, OptionalSupportingDocsPage}
import pages.importDetails._
import pages.paymentInfo._
import pages.reasons._
import pages.serviceEntry.KnownEoriDetailsPage
import pages.shared.AnyOtherSupportingDocsPage
import pages.underpayments.UnderpaymentDetailSummaryPage
import utils.ReusableValues

trait SubmissionServiceTestData extends ReusableValues {

  val completeSubmission: SubmissionData = SubmissionData(
    userType = representative,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = entryDetails,
    oneCpc = Some(true),
    originalCpc = Some(cpc),
    declarantContactDetails = contactDetails,
    traderAddressCorrect = true,
    traderAddress = addressDetails,
    importerEoriExists = Some(true),
    importerEori = Some(eoriDetails.eori),
    importerName = Some("Joe Bloggs"),
    importerAddress = Some(addressDetails),
    paymentByDeferment = true,
    defermentType = Some("B"),
    defermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentType = Some("B"),
    amendedItems = underpaymentReasons,
    additionalInfo = Some("Additional information"),
    underpaymentDetails = someUnderpaymentDetailsSelected(),
    anyOtherSupportingDocs = Some(true),
    optionalDocumentsSupplied = Some(optionalSupportingDocuments),
    supportingDocuments = supportingDocuments,
    splitDeferment = Some(true),
    authorityDocuments = Some(authorityDocuments),
    isImporterVatRegistered = Some(true)
  )

  val bulkCompleteSubmission: SubmissionData = completeSubmission.copy(
    oneCpc = None,
    numEntries = MoreThanOneEntry,
    entryDetails = None,
    amendedItems = None,
    anyOtherSupportingDocs = None,
    additionalInfo = Some("This is a bulk submission"),
    optionalDocumentsSupplied = None
  )

  val completeUserAnswers: UserAnswers = (for {
    answers <- new UserAnswers("some-cred-id").set(UserTypePage, completeSubmission.userType)
    answers <- answers.set(KnownEoriDetailsPage, completeSubmission.knownDetails)
    answers <- answers.set(NumberOfEntriesPage, completeSubmission.numEntries)
    answers <- answers.set(AcceptanceDatePage, completeSubmission.acceptedBeforeBrexit)
    answers <- answers.set(EntryDetailsPage, completeSubmission.entryDetails.get)
    answers <- answers.set(OneCustomsProcedureCodePage, completeSubmission.oneCpc.get)
    answers <- answers.set(EnterCustomsProcedureCodePage, completeSubmission.originalCpc.getOrElse("cpcError"))
    answers <- answers.set(DeclarantContactDetailsPage, completeSubmission.declarantContactDetails)
    answers <- answers.set(TraderAddressCorrectPage, completeSubmission.traderAddressCorrect)
    answers <- answers.set(TraderAddressPage, completeSubmission.traderAddress)
    answers <- answers.set(ImporterEORIExistsPage, completeSubmission.importerEoriExists.getOrElse(true))
    answers <- answers.set(ImporterEORINumberPage, completeSubmission.importerEori.getOrElse("GB021111240000"))
    answers <- answers.set(ImporterNamePage, completeSubmission.importerName.getOrElse("Joe Bloggs"))
    answers <- answers.set(ImporterAddressPage, completeSubmission.importerAddress.getOrElse(addressDetails))
    answers <- answers.set(DefermentPage, completeSubmission.paymentByDeferment)
    answers <- answers.set(DefermentTypePage, completeSubmission.defermentType.getOrElse("B"))
    answers <- answers.set(
      DefermentAccountPage,
      completeSubmission.defermentAccountNumber.getOrElse(defermentAccountNumber)
    )
    answers <- answers.set(AdditionalDefermentTypePage, completeSubmission.additionalDefermentType.getOrElse("B"))
    answers <- answers.set(
      AdditionalDefermentNumberPage,
      completeSubmission.additionalDefermentAccountNumber.getOrElse(defermentAccountNumber)
    )
    answers <- answers.set(UnderpaymentReasonsPage, completeSubmission.amendedItems.get)
    answers <- answers.set(MoreInformationPage, completeSubmission.additionalInfo.getOrElse("Additional information"))
    answers <- answers.set(UnderpaymentDetailSummaryPage, completeSubmission.underpaymentDetails)
    answers <- answers.set(AnyOtherSupportingDocsPage, completeSubmission.anyOtherSupportingDocs.get)
    answers <- answers.set(
      OptionalSupportingDocsPage,
      completeSubmission.optionalDocumentsSupplied.getOrElse(optionalSupportingDocuments)
    )
    answers <- answers.set(FileUploadPage, completeSubmission.supportingDocuments)
    answers <- answers.set(SplitPaymentPage, completeSubmission.splitDeferment.get)
    answers <- answers.set(UploadAuthorityPage, completeSubmission.authorityDocuments.getOrElse(Seq.empty))
    answers <- answers.set(ImporterVatRegisteredPage, completeSubmission.isImporterVatRegistered.getOrElse(true))
  } yield answers).getOrElse(new UserAnswers("some-cred-id"))

  val importerSubmission: SubmissionData = SubmissionData(
    userType = importer,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = entryDetails,
    oneCpc = Some(false),
    originalCpc = None,
    declarantContactDetails = contactDetails,
    traderAddressCorrect = true,
    traderAddress = addressDetails,
    importerEoriExists = None,
    importerEori = None,
    importerName = None,
    importerAddress = None,
    paymentByDeferment = false,
    defermentType = None,
    defermentAccountNumber = None,
    additionalDefermentAccountNumber = None,
    additionalDefermentType = None,
    amendedItems = underpaymentReasons,
    additionalInfo = None,
    underpaymentDetails = allUnderpaymentDetailsSelected(),
    anyOtherSupportingDocs = Some(false),
    optionalDocumentsSupplied = None,
    supportingDocuments = supportingDocuments,
    splitDeferment = None,
    authorityDocuments = None,
    isImporterVatRegistered = None
  )

  val bulkImporterSubmission: SubmissionData = importerSubmission.copy(
    oneCpc = None,
    numEntries = MoreThanOneEntry,
    entryDetails = None,
    amendedItems = None,
    anyOtherSupportingDocs = None,
    additionalInfo = Some("This is a bulk submission")
  )

  val importerDetailsNoEoriSubmission: SubmissionData = SubmissionData(
    userType = representative,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = entryDetails,
    oneCpc = Some(true),
    originalCpc = Some(cpc),
    declarantContactDetails = contactDetails,
    traderAddressCorrect = true,
    traderAddress = addressDetails,
    importerEoriExists = Some(false),
    importerEori = None,
    importerName = Some("Joe Bloggs"),
    importerAddress = Some(addressDetails),
    paymentByDeferment = true,
    defermentType = Some("B"),
    defermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentType = Some("B"),
    amendedItems = underpaymentReasons,
    additionalInfo = Some("Additional information"),
    underpaymentDetails = someUnderpaymentDetailsSelected(),
    anyOtherSupportingDocs = Some(true),
    optionalDocumentsSupplied = Some(optionalSupportingDocuments),
    supportingDocuments = supportingDocuments,
    splitDeferment = Some(false),
    authorityDocuments = Some(authorityDocuments),
    isImporterVatRegistered = None
  )

  val importerDefermentSubmission: SubmissionData = SubmissionData(
    userType = importer,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = entryDetails,
    oneCpc = Some(false),
    originalCpc = None,
    declarantContactDetails = contactDetails,
    traderAddressCorrect = true,
    traderAddress = addressDetails,
    importerEoriExists = None,
    importerEori = None,
    importerName = None,
    importerAddress = None,
    paymentByDeferment = true,
    defermentType = None,
    defermentAccountNumber = Some("1234567"),
    additionalDefermentAccountNumber = None,
    additionalDefermentType = None,
    amendedItems = underpaymentReasons,
    additionalInfo = None,
    underpaymentDetails = allUnderpaymentDetailsSelected(),
    anyOtherSupportingDocs = Some(false),
    optionalDocumentsSupplied = None,
    supportingDocuments = supportingDocuments,
    splitDeferment = None,
    authorityDocuments = None,
    isImporterVatRegistered = None
  )

  val representativeNoSplitDefermentSubmission: SubmissionData = SubmissionData(
    userType = representative,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = entryDetails,
    oneCpc = Some(true),
    originalCpc = Some(cpc),
    declarantContactDetails = contactDetails,
    traderAddressCorrect = true,
    traderAddress = addressDetails,
    importerEoriExists = Some(true),
    importerEori = Some(eoriDetails.eori),
    importerName = Some("Joe Bloggs"),
    importerAddress = Some(addressDetails),
    paymentByDeferment = true,
    defermentType = Some("B"),
    defermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentAccountNumber = None,
    additionalDefermentType = None,
    amendedItems = underpaymentReasons,
    additionalInfo = Some("Additional information"),
    underpaymentDetails = someUnderpaymentDetailsSelected(),
    anyOtherSupportingDocs = Some(true),
    optionalDocumentsSupplied = Some(optionalSupportingDocuments),
    supportingDocuments = supportingDocuments,
    splitDeferment = Some(false),
    authorityDocuments = Some(authorityDocuments),
    isImporterVatRegistered = Some(true)
  )

  val dutyOnlyDefermentSubmission: SubmissionData = SubmissionData(
    userType = representative,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = entryDetails,
    oneCpc = Some(true),
    originalCpc = Some(cpc),
    declarantContactDetails = contactDetails,
    traderAddressCorrect = true,
    traderAddress = addressDetails,
    importerEoriExists = Some(true),
    importerEori = Some(eoriDetails.eori),
    importerName = Some("Joe Bloggs"),
    importerAddress = Some(addressDetails),
    paymentByDeferment = true,
    defermentType = Some("B"),
    defermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentAccountNumber = None,
    additionalDefermentType = None,
    amendedItems = underpaymentReasons,
    additionalInfo = Some("Additional information"),
    underpaymentDetails = someUnderpaymentDetailsSelected(),
    anyOtherSupportingDocs = Some(true),
    optionalDocumentsSupplied = Some(Seq.empty),
    supportingDocuments = supportingDocuments,
    splitDeferment = Some(true),
    authorityDocuments = Some(authorityDocuments),
    isImporterVatRegistered = Some(true)
  )

  val vatOnlyDefermentSubmission: SubmissionData = SubmissionData(
    userType = representative,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = entryDetails,
    oneCpc = Some(true),
    originalCpc = Some(cpc),
    declarantContactDetails = contactDetails,
    traderAddressCorrect = true,
    traderAddress = addressDetails,
    importerEoriExists = Some(true),
    importerEori = Some(eoriDetails.eori),
    importerName = Some("Joe Bloggs"),
    importerAddress = Some(addressDetails),
    paymentByDeferment = true,
    defermentType = Some("C"),
    defermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentType = Some("B"),
    amendedItems = underpaymentReasons,
    additionalInfo = Some("Additional information"),
    underpaymentDetails = someUnderpaymentDetailsSelected(),
    anyOtherSupportingDocs = Some(true),
    optionalDocumentsSupplied = Some(Seq.empty),
    supportingDocuments = supportingDocuments,
    splitDeferment = Some(true),
    authorityDocuments = Some(vatAuthorityDocuments),
    isImporterVatRegistered = Some(true)
  )

}
