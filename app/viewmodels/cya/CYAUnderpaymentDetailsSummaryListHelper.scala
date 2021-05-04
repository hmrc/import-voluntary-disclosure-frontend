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

import models.requests.DataRequest
import pages.FileUploadPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya

trait CYAUnderpaymentDetailsSummaryListHelper {

  def buildUnderpaymentDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val uploadedFilesSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(FileUploadPage) match {
      case Some(files) =>
        val fileNames = files map (file => file.fileName)
        val whichFile = if (fileNames.length == 1) "file" else "files"
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.filesUploaded", fileNames.length, whichFile)),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(fileNames.mkString("\n"))
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
    val rows = uploadedFilesSummaryListRow
    if (rows.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          messages(messages("cya.supportingDocuments")),
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
