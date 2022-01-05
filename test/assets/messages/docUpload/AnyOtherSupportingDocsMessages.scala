/*
 * Copyright 2022 HM Revenue & Customs
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

package messages.docUpload

import messages.BaseMessages

object AnyOtherSupportingDocsMessages extends BaseMessages {

  val pageTitle: String = "Do you have any of these documents?"
  val p1: String        = "You must upload the following documents if you have them:"
  val bullet1: String =
    "the amendment import declaration (C88) and entry acceptance (E2), sometimes known as a training entry"
  val bullet2: String = "the air waybill, if the goods were transported by air"
  val bullet3: String = "a proof of origin for the goods"
  val bullet4: String = "other documents relevant to this underpayment disclosure"
  val yes: String     = "Yes"
  val no: String      = "No"

}
