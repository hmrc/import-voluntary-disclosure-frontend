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

package messages.cancelCase

import messages.BaseMessages

object CancelCaseCYAMessages extends BaseMessages {

  val title: String           = "Check your answers before sending your cancellation request"
  val sendInformation: String = "Now send your cancellation request"
  val disclosureConfirmation: String =
    "By submitting this request you are confirming that, to the best of your knowledge, the details you are providing are correct."
  val accept: String                        = "Accept and send"
  val referenceNumber: String               = "Reference number"
  val reasonCancellation: String            = "Reason for cancellation"
  val supportingDocumentation: String       = "Supporting documentation?"
  val changeReferenceNumber: String         = "Change reference number"
  val changeReasonCancellation: String      = "Change reason for cancellation"
  val changeSupportingDocumentation: String = "Change supporting documentation?"
  val changeUploadedFiles: String           = "Change uploaded files"

  def filesUploaded(numberOfFiles: Int): String =
    if (numberOfFiles == 1) s"$numberOfFiles file uploaded" else s"$numberOfFiles files uploaded"

}
