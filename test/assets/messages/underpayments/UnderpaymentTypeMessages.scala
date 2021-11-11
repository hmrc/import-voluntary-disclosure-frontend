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

package messages.underpayments

import messages.BaseMessages

object UnderpaymentTypeMessages extends BaseMessages {

  val firstTimePageTitle = "Which tax or duty do you want to tell us about first?"
  val hint = "If more than one type of tax or duty was underpaid, you can tell us about them later."
  val secondTimePageTitle           = "Which tax or duty was underpaid?"
  val errorRequired                 = "Select the type of tax or duty that was underpaid"
  val importVAT                     = "Import VAT (B00)"
  val customsDuty                   = "Customs Duty (A00)"
  val exciseDuty                    = "Excise duty"
  val additionalDuty                = "Additional Duty (A20)"
  val definitiveAntiDumpingDuty     = "Definitive Anti-Dumping Duty (A30)"
  val provisionalAntiDumpingDuty    = "Provisional Anti-Dumping Duty (A35)"
  val definitiveCountervailingDuty  = "Definitive Countervailing Duty (A40)"
  val provisionalCountervailingDuty = "Provisional Countervailing Duty (A45)"
  val agriculturalDuty              = "Customs Duty on Agricultural products (A10)"
  val compensatoryDuty              = "Compensatory Duty (D10)"

}
