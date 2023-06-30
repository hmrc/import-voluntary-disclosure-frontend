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

package views.data.reasons

import messages.reasons.ConfirmReasonDetailMessages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

object ConfirmReasonData {

  val itemNumberExample: Int = 1

  def boxNumber(box: Int): Seq[SummaryListRow] =
    Seq(
      SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.boxNumber),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(
          content = Text(box.toString),
          classes = "govuk-!-width-one-half"
        ),
        actions = Some(
          Actions(
            items = Seq(
              ActionItem(
                controllers.reasons.routes.BoxNumberController.onLoad().url,
                HtmlContent("""<span aria-hidden="true">Change</span>"""),
                Some(ConfirmReasonDetailMessages.boxChange)
              )
            )
          )
        )
      )
    )

  def itemNumber(item: Option[Int]): Option[Seq[SummaryListRow]] =
    item.map { itemNo =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(ConfirmReasonDetailMessages.itemNumber)
          ),
          value = Value(
            content = Text(itemNo.toString),
            classes = "govuk-!-width-two-thirds"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  controllers.reasons.routes.ItemNumberController.onLoad().url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some(ConfirmReasonDetailMessages.itemChange)
                )
              )
            )
          )
        )
      )
    }

  def originalAmount(originalValue: String, boxNumber: Int): Seq[SummaryListRow] =
    Seq(
      SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.originalValue)
        ),
        value = Value(
          content = Text(originalValue),
          classes = "govuk-!-width-two-thirds"
        ),
        actions = Some(
          Actions(
            items = Seq(
              ActionItem(
                controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber).url,
                HtmlContent("""<span aria-hidden="true">Change</span>"""),
                Some(ConfirmReasonDetailMessages.originalAmountChange)
              )
            )
          )
        )
      )
    )

  def amendedAmount(amendedValue: String, boxNumber: Int): Seq[SummaryListRow] =
    Seq(
      SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.amendedValue)
        ),
        value = Value(
          content = Text(amendedValue),
          classes = "govuk-!-width-two-thirds"
        ),
        actions = Some(
          Actions(
            items = Seq(
              ActionItem(
                controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber).url,
                HtmlContent("""<span aria-hidden="true">Change</span>"""),
                Some(ConfirmReasonDetailMessages.amendedAmountChange)
              )
            )
          )
        )
      )
    )

  def reasons(box: Int, item: Option[Int] = None, originalValue: String, amendedValue: String): Seq[SummaryList] = Seq(
    SummaryList(
      boxNumber(box) ++ itemNumber(item).getOrElse(Seq.empty) ++ originalAmount(originalValue, box) ++ amendedAmount(
        amendedValue,
        box
      )
    )
  )
}
