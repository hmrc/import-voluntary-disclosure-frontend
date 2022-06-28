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

import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, ChangeUnderpaymentReason}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.SummaryListHelper

trait ConfirmChangeReasonDetailSummaryList extends SummaryListHelper {

  def buildSummaryList(reasonOpt: Option[ChangeUnderpaymentReason], boxNumber: BoxNumber)(implicit
    messages: Messages
  ): SummaryList = {

    val rows = reasonOpt match {
      case Some(reason) if boxNumber == BoxNumber.OtherItem =>
        buildOtherReasonSummaryList(reason.changed.original)
      case Some(reason) if reason.changed.itemNumber != 0 =>
        buildItemNumberSummaryListRow(reason) ++ buildOriginalAmountSummaryListRow(reason, boxNumber.id)
      case Some(reason) =>
        Seq.empty ++ buildOriginalAmountSummaryListRow(reason, boxNumber.id)
      case None => Seq.empty
    }

    SummaryList(rows)
  }

  private def buildItemNumberSummaryListRow(
    reason: ChangeUnderpaymentReason
  )(implicit messages: Messages): Seq[SummaryListRow] = {
    Seq(
      createRow(
        Text(messages("confirmReason.itemNumber")),
        HtmlContent(reason.changed.itemNumber.toString),
        Some(
          createChangeActionItem(
            controllers.reasons.routes.ChangeItemNumberController.onLoad().url,
            messages("confirmReason.item.change")
          )
        ),
        "govuk-!-width-two-thirds"
      )
    )
  }

  def buildOriginalAmountSummaryListRow(reason: ChangeUnderpaymentReason, boxNumber: Int)(implicit
    messages: Messages
  ): Seq[SummaryListRow] = {
    Seq(
      createRow(
        Text(messages("confirmReason.original")),
        HtmlContent(reason.changed.original),
        Some(
          createChangeActionItem(
            controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(boxNumber).url,
            messages("confirmReason.values.original.change")
          )
        ),
        "govuk-!-width-two-thirds"
      ),
      createRow(
        Text(messages("confirmReason.amended")),
        HtmlContent(reason.changed.amended),
        Some(
          createChangeActionItem(
            controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(boxNumber).url,
            messages("confirmReason.values.amended.change")
          )
        ),
        "govuk-!-width-two-thirds"
      )
    )
  }

  private def buildOtherReasonSummaryList(value: String)(implicit messages: Messages): Seq[SummaryListRow] =
    Seq(
      createRow(
        Text(messages("confirmReason.otherReason")),
        HtmlContent(value),
        Some(
          createChangeActionItem(
            controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(BoxNumber.OtherItem.id).url,
            messages("confirmReason.values.otherReason.change")
          )
        ),
        ""
      )
    )

}
