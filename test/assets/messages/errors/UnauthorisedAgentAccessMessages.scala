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

package messages.errors

import messages.BaseMessages

object UnauthorisedAgentAccessMessages extends BaseMessages {

  val title = "You cannot access this service to disclose a Customs Duty or import VAT underpayment"
  val para1 = "You have signed in with an agent Government user ID that cannot access this service."
  val para2 =
    "If you are an importer or an importer’s representative you need to sign in again with the Government Gateway user ID you use for your business."
}
