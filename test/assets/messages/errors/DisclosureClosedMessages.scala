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

  val pageTitle = "You cannot add information to this disclosure"
  val heading = "You cannot add information to this disclosure"
  val p1 = "This is because we may have already issued the C18 Post Clearance Demand Note."
  val p2 = "Instead, you need to email customsaccountingrepayments@hmrc.gov.uk, and include:"
  val bullet1 = "the disclosure reference number C182107152124AQYVM6E34"
  val bullet2 = "the entry details (or where there are multiple entry details, the total amount of tax or duty that is owed)"
  val bullet3 = "the EORI number and name of the importer"
  val bullet4 = "the information you wanted to add to this disclosure"
}
