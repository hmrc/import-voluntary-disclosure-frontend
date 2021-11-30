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

import models.UserAnswers
import models.importDetails.NumberOfEntries
import models.requests.DataRequest
import pages.importDetails._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya.CYAHelper._
import viewmodels.{ActionItemHelper, cya}

trait CYAEntryDetailsSummaryListHelper {

  def buildEntryDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    if (request.isOneEntry) {
      val answers = request.userAnswers
      val rows = Seq(
        buildNumberOfEntriesSummaryListRow(answers),
        buildEpuSummaryListRow(answers),
        buildEntryNumberListRow(answers),
        buildEntryDateListRow(answers),
        buildAcceptanceDateListRow(answers),
        buildOneCustomsProcedureCodeListRow(answers),
        buildCustomsProcedureCodeListRow(answers)
      ).flatten

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
    } else {
      Seq.empty
    }
  }

  private def buildNumberOfEntriesSummaryListRow(
    answers: UserAnswers
  )(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(NumberOfEntriesPage).map { numberOfEntries =>
      val numberOfEntriesValue =
        if (numberOfEntries.equals(NumberOfEntries.OneEntry)) messages("cya.oneEntry") else messages("cya.bulkEntry")
      createRow(
        keyText = Text(messages("cya.numberOfEntries")),
        valueContent = Text(numberOfEntriesValue),
        action = Some(
          ActionItemHelper.createChangeActionItem(
            controllers.importDetails.routes.NumberOfEntriesController.onLoad().url,
            messages("cya.numberOfEntries.change")
          )
        )
      )
    }

  private def buildEpuSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(EntryDetailsPage).map { entryDetails =>
      createRow(
        keyText = Text(messages("cya.epu")),
        valueContent = Text(entryDetails.epu),
        action = Some(
          ActionItemHelper.createChangeActionItem(
            controllers.importDetails.routes.EntryDetailsController.onLoad().url,
            messages("cya.epu.change")
          )
        )
      )
    }

  private def buildEntryNumberListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(EntryDetailsPage).map { entryDetails =>
      createRow(
        keyText = Text(messages("cya.entryNumber")),
        valueContent = Text(entryDetails.entryNumber),
        action = Some(
          ActionItemHelper.createChangeActionItem(
            controllers.importDetails.routes.EntryDetailsController.onLoad().url,
            messages("cya.entryNumber.change")
          )
        )
      )
    }

  private def buildEntryDateListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(EntryDetailsPage).map { entryDetails =>
      val entryMonth = entryDetails.entryDate.getMonth.toString match {
        case "JANUARY"   => messages("cya.month.1")
        case "FEBRUARY"  => messages("cya.month.2")
        case "MARCH"     => messages("cya.month.3")
        case "APRIL"     => messages("cya.month.4")
        case "MAY"       => messages("cya.month.5")
        case "JUNE"      => messages("cya.month.6")
        case "JULY"      => messages("cya.month.7")
        case "AUGUST"    => messages("cya.month.8")
        case "SEPTEMBER" => messages("cya.month.9")
        case "OCTOBER"   => messages("cya.month.10")
        case "NOVEMBER"  => messages("cya.month.11")
        case "DECEMBER"  => messages("cya.month.12")
      }
      val entryDateFormat =
        entryDetails.entryDate.getDayOfMonth + " " + entryMonth + " " + entryDetails.entryDate.getYear
      createRow(
        keyText = Text(messages("cya.entryDate")),
        valueContent = Text(entryDateFormat),
        action = Some(
          ActionItemHelper.createChangeActionItem(
            controllers.importDetails.routes.EntryDetailsController.onLoad().url,
            messages("cya.entryDate.change")
          )
        )
      )
    }

  private def buildAcceptanceDateListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AcceptanceDatePage).map { acceptanceDate =>
      val acceptanceDateValue =
        if (acceptanceDate) messages("cya.single.acceptanceDate.before")
        else messages("cya.single.acceptanceDate.after")
      createRow(
        keyText = Text(messages("cya.acceptanceDate")),
        valueContent = Text(acceptanceDateValue),
        action = Some(
          ActionItemHelper.createChangeActionItem(
            controllers.importDetails.routes.AcceptanceDateController.onLoad().url,
            messages("cya.acceptanceDate.change")
          )
        )
      )
    }

  private def buildOneCustomsProcedureCodeListRow(
    answers: UserAnswers
  )(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(OneCustomsProcedureCodePage).map { customsProcedureCode =>
      val oneCustomsProcedureCode = if (customsProcedureCode) messages("site.yes") else messages("site.no")
      createRow(
        keyText = Text(messages("cya.oneCustomsProcedureCode")),
        valueContent = Text(oneCustomsProcedureCode),
        action = Some(
          ActionItemHelper.createChangeActionItem(
            controllers.importDetails.routes.OneCustomsProcedureCodeController.onLoad().url,
            messages("cya.cpcExists.change")
          )
        )
      )
    }

  private def buildCustomsProcedureCodeListRow(
    answers: UserAnswers
  )(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(EnterCustomsProcedureCodePage).map { customsProcedureCode =>
      createRow(
        keyText = Text(messages("cya.customsProcedureCode")),
        valueContent = Text(customsProcedureCode),
        action = Some(
          ActionItemHelper.createChangeActionItem(
            controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad().url,
            messages("cya.enterCpc.change")
          )
        )
      )
    }

}
