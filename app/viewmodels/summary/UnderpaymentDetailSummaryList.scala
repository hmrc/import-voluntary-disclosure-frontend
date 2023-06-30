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

package viewmodels.summary

import models.underpayments.UnderpaymentDetail
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.SummaryListHelper
import views.ViewUtils.displayMoney

trait UnderpaymentDetailSummaryList extends SummaryListHelper {

  def buildSummaryList(underpaymentDetail: Seq[UnderpaymentDetail])(implicit messages: Messages): SummaryList =
    SummaryList(
      rows = generateUnderpaymentsDetailsRows(underpaymentDetail) :+ buildAmountOwedSummaryListRow(underpaymentDetail)
    )

  private def generateUnderpaymentsDetailsRows(
    underpaymentDetails: Seq[UnderpaymentDetail]
  )(implicit messages: Messages): Seq[SummaryListRow] = {
    for (underpayment <- underpaymentDetails.reverse)
      yield {
        createRow(
          Text(messages(s"underpaymentDetailsSummary.${underpayment.duty}")),
          Text(displayMoney(underpayment.amended - underpayment.original)),
          Some(
            createChangeActionItem(
              controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpayment.duty).url,
              messages(s"underpaymentDetailsSummary.${underpayment.duty}.change")
            )
          ),
          "govuk-!-width-two-thirds govuk-!-font-weight-regular"
        )
      }
  }

  private def buildAmountOwedSummaryListRow(
    underpaymentDetail: Seq[UnderpaymentDetail]
  )(implicit messages: Messages): SummaryListRow = {
    val amountOwed = underpaymentDetail.map(underpayment => underpayment.amended - underpayment.original).sum
    createRow(
      Text(messages(s"underpaymentDetailsSummary.owedToHMRC")),
      Text(displayMoney(amountOwed)),
      keyColumnClasses = "govuk-!-width-one-half govuk-!-padding-top-7",
      rowClasses = "govuk-summary-list__row--no-border govuk-summary-list__row--no-actions govuk-!-font-weight-bold"
    )
  }
}
