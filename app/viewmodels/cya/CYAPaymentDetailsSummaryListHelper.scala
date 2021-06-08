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
import models.{SelectedDutyTypes, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{ActionItemHelper, cya}
import viewmodels.cya.CYAHelper.createRow

trait CYAPaymentDetailsSummaryListHelper {

  def buildPaymentDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {

    val answers = request.userAnswers
    val rows = if (request.isRepFlow) {
      Seq(buildDefermentTypeSummaryListRow(answers),
        buildSplitDefermentSummaryListRow(answers),
        buildAccountNumberRepSummaryListRow(answers),
        buildAccountOwnerSummaryListRow(answers),
        buildProofOfAuthSummaryListRow(answers)).flatten
    } else {
      Seq(buildDefermentTypeSummaryListRow(answers),
        buildAccountNumberImporterSummaryListRow(answers)).flatten
    }

    if (rows.nonEmpty) {
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

  private def buildDefermentTypeSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DefermentPage).map { deferment =>
      val payingByDeferment = if (deferment) messages("cya.payingByDeferment") else messages("cya.payingByOther")
      createRow(
        Text(messages("cya.paymentMethod")),
        Text(payingByDeferment),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.DefermentController.onLoad().url,
          messages("cya.deferment.change")
        ))
      )
    }

  private def buildSplitDefermentSummaryListRow(answers: UserAnswers)(implicit messages: Messages, request: DataRequest[_]): Option[SummaryListRow] =
    (answers.get(SplitPaymentPage), request.dutyType) match {
      case (Some(splitDeferment), SelectedDutyTypes.Both) =>
        val isSplitDeferment = if (splitDeferment) messages("site.yes") else messages("site.no")
        Some(
          createRow(
            Text(messages("cya.splitDeferment")),
            Text(isSplitDeferment),
            action = Some(ActionItemHelper.createChangeActionItem(
              controllers.routes.SplitPaymentController.onLoad().url,
              messages("cya.splitDeferment.change")
            ))
          )
        )
      case _ => None
    }

  private def buildAccountNumberImporterSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    (answers.get(DefermentPage), answers.get(DefermentAccountPage)) match {
      case (Some(true), Some(accountNumber)) =>
        Some(
          createRow(
            Text(messages("cya.importerAccountNumber")),
            Text(accountNumber),
            action = Some(ActionItemHelper.createChangeActionItem(
              controllers.routes.ImporterDanController.onLoad().url,
              messages("cya.importerAccountNumber.change")
            )),
            columnClasses = "govuk-summary-list__row",
            rowClasses = "govuk-summary-list__row"
          )
        )
      case _ => None
    }

  private def buildAccountNumberRepSummaryListRow(answers: UserAnswers)(implicit messages: Messages, request: DataRequest[_]): Option[SummaryListRow] =
    (answers.get(DefermentPage), answers.get(SplitPaymentPage), request.dutyType, answers.get(DefermentAccountPage)) match {
      case (Some(true), Some(true), SelectedDutyTypes.Both, _) => None
      case (Some(true), _, _, Some(accountNumber)) =>
        Some(
          createRow(
            Text(messages("cya.repAccountNumber")),
            Text(accountNumber),
            action = Some(ActionItemHelper.createChangeActionItem(
              controllers.routes.RepresentativeDanController.onLoad().url,
              messages("cya.repDanDuty.change")
            )),
            columnClasses = "govuk-!-padding-bottom-1",
            rowClasses = "govuk-summary-list__row--no-border"
          )
        )
      case _ => None
    }

  private def buildAccountOwnerSummaryListRow(answers: UserAnswers)(implicit messages: Messages, request: DataRequest[_]): Option[SummaryListRow] = {
    val accountOwnerContent = answers.get(DefermentTypePage) match {
      case Some("A") => messages("cya.myDefermentAccount")
      case Some("B") => messages("cya.importerAuthority")
      case _ => messages("cya.importerStandingAuthority")
    }
    (answers.get(DefermentPage), answers.get(SplitPaymentPage), request.dutyType) match {
      case (Some(true), Some(true), SelectedDutyTypes.Both) => None
      case (Some(true), _, _) =>
        Some(
          createRow(
            Text(messages("cya.accountOwner")),
            Text(accountOwnerContent),
            columnClasses = "govuk-!-padding-top-0"
          )
        )
      case _ => None
    }
  }

  private def buildProofOfAuthSummaryListRow(answers: UserAnswers)(implicit messages: Messages, request: DataRequest[_]): Option[SummaryListRow] = {
    (answers.get(DefermentPage), answers.get(SplitPaymentPage), request.dutyType, answers.get(DefermentTypePage), answers.get(UploadAuthorityPage), answers.get(DefermentAccountPage)) match {
      case (Some(true), Some(true), SelectedDutyTypes.Both, _, _, _) => None
      case (Some(true), _, _, Some("B"), Some(files), Some(dan)) =>
        val fileName = files.filter(file => file.dan == dan).map(_.file.fileName).headOption.getOrElse("No authority file found")
        val dutyType = files.filter(file => file.dan == dan).map(_.dutyType).headOption.getOrElse(SelectedDutyTypes.Neither)
        Some(
          createRow(
            Text(messages("cya.proofOfAuth")),
            Text(fileName),
            action = Some(ActionItemHelper.createChangeActionItem(
              controllers.routes.UploadAuthorityController.onLoad(dutyType, answers.get(DefermentAccountPage).get).url,
              messages("cya.proofOfAuth.change")
            ))
          )
        )
      case _ => None
    }
  }


}
