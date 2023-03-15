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

package viewmodels.cya

import models.UserAnswers
import models.importDetails.NumberOfEntries
import models.requests.DataRequest
import pages.importDetails._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{SummaryListHelper, cya}

trait CYAEntryDetailsSummaryListHelper extends SummaryListHelper {

  def buildEntryDetailsSummaryList(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
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

      Seq(
        cya.CYASummaryList(
          Some(messages("cya.entryDetails")),
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
          createChangeActionItem(
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
          createChangeActionItem(
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
          createChangeActionItem(
            controllers.importDetails.routes.EntryDetailsController.onLoad().url,
            messages("cya.entryNumber.change")
          )
        )
      )
    }

  private def buildEntryDateListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(EntryDetailsPage).map { entryDetails =>
      val entryDate =
        s"${entryDetails.entryDate.getDayOfMonth} " + messages(
          s"month.${entryDetails.entryDate.getMonthValue}"
        ) + s" ${entryDetails.entryDate.getYear}"
      createRow(
        keyText = Text(messages("cya.entryDate")),
        valueContent = Text(entryDate),
        action = Some(
          createChangeActionItem(
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
          createChangeActionItem(
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
          createChangeActionItem(
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
          createChangeActionItem(
            controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad().url,
            messages("cya.enterCpc.change")
          )
        )
      )
    }

}
