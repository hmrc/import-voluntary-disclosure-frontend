/*
 * Copyright 2022 HM Revenue & Customs
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

package viewmodels.summary

import models.reasons.{BoxNumber, UnderpaymentReason}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.SummaryListHelper

trait ChangeUnderpaymentReasonSummaryList extends SummaryListHelper {

  def buildSummaryList(underpaymentReason: UnderpaymentReason)(implicit messages: Messages): SummaryList = {

    if (underpaymentReason.boxNumber == BoxNumber.OtherItem) {
      SummaryList(Seq(buildOtherReasonSummary(underpaymentReason.original)))
    } else {
      SummaryList(
        buildItemNumberSummaryListRow(underpaymentReason) ++ buildOriginalAmountSummaryListRow(underpaymentReason)
      )
    }
  }

  private def buildItemNumberSummaryListRow(
    underpaymentReason: UnderpaymentReason
  )(implicit messages: Messages): Seq[SummaryListRow] = {
    if (underpaymentReason.itemNumber != 0) {
      Seq(
        createRow(
          Text(messages("changeUnderpaymentReason.itemNumber")),
          HtmlContent(underpaymentReason.itemNumber.toString),
          Some(
            createChangeActionItem(
              controllers.reasons.routes.ChangeItemNumberController.onLoad().url,
              messages("changeUnderpaymentReason.item.change")
            )
          ),
          "",
          ""
        )
      )
    } else {
      Seq.empty
    }
  }

  private def buildOriginalAmountSummaryListRow(
    underpaymentReason: UnderpaymentReason
  )(implicit messages: Messages): Seq[SummaryListRow] = {
    Seq(
      createRow(
        Text(messages("changeUnderpaymentReason.original")),
        HtmlContent(underpaymentReason.original),
        Some(
          createChangeActionItem(
            controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(
              underpaymentReason.boxNumber.id
            ).url,
            messages("changeUnderpaymentReason.values.original.change")
          )
        ),
        "",
        ""
      ),
      createRow(
        Text(messages("changeUnderpaymentReason.amended")),
        HtmlContent(underpaymentReason.amended),
        Some(
          createChangeActionItem(
            controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(
              underpaymentReason.boxNumber.id
            ).url,
            messages("changeUnderpaymentReason.values.amended.change")
          )
        ),
        "govuk-!-width-two-thirds",
        ""
      )
    )
  }

  private def buildOtherReasonSummary(value: String)(implicit messages: Messages): SummaryListRow = {
    createRow(
      Text(messages("changeUnderpaymentReason.otherReason")),
      HtmlContent(value),
      Some(
        createChangeActionItem(
          controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(99).url,
          messages("changeUnderpaymentReason.values.otherReason.change")
        )
      ),
      ""
    )
  }
}
