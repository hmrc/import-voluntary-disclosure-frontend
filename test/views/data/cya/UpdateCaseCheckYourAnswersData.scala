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

import messages.updateCase.UpdateCaseCYAMessages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import viewmodels.cya.CYASummaryList

object UpdateCaseCheckYourAnswersData {

  val changeUrl             = "url"
  val referenceNumber       = "C184567898765333333333"
  val yes                   = "Yes"
  val no                    = "No"
  val file                  = "Example.pdf"
  val additionalInformation = "Hello World"

  def updateCaseAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    None,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    )
  )

  val referenceNumberRow: SummaryListRow = SummaryListRow(
    key = Key(
      Text(UpdateCaseCYAMessages.referenceNumber),
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
            controllers.updateCase.routes.DisclosureReferenceNumberController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(UpdateCaseCYAMessages.changeReferenceNumber)
          )
        )
      )
    )
  )

  def moreDocumentationRow(moreDocuments: Boolean): SummaryListRow = SummaryListRow(
    key = Key(
      Text(UpdateCaseCYAMessages.moreDocumentation),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(if (moreDocuments) yes else no),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.updateCase.routes.MoreDocumentationController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(UpdateCaseCYAMessages.changeMoreDocumentation)
          )
        )
      )
    )
  )

  def fileUploadRow(file: String = file, numberOfFile: Int = 1): SummaryListRow = SummaryListRow(
    key = Key(
      Text(UpdateCaseCYAMessages.filesUploaded(numberOfFile)),
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
            controllers.updateCase.routes.UploadSupportingDocumentationSummaryController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(UpdateCaseCYAMessages.changeUploadedFiles)
          )
        )
      )
    )
  )

  val additionalInformationRow: SummaryListRow = SummaryListRow(
    key = Key(
      Text(UpdateCaseCYAMessages.additionalInformation),
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
            controllers.updateCase.routes.UpdateAdditionalInformationController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(UpdateCaseCYAMessages.changeAdditionalInformation)
          )
        )
      )
    )
  )

  val answers: Seq[CYASummaryList] = Seq(
    updateCaseAnswers(
      Seq(
        referenceNumberRow,
        moreDocumentationRow(true),
        fileUploadRow(),
        additionalInformationRow
      )
    )
  )

}
