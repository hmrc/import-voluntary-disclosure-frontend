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

package services

import base.SpecBase
import mocks.connectors.MockIvdSubmissionConnector
import models.{ErrorModel, SubmissionData, SubmissionResponse, UserAnswers}
import pages.{AcceptanceDatePage, DeclarantContactDetailsPage, DefermentAccountPage, DefermentPage, EnterCustomsProcedureCodePage, EntryDetailsPage, FileUploadPage, KnownEoriDetails, MoreInformationPage, NumberOfEntriesPage, OneCustomsProcedureCodePage, OptionalSupportingDocsPage, SplitPaymentPage, TraderAddressCorrectPage, TraderAddressPage, UnderpaymentReasonsPage, UserTypePage}
import utils.ReusableValues


class SubmissionServiceSpec extends SpecBase with MockIvdSubmissionConnector with ReusableValues {

  def setup(response: Either[ErrorModel,SubmissionResponse]): SubmissionService = {
    setupMockPostSubmission(response)
    new SubmissionService(mockIVDSubmissionConnector)
  }

  val completeSubmission: SubmissionData = SubmissionData(
    userType = representative,
    knownDetails = eoriDetails,
    numEntries = oneEntry,
    acceptedBeforeBrexit = true,
    additionalInfo = "Additional information",
    entryDetails = entryDetails,
    originalCpc = cpc,
    declarantContactDetails = contactDetails,
    traderAddress = addressDetails,
    importerEori = Some(eoriDetails.eori),
    importerName = Some("Joe Bloggs"),
    importerAddress = Some(addressDetails),
    paymentByDeferment = true,
    defermentType = Some("C"),
    defermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentAccountNumber = Some(defermentAccountNumber),
    additionalDefermentType = Some("D"),
    amendedItems = underpaymentReasons,
    underpaymentDetails = someUnderpaymentDetailsSelected,
    optionalDocumentsSupplied = None,
    supportingDocuments = supportingDocuments,
    splitDeferment = true,
    authorityDocuments = authorityDocuments,
    isImporterVatRegistered = Some(true)
  )
  val completeUserAnswers: UserAnswers = (for {
    answers <- new UserAnswers("some-cred-id").set(UserTypePage, completeSubmission.userType)
    answers <- answers.set(EntryDetailsPage, completeSubmission.entryDetails)
    answers <- answers.set(KnownEoriDetails, completeSubmission.knownDetails)
    answers <- answers.set(NumberOfEntriesPage, completeSubmission.numEntries)
    answers <- answers.set(AcceptanceDatePage, completeSubmission.acceptedBeforeBrexit)
    answers <- answers.set(TraderAddressCorrectPage, true)
    answers <- answers.set(DeclarantContactDetailsPage, completeSubmission.declarantContactDetails)
    answers <- answers.set(TraderAddressPage, completeSubmission.traderAddress)
    answers <- answers.set(OneCustomsProcedureCodePage, true)
    answers <- answers.set(EnterCustomsProcedureCodePage, completeSubmission.originalCpc)
    answers <- answers.set(FileUploadPage, completeSubmission.supportingDocuments)
    answers <- answers.set(DefermentPage, true)
    answers <- answers.set(DefermentAccountPage, completeSubmission.defermentAccountNumber.getOrElse(defermentAccountNumber))
    answers <- answers.set(MoreInformationPage, completeSubmission.additionalInfo)
    answers <- answers.set(UnderpaymentReasonsPage, completeSubmission.amendedItems)
    answers <- answers.set(SplitPaymentPage, completeSubmission.splitDeferment)
    answers <- answers.set(OptionalSupportingDocsPage, optionalSupportingDocuments)
  } yield answers).getOrElse(new UserAnswers("some-cred-id"))


  "createCase" when {
    "called with a valid User Answers" should {
      lazy val service = setup(Right(eoriDetails))
      lazy val result = service.retrieveEoriDetails(idOne)

      "return successful RetrieveEoriDetailsResponse" in {
        await(result) mustBe Right(eoriDetails)
      }
    }
  }

}
