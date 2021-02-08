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

package viewmodels

import models.UserAnswers
import pages.{CustomsDutyPage, CPCChangedPage, EnterCustomsProcedureCodePage, ExciseDutyPage, ImportVATPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryListRow, Value}
import views.ViewUtils.displayMoney

class CYASummaryListHelper {



  def buildUnderpaymentDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val customsDutySummaryListRow: Option[Seq[SummaryListRow]] = answer.get(CustomsDutyPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.customsDuty")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val importVatSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ImportVATPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.importVat")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val exciseDutySummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ExciseDutyPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.exciseDuty")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val rows = customsDutySummaryListRow.getOrElse(Seq.empty) ++
      importVatSummaryListRow.getOrElse(Seq.empty) ++
      exciseDutySummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages("cya.underpaymentDetails"),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildCustomProcedureCodeSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val customProcedureCodeSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(EnterCustomsProcedureCodePage) map { customsProcedure =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.CustomsProcedureCode")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(customsProcedure),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val CustomProcedureCodeChangedSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(CPCChangedPage) map { customsProcedure =>
      val answer = customsProcedure match {
        case true => messages("site.yes")
        case false => messages("site.no")
      }
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.CPCChange")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(answer),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val exciseDutySummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ExciseDutyPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.exciseDuty")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val rows = customsDutySummaryListRow.getOrElse(Seq.empty) ++
      importVatSummaryListRow.getOrElse(Seq.empty) ++
      exciseDutySummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages("cya.underpaymentDetails"),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }
}
