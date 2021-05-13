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
import viewmodels.cya

trait CYADefermentImportVATDetailsSummaryListHelper {

  def buildDefermentImportVatSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val paymentMethod = request.userAnswers.get(DefermentPage)
    val splitPayment = request.userAnswers.get(SplitPaymentPage)

    val answers = request.userAnswers
    val rows = (request.isRepFlow, paymentMethod, splitPayment, request.dutyType) match {
      case (true, Some(true), Some(true), SelectedDutyTypes.Both) =>
        Seq(buildAccountNumberSummaryListRow(answers),
          buildAccountOwnerSummaryListRow(answers),
          buildProofOfAuthSummaryListRow(answers)).flatten
      case _ => Seq.empty
    }


    if (rows.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          messages(messages("cya.defermentInfoImportVAT")),
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

  private def buildAccountNumberSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AdditionalDefermentNumberPage).map { accountNumber =>
      SummaryListRow(
        key = Key(
          content = Text(messages("cya.repAccountNumber")),
          classes = "govuk-!-width-one-third govuk-!-padding-bottom-0"
        ),
        value = Value(
          content = Text(accountNumber),
          classes = "govuk-!-padding-bottom-0"
        ),
        actions = Some(Actions(
          items = Seq(
            ActionItem("Url", Text(messages("cya.change")))
          ),
          classes = "govuk-!-padding-bottom-0"
        )),
        classes = "govuk-summary-list__row--no-border"
      )
    }

  private def buildAccountOwnerSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    val accountOwnerContent = answers.get(AdditionalDefermentTypePage) match {
      case Some("A") => messages("cya.myDefermentAccount")
      case Some("B") => messages("cya.importerAuthority")
      case _ => messages("cya.importerStandingAuthority")
    }
    answers.get(AdditionalDefermentTypePage).map { _ =>
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
    }
  }

  private def buildProofOfAuthSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    val fileName = (answers.get(UploadAuthorityPage), answers.get(AdditionalDefermentNumberPage)) match {
      case (Some(files), Some(dan)) => files.filter(file => file.dan == dan).map(_.file.fileName).headOption.getOrElse("No authority file found")
      case _ => "No authority file found"
    }

    (answers.get(UploadAuthorityPage), answers.get(AdditionalDefermentTypePage)) match {
      case (Some(_), Some("B")) =>
        Some(
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
      case _ => None
    }
  }

}
