/*
 * Copyright 2025 HM Revenue & Customs
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

package messages.underpayments

import messages.BaseMessages

object ChangeUnderpaymentDetailsMessages extends BaseMessages {

  case class ExpectedContentChangeUnderpayment(title: String, heading: String, remove: String)

  val B00pageTitle  = "Change the import VAT underpayment details"
  val A00pageTitle  = "Change the Customs Duty underpayment details"
  val E00pageTitle  = "Change the excise duty underpayment details"
  val A20pageTitle  = "Change the Additional Duty underpayment details"
  val A30pageTitle  = "Change the Definitive Anti-Dumping Duty underpayment details"
  val A35pageTitle  = "Change the Provisional Anti-Dumping Duty underpayment details"
  val A40pageTitle  = "Change the Definitive Countervailing Duty underpayment details"
  val A45pageTitle  = "Change the Provisional Countervailing Duty underpayment details"
  val A10pageTitle  = "Change the Customs Duty on Agricultural Products underpayment details"
  val D10pageTitle  = "Change the Compensatory interest underpayment details"
  val B00removeLink = "Remove this import VAT underpayment"
  val A00removeLink = "Remove this Customs Duty underpayment"
  val E00removeLink = "Remove this excise duty underpayment"
  val A20removeLink = "Remove this Additional Duty underpayment"
  val A30removeLink = "Remove this Definitive Anti-Dumping Duty underpayment"
  val A35removeLink = "Remove this Provisional Anti-Dumping Duty underpayment"
  val A40removeLink = "Remove this Definitive Countervailing Duty underpayment"
  val A45removeLink = "Remove this Provisional Countervailing Duty underpayment"
  val A10removeLink = "Remove this Customs Duty on Agricultural Products underpayment"
  val D10removeLink = "Remove this Compensatory interest underpayment"

  val originalAmount = "Amount that was paid"
  val amendedAmount  = "Amount that should have been paid"

  val originalNonEmpty   = "Enter the amount that was paid, in pounds"
  val originalNonNumber  = "Amount that was paid must be a number like 7235 or 67.39"
  val originalOutOfRange = "Amount that was paid must be between £0 and £9,999,999,999.99"
  val amendedNonEmpty    = "Enter the amount that should have been paid, in pounds"
  val amendedNonNumber   = "Amount that should have been paid must be a number like 7235 or 67.39"
  val amendedOutOfRange  = "Amount that should have been paid must be between £0 and £9,999,999,999.99"
  val amendedDifferent   = "Amount that should have been paid must be more than amount that was paid"

  val underpaymentTypeContent: Map[String, ExpectedContentChangeUnderpayment] = Map(
    "B00" -> ExpectedContentChangeUnderpayment(
      B00pageTitle,
      B00pageTitle,
      B00removeLink
    ),
    "A00" -> ExpectedContentChangeUnderpayment(
      A00pageTitle,
      A00pageTitle,
      A00removeLink
    ),
    "E00" -> ExpectedContentChangeUnderpayment(
      E00pageTitle,
      E00pageTitle,
      E00removeLink
    ),
    "A20" -> ExpectedContentChangeUnderpayment(
      A20pageTitle,
      A20pageTitle,
      A20removeLink
    ),
    "A30" -> ExpectedContentChangeUnderpayment(
      A30pageTitle,
      A30pageTitle,
      A30removeLink
    ),
    "A35" -> ExpectedContentChangeUnderpayment(
      A35pageTitle,
      A35pageTitle,
      A35removeLink
    ),
    "A40" -> ExpectedContentChangeUnderpayment(
      A40pageTitle,
      A40pageTitle,
      A40removeLink
    ),
    "A45" -> ExpectedContentChangeUnderpayment(
      A45pageTitle,
      A45pageTitle,
      A45removeLink
    ),
    "A10" -> ExpectedContentChangeUnderpayment(
      A10pageTitle,
      A10pageTitle,
      A10removeLink
    ),
    "D10" -> ExpectedContentChangeUnderpayment(
      D10pageTitle,
      D10pageTitle,
      D10removeLink
    )
  )
}
