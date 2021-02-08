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

    val numberOfEntries = answer.get(NumberOfEntriesPage)
    val entryDetails = answer.get(EntryDetailsPage)
    val acceptanceDate = answer.get(AcceptanceDatePage)

    val numberOfEntriesSummaryListRow: Option[SummaryListRow] = if (numberOfEntries.isDefined) {
      numberOfEntries map { numberOfEntries =>
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
      }
    } else Some(SummaryListRow())

    val epuSummaryListRow: Option[SummaryListRow] = if (entryDetails.isDefined) {
      entryDetails map { entryDetails =>
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
      }
    } else Some(SummaryListRow())

   val entryNumberListRow: Option[SummaryListRow] = if (entryDetails.isDefined) {
     entryDetails map { entryDetails =>
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
     }
   } else Some(SummaryListRow())

   val entryDateListRow: Option[SummaryListRow] = if (entryDetails.isDefined) {
     entryDetails map { entryDetails =>
       val entryDateFormat = entryDetails.entryDate.format(DateTimeFormatter.ofPattern("dd MMMM uuuu"))

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
     }
   } else Some(SummaryListRow())

   val acceptanceDateSummaryListRow: Option[SummaryListRow] = if (acceptanceDate.isDefined) {
     acceptanceDate map { acceptanceDate =>
       val acceptanceDateValue = if (acceptanceDate) {
         messages("site.yes")
       } else {
         messages("site.no")
       }
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
     }
   } else Some(SummaryListRow())

    Some(CYASummaryList(
      messages("cya.disclosureDetails"),
      SummaryList(
        classes = "govuk-!-margin-bottom-9",
        rows = if (numberOfEntriesSummaryListRow.isDefined)
          Seq(
            numberOfEntriesSummaryListRow.get,
            epuSummaryListRow.get,
            entryNumberListRow.get,
            entryDateListRow.get,
            acceptanceDateSummaryListRow.get
          )
        else Seq(SummaryListRow())
      )
    )
    )
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



