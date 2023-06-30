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

import models.reasons.{BoxNumber, UnderpaymentReason}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.SummaryListHelper

trait UnderpaymentReasonSummaryList extends SummaryListHelper {

  def buildSummaryList(
    underpaymentReason: Option[Seq[UnderpaymentReason]]
  )(implicit messages: Messages): Option[SummaryList] = {

    underpaymentReason.map { reasons =>
      val sortedReasons = reasons.sortBy(item => item.boxNumber)
      SummaryList(
        rows = for (underpayment <- sortedReasons) yield {
          val label = underpayment.boxNumber match {
            case BoxNumber.OtherItem => Text(messages("underpaymentReasonSummary.otherReason"))
            case _ => Text(s"${messages("underpaymentReasonSummary.box")} ${underpayment.boxNumber.id}")
          }
          val hiddenLabel = underpayment.boxNumber match {
            case BoxNumber.OtherItem => messages("underpaymentReasonSummary.otherReason.change")
            case BoxNumber.Box33 | BoxNumber.Box34 | BoxNumber.Box35 | BoxNumber.Box36 | BoxNumber.Box37 |
                BoxNumber.Box38 | BoxNumber.Box39 | BoxNumber.Box41 | BoxNumber.Box42 | BoxNumber.Box43 |
                BoxNumber.Box45 | BoxNumber.Box46 =>
              messages("underpaymentReasonSummary.itemLevel.change", underpayment.boxNumber.id, underpayment.itemNumber)
            case _ => messages("underpaymentReasonSummary.entryLevel.change", underpayment.boxNumber.id)
          }

          val valueContent = if (underpayment.boxNumber == BoxNumber.OtherItem) {
            Text(messages("underpaymentReasonSummary.entryOrItem"))
          } else if (underpayment.itemNumber == 0) {
            Text(messages("underpaymentReasonSummary.entryLevel"))
          } else {
            Text(s"${messages("underpaymentReasonSummary.item")} ${underpayment.itemNumber}")
          }

          createRow(
            label,
            valueContent,
            Some(
              createChangeActionItem(
                controllers.reasons.routes.ChangeUnderpaymentReasonController.change(
                  underpayment.boxNumber.id,
                  underpayment.itemNumber
                ).url,
                hiddenLabel
              )
            ),
            keyColumnClasses = "",
            actionColumnClasses = "govuk-!-width-one-third"
          )
        }
      )
    }

  }
}
