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

package messages.cancelCase

import messages.BaseMessages

object CancelCaseConfirmationMessages extends BaseMessages {

  val pageTitle = "Confirmation"
  val heading   = "Cancellation requested"

  def panelText(caseId: String): String =
    s"for disclosure reference number $caseId"

  val whatHappensNext          = "What happens next"
  val whatHappensNextParagraph =
    "We will check the information you have provided. If we accept the reason for cancellation, we will cancel the underpayment disclosure. Otherwise, we will continue to issue the C18 Post Clearance Demand Note."
  val anotherDisclosure        = "Start a new, add to, or cancel a disclosure"
  val helpImproveServiceLink   = "What did you think of this service?"
}
