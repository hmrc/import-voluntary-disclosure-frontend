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

package messages.serviceEntry

import messages.BaseMessages

object WhatDoYouWantToDoMessages extends BaseMessages {

  val title: String         = "What do you want to do?"
  val createCaseMsg: String = "Start a new underpayment disclosure"
  val updateCaseMsg: String = "Add more information to an existing underpayment disclosure"
  val cancelCaseMsg: String = "Cancel an existing underpayment disclosure"
  val error: String         = "Select what you want to do"

}
