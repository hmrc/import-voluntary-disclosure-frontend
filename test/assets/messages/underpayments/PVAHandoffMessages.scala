/*
 * Copyright 2023 HM Revenue & Customs
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

object PVAHandoffMessages extends BaseMessages {

  val title = "You cannot disclose underpaid import VAT using this service"
  val repMessage =
    "As fewfew uses postponed VAT accounting they will need to account for the underpaid import VAT on their next VAT return."
  val importerMessage =
    "As you use postponed VAT accounting then you need to account for the underpaid import VAT on your next VAT return."
  val findOut =
    "Find out how to account for import VAT on your VAT Return if you’re using postponed VAT accounting (opens in new tab)."

}
