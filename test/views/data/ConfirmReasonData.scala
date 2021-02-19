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
  val boxNumberExample: Int = 33
  val itemNumberExample: Int = 1

  val boxNumber: Seq[SummaryListRow] =
      Seq(SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.boxNumber),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(
          content = HtmlContent(boxNumberExample.toString)
        ),
        actions = Some(Actions(
          items = Seq(
            ActionItem(controllers.routes.BoxNumberController.onLoad().url, Text(ConfirmReasonDetailMessages.change))
          )
        )
        )
      )
  )

  val itemNumber: Seq[SummaryListRow] =
     Seq(SummaryListRow(
        key = Key(
          content = Text(ConfirmReasonDetailMessages.itemNumber),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(
          content = HtmlContent(itemNumberExample.toString)
        ),
        actions = Some(Actions(
          items = Seq(
            ActionItem(controllers.routes.ItemNumberController.onLoad().url, Text(ConfirmReasonDetailMessages.change))
          )
        )
        )
      )
    )
  val answers: Seq[SummaryList] = Seq(
    SummaryList(
      boxNumber ++ itemNumber
    )
  )
}
