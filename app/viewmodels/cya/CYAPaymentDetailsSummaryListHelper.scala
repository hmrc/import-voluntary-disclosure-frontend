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
import pages.{DefermentAccountPage, DefermentPage, DefermentTypePage, SplitPaymentPage, UploadAuthorityPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya

//noinspection ScalaStyle
trait CYAPaymentDetailsSummaryListHelper {

  def buildPaymentDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val defermentTypeSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(DefermentPage) match {
      case Some(deferment) =>
        val payingByDeferment = if (deferment) messages("cya.payingByDeferment") else messages("cya.payingByOther")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.payingByDeferment")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = HtmlContent(payingByDeferment)
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("cya.change")))
              ))
            )
          )
        )
      case None => Seq.empty
    }

    val splitDefermentSummaryListRow: Seq[SummaryListRow] =
      (request.userAnswers.get(SplitPaymentPage), request.dutyType) match {
        case (Some(splitDeferment), SelectedDutyTypes.Both) =>
          val isSplitDeferment = if (splitDeferment) messages("site.yes") else messages("site.no")
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.splitDeferment")),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = HtmlContent(isSplitDeferment)
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

    val accountNumberSummaryListRow: Seq[SummaryListRow] = {
      val accountNumberContent = if(request.isRepFlow) messages("cya.repAccountNumber") else messages("cya.importerAccountNumber")
      (request.userAnswers.get(DefermentAccountPage), request.dutyType, request.userAnswers.get(SplitPaymentPage)) match {
        case (Some(accountNumber), SelectedDutyTypes.Both, Some(false)) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(accountNumberContent),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = HtmlContent(accountNumber)
              ),
              actions = Some(Actions(
                items = Seq(
                  ActionItem("Url", Text(messages("cya.change")))
                ))
              )
            )
          )
        case (Some(accountNumber), _, None) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(accountNumberContent),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = HtmlContent(accountNumber)
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

    val accountOwnerSummaryListRow: Seq[SummaryListRow] = {
      val accountOwnerContent = request.userAnswers.get(DefermentTypePage) match {
        case Some(value) if value == "A" => messages("cya.myDefermentAccount")
        case Some(value) if value == "B" => messages("cya.importerAuthority")
        case _ => messages("cya.importerStandingAuthority")
      }
      (request.userAnswers.get(DefermentTypePage), request.dutyType, request.userAnswers.get(SplitPaymentPage)) match {
        case (Some(_), SelectedDutyTypes.Both, Some(false)) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.accountOwner")),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = HtmlContent(accountOwnerContent)
              ),
              actions = Some(Actions(
                items = Seq(
                  ActionItem("Url", Text(messages("cya.change")))
                ))
              )
            )
          )
        case (Some(_), _, None) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.accountOwner")),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = HtmlContent(accountOwnerContent)
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

    val proofOfAuthSummaryListRow: Seq[SummaryListRow] = {
      (request.userAnswers.get(UploadAuthorityPage), request.dutyType, request.userAnswers.get(SplitPaymentPage)) match {
        case (Some(file), SelectedDutyTypes.Both, Some(false)) =>
          val fileName = file.map (doc => doc.file.fileName).head
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.proofOfAuth")),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = HtmlContent(fileName)
              ),
              actions = Some(Actions(
                items = Seq(
                  ActionItem("Url", Text(messages("cya.change")))
                ))
              )
            )
          )
        case (Some(file), _, None) =>
          val fileName = file.map (doc => doc.file.fileName).head
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.proofOfAuth")),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = HtmlContent(fileName)
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

    val rows = defermentTypeSummaryListRow ++
      splitDefermentSummaryListRow ++
      accountNumberSummaryListRow ++
      accountOwnerSummaryListRow ++
      proofOfAuthSummaryListRow


    if (defermentTypeSummaryListRow.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          messages(messages("cya.paymentInformation")),
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
