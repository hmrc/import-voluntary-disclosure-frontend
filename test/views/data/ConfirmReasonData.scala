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

import messages.ConfirmReasonDetailMessages
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryList, SummaryListRow, Value}

object ConfirmReasonData {

  val itemNumberExample: Int = 1

  def boxNumber(box: Int): Seq[SummaryListRow] =
      Seq(SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.boxNumber),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(
          content = HtmlContent(box.toString)
        ),
        actions = Some(Actions(
          items = Seq(
            ActionItem(
              controllers.routes.BoxNumberController.onLoad().url,
              Text(ConfirmReasonDetailMessages.change),
              Some(ConfirmReasonDetailMessages.boxChange)
            )
          )
        ))
      )
  )

  def itemNumber(item: Option[Int]): Option[Seq[SummaryListRow]] =
    item.map { itemNo =>
      Seq(SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.itemNumber),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(
          content = HtmlContent(itemNo.toString)
        ),
        actions = Some(Actions(
          items = Seq(
            ActionItem(
              controllers.routes.ItemNumberController.onLoad().url,
              Text(ConfirmReasonDetailMessages.change),
              Some(ConfirmReasonDetailMessages.itemChange)
            )
          )
        ))
      ))
    }

  def originalAmount(originalValue: String, boxNumber: Int): Seq[SummaryListRow] =
    Seq(SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.originalValue),
          classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
        ),
        value = Value(
          content = HtmlContent(originalValue),
          classes = "govuk-!-padding-bottom-0"
        ),
        actions = Some(Actions(
          items = Seq(
            ActionItem(
              controllers.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber).url,
              Text(ConfirmReasonDetailMessages.change),
              Some(ConfirmReasonDetailMessages.itemValues)
            )
          ),
          classes = "govuk-!-padding-bottom-0")
        ),
        classes = "govuk-summary-list__row--no-border"
      )
    )

  def amendedAmount(amendedValue: String): Seq[SummaryListRow] =
    Seq(SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.amendedValue),
          classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
        ),
        value = Value(
          content = HtmlContent(amendedValue),
          classes = "govuk-!-padding-top-0"
        )
        )
      )

  def reasons(box: Int, item: Option[Int] = None, originalValue: String, amendedValue: String): Seq[SummaryList] = Seq(
    SummaryList(
      boxNumber(box) ++ itemNumber(item).getOrElse(Seq.empty) ++ originalAmount(originalValue, box) ++ amendedAmount(amendedValue)
    )
  )
}
