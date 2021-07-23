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

package views.data.reasons

import models.reasons.{ChangeUnderpaymentReason, UnderpaymentReason}
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

object ChangeUnderpaymentReasonData {

  private lazy val changeItemAction: Call = controllers.reasons.routes.ChangeItemNumberController.onLoad()

  private def changeDetailsAction(boxNumber: Int): Call = controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(boxNumber)

  val singleItemReason: ChangeUnderpaymentReason = ChangeUnderpaymentReason(
    original = UnderpaymentReason(35, 1, "50", "60"),
    changed = UnderpaymentReason(35, 1, "50", "60")
  )

  val singleEntryLevelReason: ChangeUnderpaymentReason = ChangeUnderpaymentReason(
    original = UnderpaymentReason(22, 0, "50", "60"),
    changed = UnderpaymentReason(22, 0, "50", "60")
  )


  def summaryList(boxNumber: Int): SummaryList =
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("Item number")
          ),
          value = Value(
            HtmlContent("1")
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeItemAction.url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some("Change item number")
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text("Original value"),
            classes = "govuk-!-padding-bottom-1"
          ),
          value = Value(
            HtmlContent("50"),
            classes = "govuk-!-padding-bottom-1"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeDetailsAction(boxNumber).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some("Change original value")
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text("Amended value"),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent("60"),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeDetailsAction(boxNumber).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some("Change amended value")
                )
              ),
              classes = "govuk-!-padding-bottom-1"
            )
          )
        )
      )
    )

  def entryLevelSummaryList(boxNumber: Int): SummaryList =
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("Original value"),
            classes = "govuk-!-padding-bottom-1"
          ),
          value = Value(
            HtmlContent("50"),
            classes = "govuk-!-padding-bottom-1"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeDetailsAction(boxNumber).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some("Change")
                )
              ),
              classes = "govuk-!-padding-bottom-1"
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text("Amended value"),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent("60"),
            classes = "govuk-!-padding-top-0"
          )
        )
      )
    )
}
