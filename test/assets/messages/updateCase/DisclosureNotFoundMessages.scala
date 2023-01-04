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

package messages.updateCase

import messages.BaseMessages

object DisclosureNotFoundMessages extends BaseMessages {

  val pageTitle: String = "No disclosure found"

  def p1(caseId: String): String = s"We cannot find a disclosure with the reference number $caseId."

  val p2: String  = "You can:"
  val li1: String = "check and enter the disclosure reference number again"
  val li2: String =
    "email us at npcc@hmrc.gov.uk including the entry details (or where there are multiple entry details, the total amount of tax or duty that is owed), the EORI number and name of the importer, and the information you want to add to the disclosure"
}
