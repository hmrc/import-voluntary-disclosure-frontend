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

package messages.underpayments

import messages.{BaseMessages, ExpectedContent}

object UnderpaymentDetailsMessages extends BaseMessages {

  val originalAmount = "Amount that was paid"
  val amendedAmount  = "Amount that should have been paid"
  val bulkMessage    = "This must be the total across all the entries included in this disclosure."

  val B00pageTitle = "Import VAT underpayment details, in pounds"
  val A00pageTitle = "Customs Duty underpayment details, in pounds"
  val E00pageTitle = "Excise duty underpayment details, in pounds"
  val A20pageTitle = "Additional Duty underpayment details, in pounds"
  val A30pageTitle = "Definitive Anti-Dumping Duty underpayment details, in pounds"
  val A35pageTitle = "Provisional Anti-Dumping Duty underpayment details, in pounds"
  val A40pageTitle = "Definitive Countervailing Duty underpayment details, in pounds"
  val A45pageTitle = "Provisional Countervailing Duty underpayment details, in pounds"
  val A10pageTitle = "Customs Duty on Agricultural Products underpayment details, in pounds"
  val D10pageTitle = "Compensatory Duty underpayment details, in pounds"

  val originalNonEmpty   = "Enter the amount that was paid, in pounds"
  val originalNonNumber  = "Amount that was paid must be a number like 7235 or 67.39"
  val originalOutOfRange = "Amount that was paid must be between £0 and £9,999,999,999.99"
  val amendedNonEmpty    = "Enter the amount that should have been paid, in pounds"
  val amendedNonNumber   = "Amount that should have been paid must be a number like 7235 or 67.39"
  val amendedOutOfRange  = "Amount that should have been paid must be between £0 and £9,999,999,999.99"
  val amendedDifferent   = "Amount that should have been paid must be more than amount that was paid"

  val underpaymentTypeContent: Map[String, ExpectedContent] = Map(
    "B00" -> ExpectedContent(
      B00pageTitle,
      B00pageTitle,
      None
    ),
    "A00" -> ExpectedContent(
      A00pageTitle,
      A00pageTitle,
      None
    ),
    "E00" -> ExpectedContent(
      E00pageTitle,
      E00pageTitle,
      None
    ),
    "A20" -> ExpectedContent(
      A20pageTitle,
      A20pageTitle,
      None
    ),
    "A30" -> ExpectedContent(
      A30pageTitle,
      A30pageTitle,
      None
    ),
    "A35" -> ExpectedContent(
      A35pageTitle,
      A35pageTitle,
      None
    ),
    "A40" -> ExpectedContent(
      A40pageTitle,
      A40pageTitle,
      None
    ),
    "A45" -> ExpectedContent(
      A45pageTitle,
      A45pageTitle,
      None
    ),
    "A10" -> ExpectedContent(
      A10pageTitle,
      A10pageTitle,
      None
    ),
    "D10" -> ExpectedContent(
      D10pageTitle,
      D10pageTitle,
      None
    )
  )

}
