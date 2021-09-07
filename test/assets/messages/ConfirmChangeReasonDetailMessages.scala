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

package messages

object ConfirmChangeReasonDetailMessages extends BaseMessages {

  def title(boxNumber: Int)   = s"Confirm the changes to box $boxNumber reason for underpayment"
  def heading(boxNumber: Int) = s"Confirm the changes to box $boxNumber reason for underpayment"

  val otherReasonTitle   = "Confirm the changes to the reason for underpayment"
  val otherReasonHeading = "Confirm the changes to the reason for underpayment"

  val itemNumber    = "Item number"
  val originalValue = "Original value"
  val amendedValue  = "Amended value"
  val change        = "Change"

}
