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

package messages.importDetails

import messages.BaseMessages

object UserTypeMessages extends BaseMessages {

  val title: String               = "Importer or representative?"
  val h1: String                  = "Are you the importer or their representative?"
  val radioButtonOne: String      = "I am the importer"
  val radioButtonTwo: String      = "I am a representative of the importer"
  val requiredError: String       = "Select if you are the importer or their representative"
  val beforeYouContinueh2: String = "Before you continue"
  val beforeYouContinuep1: String =
    "Once you tell HMRC if you are the importer or their representative (such as a shipping agent) and select continue, you cannot change your answer."
  val beforeYouContinuep2: String =
    "If you need to change your answer after you select continue, you will need to start your disclosure again."
  val beforeYouContinuep3: String = "HMRC will not save your information."

}
