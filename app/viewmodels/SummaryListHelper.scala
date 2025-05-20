/*
 * Copyright 2025 HM Revenue & Customs
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

package viewmodels

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

trait SummaryListHelper {

  def encodeMultilineText(content: Seq[String]): String = content.map(line => HtmlFormat.escape(line)).mkString("<br/>")

  def createRow(
    keyText: Content,
    valueContent: Content,
    action: Option[ActionItem] = None,
    keyColumnClasses: String = "govuk-!-width-one-third",
    valueColumnClasses: String = "govuk-!-width-one-half",
    actionColumnClasses: String = "",
    rowClasses: String = ""
  ): SummaryListRow = {

    SummaryListRow(
      key = Key(content = keyText, classes = keyColumnClasses),
      value = Value(content = valueContent, classes = valueColumnClasses),
      actions = action.map(act => Actions(items = Seq(act), classes = actionColumnClasses)),
      classes = rowClasses
    )
  }

  def createChangeActionItem(url: String, accessibilityMessage: String)(implicit messages: Messages): ActionItem =
    ActionItem(
      url,
      HtmlContent(s"""<span aria-hidden="true">${messages("common.change")}</span>"""),
      Some(accessibilityMessage)
    )

  def createDeleteActionItem(url: String, accessibilityMessage: String)(implicit messages: Messages): ActionItem =
    ActionItem(
      url,
      HtmlContent(s"""<span aria-hidden="true">${messages("common.remove")}</span>"""),
      Some(accessibilityMessage)
    )

  def createViewSummaryActionItem(url: String, accessibilityMessage: String)(implicit messages: Messages): ActionItem =
    ActionItem(
      url,
      HtmlContent(s"""<span aria-hidden="true">${messages("cya.viewSummary")}</span>"""),
      Some(accessibilityMessage)
    )

}
