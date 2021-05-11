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

import models.SelectedDutyTypes
import models.requests.DataRequest
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya

//noinspection ScalaStyle
trait CYADefermentDutyDetailsSummaryListHelper {

  def buildDefermentDutySummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {

    val accountNumberSummaryListRow: Seq[SummaryListRow] = {
      (request.userAnswers.get(DefermentAccountPage), request.userAnswers.get(SplitPaymentPage)) match {
        case (Some(accountNumber), Some(true)) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.repAccountNumber")),
                classes = "govuk-!-width-one-third govuk-!-padding-bottom-0"
              ),
              value = Value(
                content = HtmlContent(accountNumber),
                classes = "govuk-!-padding-bottom-0"
              ),
              actions = Some(Actions(
                items = Seq(
                  ActionItem("Url", Text(messages("cya.change")))
                ),
                classes = "govuk-!-padding-bottom-0"
              )
              ),
              classes = "govuk-summary-list__row--no-border"
            )
          )
        case _ => Seq.empty
      }
    }

    val accountOwnerSummaryListRow: Seq[SummaryListRow] = {
      val accountOwnerContent = request.userAnswers.get(DefermentTypePage) match {
        case Some(value) if value == "A" => messages("cya.myDefermentAccount")
        case Some(value) if value == "B" => messages("cya.importerAuthority")
        case _ => messages("cya.importerStandingAuthority")
      }
      (request.userAnswers.get(DefermentTypePage), request.userAnswers.get(SplitPaymentPage)) match {
        case (Some(_), Some(true)) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.accountOwner")),
                classes = "govuk-!-width-one-third govuk-!-padding-top-0"
              ),
              value = Value(
                content = HtmlContent(accountOwnerContent),
                classes = "govuk-!-padding-top-0"
              )
            )
          )
        case _ => Seq.empty
      }
    }

    val proofOfAuthSummaryListRow: Seq[SummaryListRow] = {

      val fileName = files.filter(file => file.dan == dan).map(_.file.fileName).headOption.getOrElse("No authority file found")


      val filterDan = request.userAnswers.get(UploadAuthorityPage).get.filter(defermentAccount =>
        defermentAccount.dan == request.userAnswers.get(AdditionalDefermentNumberPage).getOrElse("") &&
          defermentAccount.dutyType == SelectedDutyTypes.Duty)

      val getFileNames = filterDan.map(doc => doc.file.fileName)
      val fileNameValue = getFileNames.headOption.getOrElse("")
      (request.userAnswers.get(UploadAuthorityPage), request.userAnswers.get(SplitPaymentPage)) match {
        case (Some(file), Some(true)) if !fileNameValue.isEmpty =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.proofOfAuth")),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = HtmlContent(fileNameValue)
              ),
              actions = Some(Actions(
                items = Seq(
                  ActionItem("Url", Text(messages("cya.change")))
                ))
              )
            )
          )
        case _ => Seq.empty
      }
    }

    val rows = accountNumberSummaryListRow ++
      accountOwnerSummaryListRow ++
      proofOfAuthSummaryListRow


    if (accountNumberSummaryListRow.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          messages(messages("cya.defermentInfoDuty")),
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
