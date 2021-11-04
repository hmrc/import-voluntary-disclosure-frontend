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

import messages.BaseMessages
import messages.reasons.ExpectedContent

object UnderpaymentDetailConfirmMessages extends BaseMessages {

  val originalAmount        = "Amount that was paid"
  val amendedAmount         = "Amount that should have been paid"
  val B00pageTitle          = "import VAT"
  val B00pageTitleUpperCase = "Import VAT"
  val A00pageTitle          = "Customs Duty"
  val E00pageTitle          = "excise duty"
  val E00pageTitleUpperCase = "Excise duty"
  val A20pageTitle          = "Additional Duty"
  val A30pageTitle          = "Definitive Anti-Dumping Duty"
  val A35pageTitle          = "Provisional Anti-Dumping Duty"
  val A40pageTitle          = "Definitive Countervailing Duty"
  val A45pageTitle          = "Provisional Countervailing Duty"
  val A10pageTitle          = "Customs Duty on Agricultural Products"
  val D10pageTitle          = "Compensatory Duty"
  val beginningMessage      = "Confirm the "
  val endingMessage         = " underpayment details"
  val dueToHMRC             = " owed to HMRC"

  val underpaymentTypeContent: Map[String, ExpectedContent] = Map(
    "B00" -> ExpectedContent(
      beginningMessage + B00pageTitle + endingMessage,
      beginningMessage + B00pageTitle + endingMessage,
      Some(B00pageTitleUpperCase + dueToHMRC)
    ),
    "A00" -> ExpectedContent(
      beginningMessage + A00pageTitle + endingMessage,
      beginningMessage + A00pageTitle + endingMessage,
      Some(A00pageTitle + dueToHMRC)
    ),
    "E00" -> ExpectedContent(
      beginningMessage + E00pageTitle + endingMessage,
      beginningMessage + E00pageTitle + endingMessage,
      Some(E00pageTitleUpperCase + dueToHMRC)
    ),
    "A20" -> ExpectedContent(
      beginningMessage + A20pageTitle + endingMessage,
      beginningMessage + A20pageTitle + endingMessage,
      Some(A20pageTitle + dueToHMRC)
    ),
    "A30" -> ExpectedContent(
      beginningMessage + A30pageTitle + endingMessage,
      beginningMessage + A30pageTitle + endingMessage,
      Some(A30pageTitle + dueToHMRC)
    ),
    "A35" -> ExpectedContent(
      beginningMessage + A35pageTitle + endingMessage,
      beginningMessage + A35pageTitle + endingMessage,
      Some(A35pageTitle + dueToHMRC)
    ),
    "A40" -> ExpectedContent(
      beginningMessage + A40pageTitle + endingMessage,
      beginningMessage + A40pageTitle + endingMessage,
      Some(A40pageTitle + dueToHMRC)
    ),
    "A45" -> ExpectedContent(
      beginningMessage + A45pageTitle + endingMessage,
      beginningMessage + A45pageTitle + endingMessage,
      Some(A45pageTitle + dueToHMRC)
    ),
    "A10" -> ExpectedContent(
      beginningMessage + A10pageTitle + endingMessage,
      beginningMessage + A10pageTitle + endingMessage,
      Some(A10pageTitle + dueToHMRC)
    ),
    "D10" -> ExpectedContent(
      beginningMessage + D10pageTitle + endingMessage,
      beginningMessage + D10pageTitle + endingMessage,
      Some(D10pageTitle + dueToHMRC)
    )
  )

}
