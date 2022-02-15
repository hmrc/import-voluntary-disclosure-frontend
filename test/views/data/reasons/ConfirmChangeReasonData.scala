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

package views.data.reasons

import messages.reasons.ConfirmReasonDetailMessages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

object ConfirmChangeReasonData {

  val itemNumberExample: Int = 1

  def itemNumber(item: Option[Int]): Option[Seq[SummaryListRow]] =
    item.map { itemNo =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(ConfirmReasonDetailMessages.itemNumber),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(itemNo.toString),
            classes = "govuk-!-width-one-half"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  controllers.reasons.routes.ChangeItemNumberController.onLoad().url,
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
          content = Text(ConfirmReasonDetailMessages.originalValue),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(
          content = HtmlContent(originalValue),
          classes = "govuk-!-width-one-half"
        ),
        actions = Some(
          Actions(
            items = Seq(
              ActionItem(
                controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(boxNumber).url,
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
          content = Text(ConfirmReasonDetailMessages.amendedValue),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(
          content = HtmlContent(amendedValue),
          classes = "govuk-!-width-one-half"
        ),
        actions = Some(
          Actions(
            items = Seq(
              ActionItem(
                controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(boxNumber).url,
                HtmlContent("""<span aria-hidden="true">Change</span>"""),
                Some(ConfirmReasonDetailMessages.amendedAmountChange)
              )
            )
          )
        )
      )
    )

  def reasons(box: Int, item: Option[Int] = None, originalValue: String, amendedValue: String): SummaryList =
    SummaryList(
      itemNumber(item).getOrElse(Seq.empty) ++ originalAmount(originalValue, box) ++ amendedAmount(amendedValue, box)
    )

  def otherItemReasons(value: String): SummaryList =
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(ConfirmReasonDetailMessages.otherReason)
          ),
          value = Value(
            content = HtmlContent(value),
            classes = "govuk-!-width-one-half"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(99).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some(ConfirmReasonDetailMessages.otherReasonChange)
                )
              )
            )
          )
        )
      )
    )
}
