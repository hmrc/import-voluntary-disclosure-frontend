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

package views.data.cya

import messages.UpdateCaseCYAMessages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import viewmodels.cya.CYASummaryList

object UpdateCaseCheckYourAnswersData {

  val changeUrl = "Url"
  val referenceNumber = "C184567898765333333333"
  val yes = "Yes"
  val file = "Example.pdf"
  val additionalInformation = "Hello World"

  def updateCaseAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    "",
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    )
  )


  val referenceNumberRow = SummaryListRow(
    key = Key(
      Text(UpdateCaseCYAMessages.referenceNumber)
    ),
    value = Value(
      Text(referenceNumber)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        changeUrl,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some("")
      )
    )
    ))
  )

  val moreDocumentationRow = SummaryListRow(
    key = Key(
      Text(UpdateCaseCYAMessages.moreDocumentation)
    ),
    value = Value(
      Text(yes)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        changeUrl,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some("")
      )
    )
    ))
  )

  val fileUploadRow = SummaryListRow(
    key = Key(
      Text(UpdateCaseCYAMessages.filesUploaded(1))
    ),
    value = Value(
      Text(file)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        changeUrl,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some("")
      )
    )
    ))
  )

  val additionalInformationRow = SummaryListRow(
    key = Key(
      Text(UpdateCaseCYAMessages.additionalInformation)
    ),
    value = Value(
      Text(additionalInformation)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        changeUrl,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some("")
      )
    )
    ))
  )


  val answers: Seq[CYASummaryList] = Seq(

    updateCaseAnswers(Seq(
      referenceNumberRow,
      moreDocumentationRow,
      fileUploadRow,
      additionalInformationRow
    )),
  )

}
