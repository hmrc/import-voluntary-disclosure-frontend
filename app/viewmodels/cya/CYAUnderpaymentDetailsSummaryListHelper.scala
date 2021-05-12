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
import pages.underpayments.UnderpaymentDetailSummaryPage
import pages.{FileUploadPage, HasFurtherInformationPage, MoreInformationPage, UnderpaymentReasonsPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import views.ViewUtils.displayMoney

trait CYAUnderpaymentDetailsSummaryListHelper {

  private def encodeMultilineText(content: Seq[String]): String = content.map(line => HtmlFormat.escape(line)).mkString("<br/>")

  def buildUnderpaymentDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val answers = request.userAnswers
    val rows = Seq(
      buildOwedToHmrcRow(messages, answers),
      buildReasonForUnderpaymentRow(messages, answers),
      buildTellUsAnythingElseRow(messages, answers),
      buildExtraInformationRow(messages, answers),
      buildUploadedFilesRow(messages, answers)
    ).flatten

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

  private def buildUploadedFilesRow(messages: Messages, answers: UserAnswers): Option[SummaryListRow] = {
    answers.get(FileUploadPage).map { files =>
      val fileNames = files map (file => file.fileName)
      val numberOfFiles = if (fileNames.length == 1) "cya.filesUploadedSingle" else "cya.filesUploadedPlural"
      createRow(
        Text(messages(numberOfFiles, fileNames.length)),
        HtmlContent(encodeMultilineText(fileNames)),
        ActionItem("Url", Text(messages("cya.change")))
      )
    }
  }

  private def buildExtraInformationRow(messages: Messages, answers: UserAnswers): Option[SummaryListRow] = {
    answers.get(MoreInformationPage).map { extraInformation =>
      createRow(
        Text(messages("cya.extraInformation")),
        Text(extraInformation),
        ActionItem("Url", Text(messages("cya.change")))
      )
    }
  }

  private def buildTellUsAnythingElseRow(messages: Messages, answers: UserAnswers): Option[SummaryListRow] = {
    answers.get(HasFurtherInformationPage).map { hasFurtherInformation =>
      val furtherInformation = if (hasFurtherInformation) messages("site.yes") else messages("site.no")
      createRow(
        Text(messages("cya.hasFurtherInformation")),
        Text(furtherInformation),
        ActionItem("Url", Text(messages("cya.change")))
      )
    }
  }

  private def buildReasonForUnderpaymentRow(messages: Messages, answers: UserAnswers): Option[SummaryListRow] = {
    answers.get(UnderpaymentReasonsPage).map { underpaymentReason =>
      val numberOfReasons = if (underpaymentReason.size == 1) "cya.numberOfUnderpaymentsSingle" else "cya.numberOfUnderpaymentsPlural"
      createRow(
        Text(messages("cya.reasonForUnderpayment")),
        Text(messages(numberOfReasons, underpaymentReason.size)),
        ActionItem("Url", Text(messages("cya.viewSummary")))
      )
    }
  }

  private def buildOwedToHmrcRow(messages: Messages, answers: UserAnswers): Option[SummaryListRow] = {
    answers.get(UnderpaymentDetailSummaryPage).map { amount =>
      val amountOwed = amount.map(underpayment => underpayment.amended - underpayment.original).sum
      createRow(
        Text(messages("cya.underpaymentDetails.owedToHmrc")),
        Text(displayMoney(amountOwed)),
        ActionItem("Url", Text(messages("cya.viewSummary")))
      )
    }
  }

  private def createRow(keyText: Content, valueContent: Content, action: ActionItem): SummaryListRow = SummaryListRow(
    key = Key(content = keyText, classes = "govuk-!-width-one-third"),
    value = Value(content = valueContent),
    actions = Some(Actions(items = Seq(action)))
  )
}
