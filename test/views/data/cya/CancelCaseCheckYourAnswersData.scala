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

package views.data.cya

import messages.cancelCase.CancelCaseCYAMessages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import viewmodels.cya.CYASummaryList

object CancelCaseCheckYourAnswersData {

  val changeUrl             = "url"
  val referenceNumber       = "C184567898765333333333"
  val yes                   = "Yes"
  val no                    = "No"
  val file                  = "Example.pdf"
  val additionalInformation = "Hello World"

  def cancelCaseAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    None,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    )
  )

  val referenceNumberRow: SummaryListRow = SummaryListRow(
    key = Key(
      Text(CancelCaseCYAMessages.referenceNumber),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(referenceNumber),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.cancelCase.routes.CancelCaseReferenceNumberController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CancelCaseCYAMessages.changeReferenceNumber)
          )
        )
      )
    )
  )

  def supportingDocumentationRow(supportingDocuments: Boolean): SummaryListRow = SummaryListRow(
    key = Key(
      Text(CancelCaseCYAMessages.supportingDocumentation),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(if (supportingDocuments) yes else no),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.cancelCase.routes.AnyOtherSupportingCancellationDocsController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CancelCaseCYAMessages.changeSupportingDocumentation)
          )
        )
      )
    )
  )

  val reasonCancellation: SummaryListRow = SummaryListRow(
    key = Key(
      Text(CancelCaseCYAMessages.reasonCancellation),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(additionalInformation),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.cancelCase.routes.CancellationReasonController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CancelCaseCYAMessages.changeReasonCancellation)
          )
        )
      )
    )
  )

  val fileUploadRow: SummaryListRow = SummaryListRow(
    key = Key(
      Text(CancelCaseCYAMessages.filesUploaded(1)),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      HtmlContent(file),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationSummaryController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CancelCaseCYAMessages.changeUploadedFiles)
          )
        )
      )
    )
  )

  val fileUploadRows: SummaryListRow = SummaryListRow(
    key = Key(
      Text(CancelCaseCYAMessages.filesUploaded(2)),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      HtmlContent(s"$file<br/>$file"),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationSummaryController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CancelCaseCYAMessages.changeUploadedFiles)
          )
        )
      )
    )
  )

  val answers: Seq[CYASummaryList] = Seq(
    cancelCaseAnswers(
      Seq(
        referenceNumberRow,
        supportingDocumentationRow(true),
        reasonCancellation,
        fileUploadRow
      )
    )
  )

}
