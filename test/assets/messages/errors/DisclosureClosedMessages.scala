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

object DisclosureClosedMessages extends BaseMessages {

  val pageTitle = "The information cannot be added to the disclosure"
  val heading   = "The information cannot be added to the disclosure"
  val p1        =
    "We cannot add any information to this underpayment disclosure as it is closed and the demand for payment note has already been issued."
  val p2        =
    "You can email us at customsaccountingrepayments@hmrc.gov.uk , including the additional information and the disclosure reference number C182107152124AQYVM6E34."
}
