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

object UpdateCaseConfirmationMessages extends BaseMessages {

  val pageTitle = "Confirmation"
  val heading = "Information added"

  def paragraph(caseId: String): String =
    s"We have received the additional information for disclosure reference number: $caseId."

  val whatHappensNext = "What happens next"
  val whatHappensNextParagraph = "We will check the information you have provided and send the C18 Post Clearance Demand Note in the post."
  val whatYouShouldDoNext = "What you should do next"
  val whatYouShouldDoNextParagraph = "If you have not received the demand note or we have not contacted you within 14 days then email customsaccountingrepayments@hmrc.gov.uk."
  val helpImproveServiceLink = "What did you think of this service?"

}
