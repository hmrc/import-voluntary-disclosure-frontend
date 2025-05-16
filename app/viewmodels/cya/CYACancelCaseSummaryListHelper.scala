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

package viewmodels.cya

import models.UserAnswers
import models.requests.DataRequest
import pages.shared.MoreDocumentationPage
import pages.updateCase._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{SummaryListHelper, cya}

trait CYACancelCaseSummaryListHelper extends SummaryListHelper {

  def buildCancelCaseSummaryList(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val answers = request.userAnswers
    val rows = Seq(
      buildReferenceNumberSummaryListRow(answers),
      buildReasonForCancellation(answers),
      buildSupportingDocumentationListRow(answers),
      buildUploadedFilesRow(answers)
    ).flatten

    if (rows.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          None,
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

  private def buildReferenceNumberSummaryListRow(
    answers: UserAnswers
  )(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DisclosureReferenceNumberPage).map { reference =>
      createRow(
        keyText = Text(messages("cancelCase.cya.referenceNumber")),
        valueContent = Text(reference),
        action = Some(
          createChangeActionItem(
            controllers.cancelCase.routes.CancelCaseReferenceNumberController.onLoad().url,
            messages("cancelCase.cya.referenceNumber.change")
          )
        )
      )
    }

  private def buildReasonForCancellation(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(UpdateAdditionalInformationPage).map { moreInformation =>
      createRow(
        keyText = Text(messages("cancelCase.cya.reasonCancellation")),
        valueContent = Text(moreInformation),
        action = Some(
          createChangeActionItem(
            controllers.cancelCase.routes.CancellationReasonController.onLoad().url,
            messages("cancelCase.cya.reasonCancellation.change")
          )
        )
      )
    }
  }

  private def buildSupportingDocumentationListRow(
    answers: UserAnswers
  )(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(MoreDocumentationPage).map { supportingDocumentation =>
      val anySupportingDocumentation = if (supportingDocumentation) messages("site.yes") else messages("site.no")
      createRow(
        keyText = Text(messages("cancelCase.cya.supportingDocumentation")),
        valueContent = Text(anySupportingDocumentation),
        action = Some(
          createChangeActionItem(
            controllers.cancelCase.routes.AnyOtherSupportingCancellationDocsController.onLoad().url,
            messages("cancelCase.cya.supportingDocumentation.change")
          )
        )
      )
    }

  private def buildUploadedFilesRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(UploadSupportingDocumentationPage).map { files =>
      val fileNames     = files map (file => file.fileName)
      val numberOfFiles = if (fileNames.length == 1) "cya.filesUploadedSingle" else "cya.filesUploadedPlural"
      createRow(
        keyText = Text(messages(numberOfFiles, fileNames.length)),
        valueContent = HtmlContent(encodeMultilineText(fileNames)),
        action = Some(
          createChangeActionItem(
            controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationSummaryController.onLoad().url,
            messages("cancelCase.cya.uploadedFiles.change")
          )
        )
      )
    }
  }

}
