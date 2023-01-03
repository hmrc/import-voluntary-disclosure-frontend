/*
 * Copyright 2023 HM Revenue & Customs
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

package messages.cancelCase

import messages.BaseMessages

object CancelCaseDisclosureClosedMessages extends BaseMessages {

  val pageTitle = "This disclosure cannot be cancelled"
  val p1 =
    "This is because we may have already issued the C18 Post Clearance Demand Note."
  val p2 =
    "Instead, you need to email npcc@hmrc.gov.uk, and include:"

  def li1(caseId: String) = s"the disclosure reference number $caseId"
  val li2 =
    "the entry details (or where there are multiple entry details, the total amount of tax or duty that is owed)"
  val li3 = "the EORI number and name of the importer"
  val li4 = "the reason for requesting the cancellation of this disclosure"
}
