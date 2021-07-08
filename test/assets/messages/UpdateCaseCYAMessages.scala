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

object UpdateCaseCYAMessages extends BaseMessages {

  val title: String = "Check your information before adding it to the disclosure"
  val heading: String = "Check your information before adding it to the disclosure"
  val sendInformation: String = "Now add your information"
  val disclosureConfirmation: String = "By adding this information you are confirming that, to the best of your knowledge, the details you are providing are correct."
  val addToTheDisclosure: String = "Add to the disclosure"
  val referenceNumber: String = "Reference number"
  val moreDocumentation: String = "Add more documentation?"
  val fileUpload: String = "Add more documentation?"
  val additionalInformation: String = "Additional information"
  val changeReferenceNumber: String = "Change reference number"
  val changeAdditionalInformation: String = "Change additional information"

  def filesUploaded(numberOfFiles: Int): String = {
    if (numberOfFiles == 1) s"$numberOfFiles file uploaded" else s"$numberOfFiles files uploaded"
  }

}
