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

package messages.paymentInfo

import messages.BaseMessages

object ImporterDanMessages extends BaseMessages {

  val title: String = "What is importer’s deferment account number?"
  val hint: String =
    "This is the number HMRC sent to the account holder when the duty deferment account was set up. It’s 7 digits, like 1234567."
  val requiredError: String = "Enter the deferment account number"
  val formatError: String   = "Enter the deferment account number in the correct format, like 1234567"

}
