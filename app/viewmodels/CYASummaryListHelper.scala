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

package viewmodels

import models.UserAnswers
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import views.ViewUtils.displayMoney

class CYASummaryListHelper {


  def buildUnderpaymentDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val customsDutySummaryListRow: Option[Seq[SummaryListRow]] = answer.get(CustomsDutyPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.customsDuty")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val importVatSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ImportVATPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.importVat")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val exciseDutySummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ExciseDutyPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.exciseDuty")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val rows = customsDutySummaryListRow.getOrElse(Seq.empty) ++
      importVatSummaryListRow.getOrElse(Seq.empty) ++
      exciseDutySummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages("cya.underpaymentDetails"),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildCustomProcedureCodeSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {

    val customProcedureCodeSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(EnterCustomsProcedureCodePage) map { customsProcedure =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.CustomsProcedureCode")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(customsProcedure),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val CustomProcedureCodeChangedSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(CPCChangedPage) map { customsProcedure =>
      val answer = customsProcedure match {
        case true => messages("site.yes")
        case false => messages("site.no")
      }
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.CPCChange")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(answer),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val exciseDutySummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ExciseDutyPage) map { underpaymentAmount =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.exciseDuty")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpaymentAmount.amended - underpaymentAmount.original)),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val rows = customProcedureCodeSummaryListRow.getOrElse(Seq.empty) ++
      CustomProcedureCodeChangedSummaryListRow.getOrElse(Seq.empty) ++
      exciseDutySummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages("cya.amendmentDetails"),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildSupportingDocumentsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {
    val uploadedFilesSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(FileUploadPage) map { files =>
      val fileNames = files map (file => file.fileName)
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.filesUploaded", fileNames.length)),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(fileNames.mkString("\n")),
            classes = "govuk-!-padding-top-0 govuk-summary-list__value",
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }
    val rows = uploadedFilesSummaryListRow.getOrElse(Seq.empty)
    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages(messages("cya.supportingDocuments")),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }

  def buildYourDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {
    val detailsSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(TraderContactDetailsPage) map { details =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.name")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(details.fullName),
            classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0 govuk-!-padding-bottom-0")
          ),
          classes = "govuk-summary-list__row--no-border govuk-!-padding-bottom-0"
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.email")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(details.email),
            classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          actions = None,
          classes = "govuk-summary-list__row--no-border govuk-!-padding-bottom-0 govuk-!-padding-top-0"
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.phone")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(details.phoneNumber),
            classes = "govuk-!-padding-top-0"
          ),
          actions = None,
          classes = "govuk-!-padding-top-0"
        )
      )
    }

    val addressSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(ImporterAddressFinalPage) map { address =>
      val addressString = address.postalCode match {
        case Some(value) => address.streetAndNumber + "<br/>" +
          address.city + "<br/>" +
          address.postalCode.get + "<br/>" +
          address.countryCode
        case None => address.streetAndNumber + "<br/>" +
          address.city + "<br/>" +
          address.countryCode
      }
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("cya.address")), // TODO
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(addressString),
            classes = "govuk-!-padding-top-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem("Url", Text(messages("cya.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          )
        )
      )
    }

    val rows = detailsSummaryListRow.getOrElse(Seq.empty) ++ addressSummaryListRow.getOrElse(Seq.empty)
    if (rows.nonEmpty) {
      Some(
        CYASummaryList(
          messages(messages("cya.yourDetails")),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None
  }


}
