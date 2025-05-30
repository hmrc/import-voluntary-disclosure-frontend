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

package viewmodels.cya

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import viewmodels.SummaryListHelper

trait CYADisclosureSummaryListHelper extends SummaryListHelper {
  private val dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def buildDisclosureSummaryList(caseId: String, date: LocalDateTime)(implicit
    messages: Messages
  ): CYASummaryList = {
    val rows = Seq(
      buildReferenceNumberSummaryListRow(caseId),
      buildDateSubmittedSummaryListRow(date)
    )

    CYASummaryList(
      heading = None,
      summaryList = SummaryList(classes = "govuk-!-margin-bottom-9", rows = rows)
    )
  }

  private def buildReferenceNumberSummaryListRow(
    caseId: String
  )(implicit messages: Messages): SummaryListRow =
    createRow(
      keyText = Text(messages("printConfirmation.referenceNumber")),
      valueContent = Text(caseId)
    )

  private def buildDateSubmittedSummaryListRow(
    date: LocalDateTime
  )(implicit messages: Messages): SummaryListRow =
    createRow(
      keyText = Text(messages("printConfirmation.dateSubmitted")),
      valueContent = HtmlContent(dateFormat.format(date))
    )
}
