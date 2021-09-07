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

package views.data.underpayments

import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryList, SummaryListRow, Value}
import views.ViewUtils.displayMoney

object UnderpaymentDetailConfirmData {

  def underpaymentDetailSummaryList(
    underpaymentType: String,
    bodyMessage: String,
    changeLinkMessageOriginal: String,
    changeLinkMessageAmended: String
  ): SummaryList =
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            content = Text("Amount that was paid"),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(displayMoney(0))
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some(changeLinkMessageOriginal)
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text("Amount that should have been paid"),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(displayMoney(1))
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some(changeLinkMessageAmended)
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text(bodyMessage),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(content = HtmlContent(displayMoney(1))),
          classes = "govuk-summary-list__row--no-border"
        )
      )
    )

}
