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

package utils

import models.{SubmissionData, UserAnswers}
import pages.{AcceptanceDatePage, AdditionalDefermentNumberPage, AdditionalDefermentTypePage, AnyOtherSupportingDocsPage, DeclarantContactDetailsPage, DefermentAccountPage, DefermentPage, DefermentTypePage, EnterCustomsProcedureCodePage, EntryDetailsPage, FileUploadPage, HasFurtherInformationPage, ImporterAddressPage, ImporterEORIExistsPage, ImporterEORINumberPage, ImporterNamePage, ImporterVatRegisteredPage, KnownEoriDetails, MoreInformationPage, NumberOfEntriesPage, OneCustomsProcedureCodePage, OptionalSupportingDocsPage, SplitPaymentPage, TraderAddressCorrectPage, TraderAddressPage, UnderpaymentReasonsPage, UploadAuthorityPage, UserTypePage}
import pages.underpayments.UnderpaymentDetailSummaryPage

trait SubmissionServiceTestData extends ReusableValues {
  val completeSubmission: SubmissionData = SubmissionData(
    userType = representative,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    entryDetails = entryDetails,
    oneCpc = true,
    originalCpc = Some(cpc),
    declarantContactDetails = contactDetails,
    traderAddressCorrect = true,
    traderAddress = addressDetails,
    importerEoriExists = true,
    importerEori = Some(eoriDetails.eori),
    importerName = Some("Joe Bloggs"),
    importerAddress = Some(addressDetails),
    paymentByDeferment = true,
    defermentType = Some("C"),
    defermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentType = Some("B"),
    amendedItems = underpaymentReasons,
    hasAdditionalInfo = true,
    additionalInfo = Some("Additional information"),
    underpaymentDetails = someUnderpaymentDetailsSelected,
    anyOtherSupportingDocs = true,
    optionalDocumentsSupplied = Some(optionalSupportingDocuments),
    supportingDocuments = supportingDocuments,
    splitDeferment = Some(true),
    authorityDocuments = Some(authorityDocuments),
    isImporterVatRegistered = Some(true)
  )

  val completeUserAnswers: UserAnswers = (for {
    answers <- new UserAnswers("some-cred-id").set(UserTypePage, completeSubmission.userType)
    answers <- answers.set(KnownEoriDetails, completeSubmission.knownDetails)
    answers <- answers.set(NumberOfEntriesPage, completeSubmission.numEntries)
    answers <- answers.set(AcceptanceDatePage, completeSubmission.acceptedBeforeBrexit)
    answers <- answers.set(EntryDetailsPage, completeSubmission.entryDetails)
    answers <- answers.set(OneCustomsProcedureCodePage, completeSubmission.oneCpc)
    answers <- answers.set(EnterCustomsProcedureCodePage, completeSubmission.originalCpc.getOrElse("cpcError"))
    answers <- answers.set(DeclarantContactDetailsPage, completeSubmission.declarantContactDetails)
    answers <- answers.set(TraderAddressCorrectPage, completeSubmission.traderAddressCorrect)
    answers <- answers.set(TraderAddressPage, completeSubmission.traderAddress)
    answers <- answers.set(ImporterEORIExistsPage, completeSubmission.importerEoriExists)
    answers <- answers.set(ImporterEORINumberPage, completeSubmission.importerEori.getOrElse("GB021111240000"))
    answers <- answers.set(ImporterNamePage, completeSubmission.importerName.getOrElse("Joe Bloggs"))
    answers <- answers.set(ImporterAddressPage, completeSubmission.importerAddress.getOrElse(addressDetails))
    answers <- answers.set(DefermentPage, completeSubmission.paymentByDeferment)
    answers <- answers.set(DefermentTypePage, completeSubmission.defermentType.getOrElse("C"))
    answers <- answers.set(DefermentAccountPage, completeSubmission.defermentAccountNumber.getOrElse(defermentAccountNumber))
    answers <- answers.set(AdditionalDefermentTypePage, completeSubmission.additionalDefermentType.getOrElse("B"))
    answers <- answers.set(AdditionalDefermentNumberPage, completeSubmission.additionalDefermentAccountNumber.getOrElse(defermentAccountNumber))
    answers <- answers.set(UnderpaymentReasonsPage, completeSubmission.amendedItems)
    answers <- answers.set(HasFurtherInformationPage, completeSubmission.hasAdditionalInfo)
    answers <- answers.set(MoreInformationPage, completeSubmission.additionalInfo.getOrElse("Additional information"))
    answers <- answers.set(UnderpaymentDetailSummaryPage, completeSubmission.underpaymentDetails)
    answers <- answers.set(AnyOtherSupportingDocsPage, completeSubmission.anyOtherSupportingDocs)
    answers <- answers.set(OptionalSupportingDocsPage, completeSubmission.optionalDocumentsSupplied.getOrElse(optionalSupportingDocuments))
    answers <- answers.set(FileUploadPage, completeSubmission.supportingDocuments)
    answers <- answers.set(SplitPaymentPage, completeSubmission.splitDeferment.get)
    answers <- answers.set(UploadAuthorityPage, completeSubmission.authorityDocuments.getOrElse(Seq.empty))
    answers <- answers.set(ImporterVatRegisteredPage, completeSubmission.isImporterVatRegistered.getOrElse(true))
  } yield answers).getOrElse(new UserAnswers("some-cred-id"))

}
