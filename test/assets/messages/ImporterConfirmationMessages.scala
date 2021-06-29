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

object ImporterConfirmationMessages extends BaseMessages {

  val pageTitle = "Confirmation screen"
  val heading = "Disclosure sent"
  val referenceNumber = "Disclosure reference number:"
  val p1SingleEntry = "We have received your disclosure of underpaid customs duty or import VAT for the import declaration 123-123456Q-01/01/2021."
  val p1BulkEntry = "We have received your disclosure of underpaid customs duty or import VAT on multiple entries. All of these entries were for goods imported by Test User, EORI number GB123456789."
  val whatHappensNext = "What happens next"
  val p2Deferment = "We will check the information you have provided and process the payment using the deferment account details you have provided. We will then send you a C18 Post Clearance Demand Note in the post, usually within 10 days."
  val p3Deferment = "If we find that the amount owed differs from what you have told us, we will contact you before we process the payment."
  val p2OtherPayment = "We will check the information you have provided and send you a C18 Post Clearance Demand Note in the post, usually within 10 days."
  val p3OtherPayment = "The demand note will tell you how to pay. Payment must be made within 10 days of receiving the demand note."
  val p4OtherPayment = "If we find that the amount owed differs from what you have told us we will contact you before sending the demand note."
  val whatYouShouldDoNext = "What you should do next"
  val p5 = "If you have not received the demand note or we have not contacted you within 14 days then email customsaccountingrepayments@hmrc.gov.uk."
  val printSave = "Print or save this page"
  val printSaveRestOfMessage = "as we will not send you an email confirmation."
  val helpImproveServiceLink = "What did you think of this service? (takes 30 seconds)"

}
