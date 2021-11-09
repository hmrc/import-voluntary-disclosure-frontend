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

package messages.importDetails

import messages.BaseMessages

object AcceptanceDateBulkMessages extends BaseMessages {

  val title: String = "When were the entry acceptance dates for all the entries?"
  val info: String =
    "You must not include entries where some have an entry acceptance date before and some after 1 January 2021. If the entries cross this date you will need to separate them and make two disclosures."
  val requiredError: String = "Select when the entry acceptance dates were for all the entries"
  val beforeRadio: String   = "On or before 31 December 2020"
  val afterRadio: String    = "From 1 January 2021"

}
