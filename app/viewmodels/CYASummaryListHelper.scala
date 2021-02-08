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

import java.time.format.DateTimeFormatter

import models.UserAnswers
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import views.ViewUtils.displayMoney

class CYASummaryListHelper {

 def buildDisclosureDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val numberOfEntriesSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(NumberOfEntriesPage) map { numberOfEntries =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.numberOfEntries")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent("One"), //TODO - Hardcoded value - refactor once bulk entry added to flow
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

        val epuSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(EntryDetailsPage) map { entryDetails =>
          Seq(
            SummaryListRow(
          key = Key(
            content = Text(messages("cya.epu")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0",
          ),
          value = Value(
            content = HtmlContent(entryDetails.epu),
            classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          ),
            classes = "govuk-summary-list__row--no-border"
        )

          )
      }

   val entryNumberListRow: Option[Seq[SummaryListRow]] = answer.get(EntryDetailsPage) map { entryDetails =>
     Seq(
       SummaryListRow(
         key = Key(
           content = Text(messages("cya.entryNumber")),
           classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0",
         ),
         value = Value(
           content = HtmlContent(entryDetails.entryNumber),
           classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
         ),
         classes = "govuk-summary-list__row--no-border"
       )
     )
     }

   val entryDateListRow: Option[Seq[SummaryListRow]] = answer.get(EntryDetailsPage) map { entryDetails =>
       val entryDateFormat = entryDetails.entryDate.format(DateTimeFormatter.ofPattern("dd MMMM uuuu"))
      Seq(
         SummaryListRow(
         key = Key(
           content = Text(messages("cya.entryDate")),
           classes = "govuk-!-width-two-thirds govuk-!-padding-top-0",
         ),
         value = Value(
           content = HtmlContent(entryDateFormat),
           classes = "govuk-!-padding-top-0"
         )
         )
        )
     }

   val acceptanceDateListRow: Option[Seq[SummaryListRow]] = answer.get(AcceptanceDatePage) map { acceptanceDate =>
       val acceptanceDateValue = if (acceptanceDate) {
         messages("site.yes")
       } else {
         messages("site.no")
       }
     Seq(
       SummaryListRow(
         key = Key(
           content = Text(messages("cya.acceptanceDate")),
           classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
         ),
         value = Value(
           content = HtmlContent(acceptanceDateValue),
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

   val rows = numberOfEntriesSummaryListRow.getOrElse(Seq.empty) ++
     epuSummaryListRow.getOrElse(Seq.empty) ++
     entryNumberListRow.getOrElse(Seq.empty) ++
     entryDateListRow.getOrElse(Seq.empty) ++
     acceptanceDateListRow.getOrElse(Seq.empty)

   if (rows.nonEmpty) {
     Some(
       CYASummaryList(
         messages("cya.disclosureDetails"),
         SummaryList(
           classes = "govuk-!-margin-bottom-9",
           rows = rows
         )
       )
     )
   } else None

  }

  def buildUnderpaymentDetailsSummaryList2(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val customsDuty = answer.get(CustomsDutyPage)
    val importVat = answer.get(ImportVATPage)
    val exciseDuty = answer.get(ExciseDutyPage)

    val customsDutySummaryListRow: Option[SummaryListRow] = if (customsDuty.isDefined) {
      customsDuty map { underpaymentAmount =>
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
      }
    } else Some(SummaryListRow())

    val importVatSummaryListRow: Option[SummaryListRow] = if (importVat.isDefined) {
      importVat map { underpaymentAmount =>
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
      }
    } else Some(SummaryListRow())

    val exciseDutySummaryListRow: Option[SummaryListRow] = if (exciseDuty.isDefined) {
      exciseDuty map { underpaymentAmount =>
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
      }
    } else Some(SummaryListRow())

    Some(CYASummaryList(
      messages("cya.underpaymentDetails"),
      SummaryList(
        classes = "govuk-!-margin-bottom-9",
        rows = if (exciseDutySummaryListRow.isDefined) Seq(customsDutySummaryListRow.get, importVatSummaryListRow.get, exciseDutySummaryListRow.get) else Seq(SummaryListRow())
      )
    )
    )

  }
}



