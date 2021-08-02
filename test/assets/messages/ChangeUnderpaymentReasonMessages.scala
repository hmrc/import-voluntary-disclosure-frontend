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

object ChangeUnderpaymentReasonMessages extends BaseMessages {

  def title(box: Int): String = s"Change box ${box} of the reason for underpayment"

  val h1: String = "Underpayment amount summary"
  val itemNumber = "Item number"
  val itemNumberChange = "Change item number"
  val change = "Change"
  val originalValue = "Original value"
  val originalValueChange = "Change original value"
  val amendedValueChange = "Change amended value"
  val amendedValue = "Amended value"
  val removeLink = "Remove this reason for underpayment"
  val backToReasons = "Back to reasons list"

}
