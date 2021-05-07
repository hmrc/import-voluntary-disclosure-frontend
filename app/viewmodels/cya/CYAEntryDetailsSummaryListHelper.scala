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

package viewmodels.cya

import java.time.format.DateTimeFormatter
import models.NumberOfEntries
import models.requests.DataRequest
import pages.{AcceptanceDatePage, EnterCustomsProcedureCodePage, EntryDetailsPage, NumberOfEntriesPage, OneCustomsProcedureCodePage}
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.{Content, SummaryList}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{ActionItemHelper, cya}

trait CYAEntryDetailsSummaryListHelper {

  def buildEntryDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {

    val numberOfEntriesSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(NumberOfEntriesPage) match {
      case Some(numberOfEntries) =>
        val numberOfEntriesValue = if (numberOfEntries.equals(NumberOfEntries.OneEntry)) messages("cya.oneEntry") else messages("cya.bulkEntry")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.numberOfEntries")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = HtmlContent(numberOfEntriesValue)
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("cya.change")))
              )
            )
            )
          )
        )
      case None => Seq.empty
    }

    val epuSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(EntryDetailsPage) match {
      case Some(entryDetails) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.epu")),
              classes = "govuk-!-width-one-third govuk-!-padding-bottom-0"
            ),
            value = Value(
              content = HtmlContent(entryDetails.epu),
              classes = "govuk-!-padding-bottom-0"
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItemHelper.createChangeActionItem(
                  controllers.routes.EntryDetailsController.onLoad().url,
                  messages("cya.epu.change")
                )
              ),
              classes = "govuk-!-padding-bottom-0")
            ),
            classes = "govuk-summary-list__row--no-border"
          )
        )
      case None => Seq.empty
    }

    val entryNumberListRow: Seq[SummaryListRow] = request.userAnswers.get(EntryDetailsPage) match {
      case Some(entryDetails) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.entryNumber")),
              classes = "govuk-!-width-one-third govuk-!-padding-top-0 govuk-!-padding-bottom-0"
            ),
            value = Value(
              content = HtmlContent(entryDetails.entryNumber),
              classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
            ),
            classes = "govuk-summary-list__row--no-border"
          )
        )
      case None => Seq.empty
    }

    val entryDateListRow: Seq[SummaryListRow] = request.userAnswers.get(EntryDetailsPage) match {
      case Some(entryDetails) =>
        val entryDateFormat = entryDetails.entryDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.entryDate")),
              classes = "govuk-!-width-one-third govuk-!-padding-top-0"
            ),
            value = Value(
              content = HtmlContent(entryDateFormat),
              classes = "govuk-!-padding-top-0"
            )
          )
        )
      case None => Seq.empty
    }

    val acceptanceDateListRow: Seq[SummaryListRow] = request.userAnswers.get(AcceptanceDatePage) match {
      case Some(acceptanceDate) =>
        val acceptanceDateValue = if (acceptanceDate) messages("cya.acceptanceDate.before") else messages("cya.acceptanceDate.after")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.acceptanceDate")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = HtmlContent(acceptanceDateValue)
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItemHelper.createChangeActionItem(
                  controllers.routes.AcceptanceDateController.onLoad().url,
                  messages("cya.acceptanceDate.change")
                )
              )
            )
            )
          )
        )
      case None => Seq.empty
    }

    val oneCustomsProcedureCodeListRow: Seq[SummaryListRow] = request.userAnswers.get(OneCustomsProcedureCodePage) match {
      case Some(customsProcedureCode) =>
        val oneCustomsProcedureCode = if (customsProcedureCode) messages("site.yes") else messages("site.no")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.oneCustomsProcedureCode")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = HtmlContent(oneCustomsProcedureCode)
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("cya.change")))
              )
            )
            )
          )
        )
      case None => Seq.empty
    }

    val customsProcedureCodeListRow: Seq[SummaryListRow] = request.userAnswers.get(EnterCustomsProcedureCodePage) match {
      case Some(customsProcedureCode) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.customsProcedureCode")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = HtmlContent(customsProcedureCode)
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("cya.change")))
              )
            )
            )
          )
        )
      case None => Seq.empty
    }

    val rows = numberOfEntriesSummaryListRow ++
      epuSummaryListRow ++
      entryNumberListRow ++
      entryDateListRow ++
      acceptanceDateListRow ++
      oneCustomsProcedureCodeListRow ++
      customsProcedureCodeListRow

    if (rows.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          messages("cya.entryDetails"),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else {
      Seq.empty
    }
  }

}
