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

object NumberOfEntriesMessages extends BaseMessages {

  val title: String               = "How many entries are you disclosing an underpayment for?"
  val radioButtonOne: String      = "One entry"
  val radioButtonTwo: String      = "More than one entry"
  val hint: String                = "All the entries must be for importer."
  val requiredError: String       = "Select if you are disclosing an underpayment for one entry or more than one entry"
  val beforeYouContinueh2: String = "Before you continue"
  val beforeYouContinuep1: String =
    "You cannot change the number of entries you are disclosing after you select continue."
  val beforeYouContinuep2: String =
    "If you need to change the number of entries you are disclosing, you will need to start your disclosure again."
  val beforeYouContinuep3: String = "HMRC will not save your information."

}
