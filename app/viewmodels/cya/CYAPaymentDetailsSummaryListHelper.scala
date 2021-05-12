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
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya

trait CYAPaymentDetailsSummaryListHelper {

  def buildPaymentDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val paymentMethod = request.userAnswers.get(DefermentPage)
    val splitPayment = request.userAnswers.get(SplitPaymentPage)
    val dan1 = request.userAnswers.get(DefermentAccountPage)
    val defermentType = request.userAnswers.get(DefermentTypePage)
    val uploadAuthority = request.userAnswers.get(UploadAuthorityPage)

    val defermentTypeSummaryListRow: Seq[SummaryListRow] = paymentMethod match {
      case Some(deferment) =>
        val payingByDeferment = if (deferment) messages("cya.payingByDeferment") else messages("cya.payingByOther")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.paymentMethod")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = Text(payingByDeferment)
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

    val splitDefermentSummaryListRow: Seq[SummaryListRow] = (splitPayment, request.dutyType) match {
      case (Some(splitDeferment), SelectedDutyTypes.Both) =>
        val isSplitDeferment = if (splitDeferment) messages("site.yes") else messages("site.no")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.splitDeferment")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = Text(isSplitDeferment)
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

    val accountNumberImporterSummaryListRow: Seq[SummaryListRow] = {
      (paymentMethod, dan1) match {
        case (Some(true), Some(accountNumber)) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.importerAccountNumber")),
                classes = s"govuk-!-width-one-third govuk-summary-list__row"
              ),
              value = Value(
                content = Text(accountNumber),
                classes = "govuk-summary-list__row"
              ),
              actions = Some(Actions(
                items = Seq(
                  ActionItem("Url", Text(messages("cya.change")))
                ),
                classes = "govuk-summary-list__row")
              ),
              classes = "govuk-summary-list__row"
            )
          )
        case _ => Seq.empty
      }
    }

    val accountNumberRepSummaryListRow: Seq[SummaryListRow] = {
      (paymentMethod, splitPayment, request.dutyType, dan1) match {
        case (Some(true), Some(true), SelectedDutyTypes.Both, _) => Seq.empty
        case (Some(true), _, _, Some(accountNumber)) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.repAccountNumber")),
                classes = s"govuk-!-width-one-third govuk-!-padding-bottom-0"
              ),
              value = Value(
                content = Text(accountNumber),
                classes = "govuk-!-padding-bottom-0"
              ),
              actions = Some(Actions(
                items = Seq(
                  ActionItem("Url", Text(messages("cya.change")))
                ),
                classes = "govuk-!-padding-bottom-0")
              ),
              classes = "govuk-summary-list__row--no-border"
            )
          )
        case _ => Seq.empty
      }
    }

    val accountOwnerSummaryListRow: Seq[SummaryListRow] = {
      val accountOwnerContent = defermentType match {
        case Some("A") => messages("cya.myDefermentAccount")
        case Some("B") => messages("cya.importerAuthority")
        case _ => messages("cya.importerStandingAuthority")
      }
      (paymentMethod, splitPayment, request.dutyType) match {
        case (Some(true), Some(true), SelectedDutyTypes.Both) => Seq.empty
        case (Some(true), _, _) =>
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.accountOwner")),
                classes = "govuk-!-width-one-third govuk-!-padding-top-0"
              ),
              value = Value(
                content = Text(accountOwnerContent),
                classes = "govuk-!-padding-top-0"
              )
            )
          )
        case _ => Seq.empty
      }
    }

    val proofOfAuthSummaryListRow: Seq[SummaryListRow] = {
      (paymentMethod, splitPayment, request.dutyType, defermentType, uploadAuthority, dan1) match {
        case (Some(true), Some(true), SelectedDutyTypes.Both, _, _, _) => Seq.empty
        case (Some(true), _, _, Some("B") ,Some(files), Some(dan)) =>
          val fileName = files.filter(file => file.dan == dan).map(_.file.fileName).headOption.getOrElse("No authority file found")
          Seq(
            SummaryListRow(
              key = Key(
                content = Text(messages("cya.proofOfAuth")),
                classes = "govuk-!-width-one-third"
              ),
              value = Value(
                content = Text(fileName)
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

    val rows = if (request.isRepFlow) {
      defermentTypeSummaryListRow ++
        splitDefermentSummaryListRow ++
        accountNumberRepSummaryListRow ++
        accountOwnerSummaryListRow ++
        proofOfAuthSummaryListRow
    } else {
      defermentTypeSummaryListRow ++
        accountNumberImporterSummaryListRow
    }


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
