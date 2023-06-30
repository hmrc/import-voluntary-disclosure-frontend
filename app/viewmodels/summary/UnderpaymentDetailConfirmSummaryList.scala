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

import models.underpayments.UnderpaymentAmount
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.SummaryListHelper
import views.ViewUtils.displayMoney

trait UnderpaymentDetailConfirmSummaryList extends SummaryListHelper {

  def buildSummaryList(underpaymentType: String, underpaymentAmount: UnderpaymentAmount)(implicit
    messages: Messages
  ): SummaryList = {
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        createRow(
          Text(messages("underpaymentDetailsConfirm.originalAmount")),
          Text(displayMoney(underpaymentAmount.original)),
          Some(
            createChangeActionItem(
              controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType).url,
              messages(s"underpaymentDetailsConfirm.$underpaymentType.original.change")
            )
          ),
          "govuk-!-width-two-thirds"
        ),
        createRow(
          Text(messages("underpaymentDetailsConfirm.amendedAmount")),
          Text(displayMoney(underpaymentAmount.amended)),
          Some(
            createChangeActionItem(
              controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType).url,
              messages(s"underpaymentDetailsConfirm.$underpaymentType.amended.change")
            )
          ),
          "govuk-!-width-two-thirds"
        ),
        createRow(
          Text(messages(s"underpaymentDetailsConfirm.$underpaymentType.dueToHmrc")),
          Text(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
          keyColumnClasses = "govuk-!-width-two-thirds",
          valueColumnClasses = "",
          rowClasses = "govuk-summary-list__row--no-border"
        )
      )
    )
  }
}
