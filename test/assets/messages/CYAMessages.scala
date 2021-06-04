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

package messages

object CYAMessages extends BaseMessages {

  val title = "Check your answers before sending your disclosure"
  val heading = "Check your answers before sending your disclosure"
  val sendDisclosure = "Now send your disclosure"
  val disclosureConfirmation = "By sending this disclosure you are confirming that, to the best of your knowledge, the details you are providing are correct."
  val acceptAndSend = "Accept and send"

  val aboutImporterDetails = "About the Importer"
  val eoriNumberExists = "Importer has EORI number?"
  val eoriNumber = "Importer EORI number"
  val vatRegistered = "Importer VAT registered?"

  val entryDetails = "Entry details"
  val numberOfEntries = "Number of entries"
  val oneCustomsProcedureCode = "One customs procedure code?"
  val epu = "EPU"
  val epuChange = "Change entry details"
  val entryNumber = "Entry number"
  val entryDate = "Entry date"
  val acceptanceDate = "Acceptance date"

  val underpaymentDetails = "Underpayment details"
  val customsDuty = "Customs Duty"
  val importVAT = "Import VAT"
  val exciseDuty = "Excise Duty"

  val totalOwed = "Total owed to HMRC"
  val viewSummary = "View summary"
  val viewSummaryChange = "View underpayments summary"
  val reasonForUnderpayment = "Reason for underpayment"
  val viewReasonForUnderpayment = "View Reason for underpayment"
  val tellUsAnythingElse = "Tell us anything else?"
  val extraInformation = "Extra information"

  val amendmentDetails = "Amendment details"
  val cpc = "Customs procedure code"
  val cpcAmended = "Amended customs procedure code"
  val cpcChanged = "Customs procedure code changed?"
  val numAmendments = "Number of amendments"
  val supportingInformation = "Supporting information"
  def filesUploaded(numberOfFiles: Int): String = {
    if(numberOfFiles ==1) s"$numberOfFiles file uploaded" else s"$numberOfFiles files uploaded"
  }
  val yourDetails = "Your details"
  val name = "Name"
  val userType = "Importer or representative?"
  val contactDetails = "Contact details"
  val email = "Email address"
  val phone = "Telephone number"
  val address = "Address"

  val paymentDetails = "Payment details"
  val defermentInfoDuty = "Deferment account details for duty owed"
  val defermentInfoVAT = "Deferment account details for import VAT owed"
  val paymentMethod = "Payment method"
  val splitDeferment = "Split deferment payment?"
  val importerAccountNumber = "Deferment account number"
  val changeImporterAccountNumber = "Change Deferment account number"
  val repAccountNumber = "Account number"
  val accountOwner = "Account owner"
  val proofOfAuthority = "Proof of authority"



  val change = "Change"
  val changeAcceptanceDate = "Change acceptance date"
  val changeCpcExists = "Change one customs procedure code?"
  val changeEnterCpc = "Change customs procedure code"
  val changeContactDetails = "Change contact details"
  val changeImporterName = "Change importer name"
  val changeImporterAddress = "Change importer address"
  val changeImporterEoriExists = "Change importer has EORI number?"
  val changeImporterEoriNumber = "Change importer EORI number"
  val changeImporterVatRegistered = "Change importer VAT registered?"
  val changeSupportingDocuments = "Change supporting documents"
  val changeAddress = "Change address"
  val changeHasFurtherInformation = "Change Tell us anything else?"
  val changePaymentMethod = "Change payment method"
  val changeMoreInformation = "Change Extra information"

}
