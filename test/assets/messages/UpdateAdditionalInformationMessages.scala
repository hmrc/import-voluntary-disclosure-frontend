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

object UpdateAdditionalInformationMessages extends BaseMessages {

  val title: String          = "Tell us any additional information"
  val p1: String             = "You may have been asked to provide more information to help us progress the disclosure."
  val requiredError: String  = "Enter the additional information about the underpayment"
  val maxLengthError: String = "More information must be 1500 characters or fewer"
  val emojiError: String     = "The additional information must not include emojis"

}
