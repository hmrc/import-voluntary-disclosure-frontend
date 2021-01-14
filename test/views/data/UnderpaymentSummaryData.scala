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

package views.data

import messages.UnderpaymentSummaryMessages
import models.UnderpaymentAmount
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryList, SummaryListRow, Value}
import views.ViewUtils.displayMoney

object UnderpaymentSummaryData {
  val cdUnderpayment = UnderpaymentAmount(123, 5123)
  val ivUnderpayment = UnderpaymentAmount(100, 1000)
  val edUnderpayment = UnderpaymentAmount(550, 690)


  val customsDuty = Some(SummaryList(
    rows = Seq(
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.originalAmount)),
        value = Value(HtmlContent(displayMoney(cdUnderpayment.original))),
        actions = Some(Actions(items = Seq(
          ActionItem(controllers.routes.UnderpaymentSummaryController.onLoad().url, HtmlContent(UnderpaymentSummaryMessages.change))
        )))),
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.amendedAmount)),
        value = Value(HtmlContent(displayMoney(cdUnderpayment.amended)))),
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.customsDutyTitle + UnderpaymentSummaryMessages.dueToHmrc)),
        value = Value(HtmlContent(displayMoney(cdUnderpayment.amended - cdUnderpayment.original))))
    )
  ))

  val importVat = Some(SummaryList(
    rows = Seq(
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.originalAmount)),
        value = Value(HtmlContent(displayMoney(ivUnderpayment.original))),
        actions = Some(Actions(items = Seq(
          ActionItem(controllers.routes.UnderpaymentSummaryController.onLoad().url, HtmlContent(UnderpaymentSummaryMessages.change))
        )))),
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.amendedAmount)),
        value = Value(HtmlContent(displayMoney(ivUnderpayment.amended)))),
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.importVatTitle + UnderpaymentSummaryMessages.dueToHmrc)),
        value = Value(HtmlContent(displayMoney(ivUnderpayment.amended - ivUnderpayment.original))))
    )
  ))

  val exciseDuty = Some(SummaryList(
    rows = Seq(
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.originalAmount)),
        value = Value(HtmlContent(displayMoney(edUnderpayment.original))),
        actions = Some(Actions(items = Seq(
          ActionItem(controllers.routes.UnderpaymentSummaryController.onLoad().url, HtmlContent(UnderpaymentSummaryMessages.change))
        )))),
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.amendedAmount)),
        value = Value(HtmlContent(displayMoney(edUnderpayment.amended)))),
      SummaryListRow(
        key = Key(HtmlContent(UnderpaymentSummaryMessages.exciseDutyTitle + UnderpaymentSummaryMessages.dueToHmrc)),
        value = Value(HtmlContent(displayMoney(edUnderpayment.amended - edUnderpayment.original))))
    )
  ))

}
