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

package messages.paymentInfo

import messages.BaseMessages

object RepresentativeDanImportVATMessages extends BaseMessages {

  val title: String = "Deferment account details for the import VAT owed"

  val accountNumberLabel: String = "Deferment account number"
  val radioButtonLabel: String   = "Whose deferment account is this?"

  val radio1: String     = "My deferment account (A)"
  val radio2: String     = "importer’s account and I have authority to use it (B)"
  val radio2Hint: String = "You will be asked to upload proof of authority"
  val radio3: String     = "importer’s account and I have standing authority to use it (C)"

  val accountNumberRequiredError: String = "Enter the deferment account number"
  val danTypeRequiredError: String       = "Select whose deferment account this is"
  val accountNumberFormatError: String   = "Enter the deferment account number in the correct format, like 1234567"

}
