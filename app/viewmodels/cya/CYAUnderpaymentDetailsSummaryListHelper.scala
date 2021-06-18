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
import models.requests.DataRequest
import pages._
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya.CYAHelper._
import viewmodels.{ActionItemHelper, cya}
import views.ViewUtils.displayMoney

trait CYAUnderpaymentDetailsSummaryListHelper {

  def buildUnderpaymentDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val answers = request.userAnswers
    val rows = if (request.isOneEntry) {
      Seq(
        buildOwedToHmrcRow(answers),
        buildReasonForUnderpaymentRow(answers),
        buildTellUsAnythingElseRow(answers),
        buildExtraInformationRow(answers),
        buildUploadedFilesRow(answers)
      ).flatten
    } else {
      Seq(
        buildNumberOfEntriesSummaryListRow(answers),
        buildAcceptanceDateListRow(answers),
        buildOwedToHmrcRow(answers),
        buildExtraInformationRow(answers),
        buildMultipleEntriesFileRow(answers)
      ).flatten
    }
    if (rows.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          messages(messages("cya.underpaymentDetails")),
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

  private def buildUploadedFilesRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(FileUploadPage).map { files =>
      val fileNames = files map (file => file.fileName)
      val numberOfFiles = if (fileNames.length == 1) "cya.filesUploadedSingle" else "cya.filesUploadedPlural"
      createRow(
        keyText = Text(messages(numberOfFiles, fileNames.length)),
        valueContent = HtmlContent(encodeMultilineText(fileNames)),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.UploadAnotherFileController.onLoad().url,
          messages("cya.supportingDocuments.change")
        ))
      )
    }
  }

  private def buildExtraInformationRow(answers: UserAnswers)(implicit messages: Messages, request: DataRequest[_]): Option[SummaryListRow] = {
    val keyTextMessage = if (request.isOneEntry) messages("cya.extraInformation") else messages("cya.bulk.reasonForUnderpayment")
    val changeTextMessage = if (request.isOneEntry) messages("cya.extraInformation.change") else messages("cya.bulk.reasonForUnderpayment.change")
    answers.get(MoreInformationPage).map { extraInformation =>
      createRow(
        keyText = Text(keyTextMessage),
        valueContent = Text(extraInformation),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.MoreInformationController.onLoad().url,
          changeTextMessage
        ))
      )
    }
  }

  private def buildTellUsAnythingElseRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(HasFurtherInformationPage).map { hasFurtherInformation =>
      val furtherInformation = if (hasFurtherInformation) messages("site.yes") else messages("site.no")
      createRow(
        keyText = Text(messages("cya.hasFurtherInformation")),
        valueContent = Text(furtherInformation),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.HasFurtherInformationController.onLoad().url,
          messages("cya.hasFurtherInformation.change")
        ))
      )
    }
  }

  private def buildReasonForUnderpaymentRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(UnderpaymentReasonsPage).map { underpaymentReason =>
      val numberOfReasons = if (underpaymentReason.size == 1) "cya.numberOfUnderpaymentsSingle" else "cya.numberOfUnderpaymentsPlural"
      createRow(
        Text(messages("cya.reasonForUnderpayment")),
        Text(messages(numberOfReasons, underpaymentReason.size)),
        Some(ActionItemHelper.createViewSummaryActionItem(
          controllers.routes.UnderpaymentReasonSummaryController.onLoad().url,
          messages("cya.reasonForUnderpayment.change")
        ))
      )
    }
  }

  private def buildOwedToHmrcRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(UnderpaymentDetailSummaryPage).map { amount =>
      val amountOwed = amount.map(underpayment => underpayment.amended - underpayment.original).sum
      createRow(
        Text(messages("cya.underpaymentDetails.owedToHmrc")),
        Text(displayMoney(amountOwed)),
        Some(ActionItemHelper.createViewSummaryActionItem(
          controllers.underpayments.routes.UnderpaymentDetailSummaryController.cya().url,
          messages("cya.underpaymentDetails.owedToHmrc.change")
        ))
      )
    }
  }

  private def buildNumberOfEntriesSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(NumberOfEntriesPage).map { numberOfEntries =>
      createRow(
        keyText = Text(messages("cya.numberOfEntries")),
        valueContent = Text(messages("cya.bulkEntry")),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.NumberOfEntriesController.onLoad().url,
          messages("cya.numberOfEntries.change")
        ))
      )
    }

  private def buildAcceptanceDateListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AcceptanceDatePage).map { acceptanceDate =>
      val acceptanceDateValue = if (acceptanceDate) messages("cya.bulk.acceptanceDate.before") else messages("cya.bulk.acceptanceDate.after")
      createRow(
        keyText = Text(messages("cya.bulk.acceptanceDate")),
        valueContent = Text(acceptanceDateValue),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.AcceptanceDateController.onLoad().url,
          messages("cya.bulk.acceptanceDate.change")
        ))
      )
    }

  private def buildMultipleEntriesFileRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(FileUploadPage).map { files =>
      val fileNames = files map (file => file.fileName)
      createRow(
        keyText = Text(messages("cya.bulk.multipleEntriesFile")),
        valueContent = HtmlContent(encodeMultilineText(fileNames)),
        action = Some(ActionItemHelper.createChangeActionItem(
          "Url",
          messages("cya.bulk.multipleEntriesFile.change")
        ))
      )
    }
  }

}
