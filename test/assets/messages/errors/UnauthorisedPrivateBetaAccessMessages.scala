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

object UnauthorisedPrivateBetaAccessMessages extends BaseMessages {
  val title   = "Sorry, the service is unavailable"
  val para1   = "We are working on updating this service."
  val para2_1 = "In the meantime you can "
  val para2_2 = "tell us about an underpayment of customs duty or import VAT using the existing C2001 form."
}
