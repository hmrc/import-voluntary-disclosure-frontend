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
import pages.shared.MoreDocumentationPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya.CYAHelper._
import viewmodels.{ActionItemHelper, cya}

trait CYAUpdateCaseSummaryListHelper {

  def buildUpdateCaseSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val answers = request.userAnswers
    val rows = Seq(
      buildReferenceNumberSummaryListRow(answers),
      buildMoreDocumentationListRow(answers),
      buildUploadedFilesRow(answers),
      buildExtraInformationRow(answers)
    ).flatten

    if (rows.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          "",
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

  private def buildReferenceNumberSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DisclosureReferenceNumberPage).map { reference =>
      createRow(
        keyText = Text(messages("updateCase.cya.referenceNumber")),
        valueContent = Text(reference),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.DisclosureReferenceNumberController.onLoad().url,
          messages("updateCase.cya.referenceNumber.change")
        ))
      )
    }

  private def buildMoreDocumentationListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(MoreDocumentationPage).map { moreDocumentation =>
      val anyMoreDocumentation = if (moreDocumentation) messages("site.yes") else messages("site.no")
      createRow(
        keyText = Text(messages("updateCase.cya.moreDocumentation")),
        valueContent = Text(anyMoreDocumentation),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.MoreDocumentationController.onLoad().url,
          messages("updateCase.cya.moreDocumentation.change")
        ))
      )
    }

  private def buildUploadedFilesRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(UploadSupportingDocumentationPage).map { files =>
      val fileNames = files map (file => file.fileName)
      val numberOfFiles = if (fileNames.length == 1) "cya.filesUploadedSingle" else "cya.filesUploadedPlural"
      createRow(
        keyText = Text(messages(numberOfFiles, fileNames.length)),
        valueContent = HtmlContent(encodeMultilineText(fileNames)),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.UploadSupportingDocumentationSummaryController.onLoad().url,
          messages("updateCase.cya.uploadedFiles.change")
        ))
      )
    }
  }

  private def buildExtraInformationRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    answers.get(UpdateAdditionalInformationPage).map { moreInformation =>
      createRow(
        keyText = Text(messages("updateCase.cya.moreInformation")),
        valueContent = Text(moreInformation),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.UpdateAdditionalInformationController.onLoad().url,
          messages("updateCase.cya.moreInformation.change")
        ))
      )
    }
  }

}
