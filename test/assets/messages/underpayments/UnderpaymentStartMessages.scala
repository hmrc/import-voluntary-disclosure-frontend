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

package messages.underpayments

import messages.BaseMessages

object UnderpaymentStartMessages extends BaseMessages {

  val pageTitle: String       = "Tell us what was underpaid"
  val p1: String              = "For each type of tax or duty, you will need to provide:"
  val oneEntryBullet1: String = "the amount that was originally paid"
  val oneEntryBullet2: String = "the amount that should have been paid"
  val bulkBullet1: String     = "the total amount that was originally paid across all the entries"
  val bulkBullet2: String     = "the total amount that should have been paid across all the entries"
  val representativeParagraph =
    "Do not use this service to tell us about underpaid import VAT if ABC ltd uses postponed VAT accounting. Instead, they must account for the underpaid import VAT on their next VAT return."
  val importerDetailsHeader =
    "If ABC ltd uses postponed VAT accounting (PVA)"
  val importerDetailsParagraph =
    "Do not use this service to tell us about underpaid import VAT if ABC ltd uses postponed VAT accounting. You must account for the underpaid import VAT on your next VAT return."
  val representativeImporterDetailsHeader =
    "If Fast Food ltd uses postponed VAT accounting (PVA)"
  val representativeImporterDetailsParagraph =
    "Do not use this service to tell us about underpaid import VAT if Fast Food ltd uses postponed VAT accounting. They must account for the underpaid import VAT on their next VAT return."
}
