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

import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, ChangeUnderpaymentReason, UnderpaymentReason}
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

object ChangeUnderpaymentReasonData {

  private lazy val changeItemAction: Call = controllers.reasons.routes.ChangeItemNumberController.onLoad()

  private def changeDetailsAction(boxNumber: BoxNumber): Call =
    controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(boxNumber.id)

  val singleItemReason: ChangeUnderpaymentReason = ChangeUnderpaymentReason(
    original = UnderpaymentReason(BoxNumber.Box35, 1, "50", "60"),
    changed = UnderpaymentReason(BoxNumber.Box35, 1, "50", "60")
  )

  val otherItemReason: ChangeUnderpaymentReason = ChangeUnderpaymentReason(
    original = UnderpaymentReason(BoxNumber.OtherItem, 0, "Other reason", ""),
    changed = UnderpaymentReason(BoxNumber.OtherItem, 0, "Other reason changed", "")
  )

  val singleEntryLevelReason: ChangeUnderpaymentReason = ChangeUnderpaymentReason(
    original = UnderpaymentReason(BoxNumber.Box22, 0, "50", "60"),
    changed = UnderpaymentReason(BoxNumber.Box22, 0, "50", "60")
  )

  def summaryList(boxNumber: BoxNumber): SummaryList =
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
            content = Text("Original value")
          ),
          value = Value(
            HtmlContent("50")
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
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent("60")
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeDetailsAction(boxNumber).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some("Change amended value")
                )
              )
            )
          )
        )
      )
    )

  def entryLevelSummaryList(boxNumber: BoxNumber): SummaryList =
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("Original value")
          ),
          value = Value(
            HtmlContent("50")
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeDetailsAction(boxNumber).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some("Change")
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text("Amended value"),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent("60")
          )
        )
      )
    )

  def otherItemSummaryList(): SummaryList =
    SummaryList(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text("Other reason")
          ),
          value = Value(
            HtmlContent("Other reason"),
            "govuk-!-width-one-half"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  changeDetailsAction(BoxNumber.OtherItem).url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some("Change other reason")
                )
              )
            )
          )
        )
      )
    )
}
