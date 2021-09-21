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

object MoreInformationMessages extends BaseMessages {

  val singleEntryTitle: String          = "Tell us the extra information about the underpayment"
  val singleEntryRequiredError: String  = "Enter the extra information about the underpayment"
  val singleEntryMaxLengthError: String = "The extra information must be 1500 characters or fewer"

  val bulkEntryTitle: String = "What were the reasons for the underpayment of tax or duty?"
  val bulkP1 = "Tell us why the duty or tax was underpaid for all the entries included in this disclosure."
  val bulkP2 = "You do not need to repeat anything that you have already provided in the file you have uploaded."
  val bulkEntryRequiredError: String  = "Enter the reasons for the underpayment of tax or duty"
  val bulkEntryMaxLengthError: String = "The reasons for the underpayment must be 1500 characters or fewer"

}
