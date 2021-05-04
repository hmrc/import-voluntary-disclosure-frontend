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

import java.time.format.DateTimeFormatter

import models.{NumberOfEntries, UserAnswers, UserType}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

trait CYASummaryListHelper {

  def buildDisclosureDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Seq[CYASummaryList] = {

    val numberOfEntriesSummaryListRow: Seq[SummaryListRow] = answer.get(NumberOfEntriesPage) match {
      case Some(numberOfEntries) =>
        val numberOfEntriesValue = if (numberOfEntries.equals(NumberOfEntries.OneEntry)) messages("cya.oneEntry") else messages("cya.bulkEntry")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.numberOfEntries")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(numberOfEntriesValue)
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

    val epuSummaryListRow: Seq[SummaryListRow] = answer.get(EntryDetailsPage) match {
      case Some(entryDetails) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.epu")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
            ),
            value = Value(
              content = HtmlContent(entryDetails.epu),
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
      case None => Seq.empty
    }

    val entryNumberListRow: Seq[SummaryListRow] = answer.get(EntryDetailsPage) match {
      case Some(entryDetails) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.entryNumber")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
            ),
            value = Value(
              content = HtmlContent(entryDetails.entryNumber),
              classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
            ),
            classes = "govuk-summary-list__row--no-border"
          )
        )
      case None => Seq.empty
    }

    val entryDateListRow: Seq[SummaryListRow] = answer.get(EntryDetailsPage) match {
      case Some(entryDetails) =>
        val entryDateFormat = entryDetails.entryDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.entryDate")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
            ),
            value = Value(
              content = HtmlContent(entryDateFormat),
              classes = "govuk-!-padding-top-0"
            )
          )
        )
      case None => Seq.empty
    }

    val acceptanceDateListRow: Seq[SummaryListRow] = answer.get(AcceptanceDatePage) match {
      case Some(acceptanceDate) =>
        val acceptanceDateValue = if (acceptanceDate) messages("site.yes") else messages("site.no")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.acceptanceDate")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(acceptanceDateValue)
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

    val rows = numberOfEntriesSummaryListRow ++
    epuSummaryListRow ++
    entryNumberListRow ++
    entryDateListRow ++
    acceptanceDateListRow

    if (rows.nonEmpty) {
      Seq(
        CYASummaryList(
          messages("cya.disclosureDetails"),
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

  def buildAmendmentDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Seq[CYASummaryList] = {

    val customProcedureCodeSummaryListRow: Seq[SummaryListRow] = answer.get(EnterCustomsProcedureCodePage) match {
      case Some(customsProcedure) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.customsProcedureCode")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(customsProcedure)
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

    //    val numberOfAmendmentsSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(numberOfAmendmentsPage) map { numberOfAmendments =>
    //      Seq(
    //        SummaryListRow(
    //          key = Key(
    //            content = Text(messages("cya.numberOfAmendments")),
    //            classes = "govuk-!-width-two-thirds"
    //          ),
    //          value = Value(
    //            content = HtmlContent(numberOfAmendments)
    //          ),
    //          actions = Some(Actions(
    //            items = Seq(
    //              ActionItem("Url", Text(messages("cya.change")))
    //            )
    //          )
    //          )
    //        )
    //      )
    //    }
    //
    //val supportingInformationSummaryListRow: Option[Seq[SummaryListRow]] = answer.get(SupportingInformationPage) map { supportingInformation =>
    //  Seq(
    //    SummaryListRow(
    //      key = Key(
    //        content = Text(messages("cya.supportingInformation")),
    //        classes = "govuk-!-width-two-thirds"
    //      ),
    //      value = Value(
    //        content = HtmlContent(supportingInformation)
    //      ),
    //      actions = Some(Actions(
    //        items = Seq(
    //          ActionItem("Url", Text(messages("cya.change")))
    //        )
    //      )
    //      )
    //    )
    //  )
    //}

    val rows = customProcedureCodeSummaryListRow
    //      numberOfAmendmentsSummaryListRow.getOrElse(Seq.empty) ++
    //      supportingInformationSummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Seq(
        CYASummaryList(
          messages("cya.amendmentDetails"),
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

  def buildSupportingDocumentsSummaryList(answer: UserAnswers)(implicit messages: Messages): Seq[CYASummaryList] = {
    val uploadedFilesSummaryListRow: Seq[SummaryListRow] = answer.get(FileUploadPage) match {
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
        CYASummaryList(
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

  def buildYourDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Seq[CYASummaryList] = {
    val detailsSummaryListRow: Seq[SummaryListRow] = answer.get(DeclarantContactDetailsPage) match {
      case Some(details) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.name")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
            ),
            value = Value(
              content = HtmlContent(details.fullName),
              classes = "govuk-!-padding-bottom-0"
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("cya.change")))
              ),
              classes = "govuk-!-padding-bottom-0")
            ),
            classes = "govuk-summary-list__row--no-border"
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
            classes = "govuk-summary-list__row--no-border"
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
            classes = "govuk-!-padding-top-0"
          )
        )
      case None => Seq.empty
    }

    val addressSummaryListRow: Seq[SummaryListRow] = answer.get(TraderAddressPage) match {
      case Some(address) =>
        val addressString = address.postalCode match {
          case Some(value) => address.addressLine1 + "<br/>" +
            address.city + "<br/>" +
            address.postalCode.get + "<br/>" +
            address.countryCode
          case None => address.addressLine1 + "<br/>" +
            address.city + "<br/>" +
            address.countryCode
        }
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.address")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(addressString)
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

    val rows = detailsSummaryListRow ++ addressSummaryListRow
    if (rows.nonEmpty) {
      Seq(
        CYASummaryList(
          messages(messages("cya.yourDetails")),
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

  def buildPaymentInformationSummaryList(answer: UserAnswers)(implicit messages: Messages): Seq[CYASummaryList] = {
    val paymentInformationSummaryListRow: Seq[SummaryListRow] = answer.get(DefermentPage) match {
      case Some(deferment) =>
        val payingByDeferment = if (deferment) messages("site.yes") else messages("site.no")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.payingByDeferment")),
              classes = "govuk-!-width-two-thirds"
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
    if (paymentInformationSummaryListRow.nonEmpty) {
      Seq(
        CYASummaryList(
          messages(messages("cya.paymentInformation")),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = paymentInformationSummaryListRow
          )
        )
      )
    } else {
      Seq.empty
    }
  }

  def buildAboutImporterSummaryList(answer: UserAnswers)(implicit messages: Messages): Seq[CYASummaryList] = {
    val importerNameSummaryListRow: Seq[SummaryListRow] = answer.get(ImporterNamePage) match {
      case Some(importerName) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.name")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(importerName)
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
    val addressSummaryListRow: Seq[SummaryListRow] = answer.get(ImporterAddressPage) match {
      case Some(address) =>
        val addressString = address.postalCode match {
          case Some(value) => address.addressLine1 + "<br/>" +
            address.city + "<br/>" +
            address.postalCode.get + "<br/>" +
            address.countryCode
          case None => address.addressLine1 + "<br/>" +
            address.city + "<br/>" +
            address.countryCode
        }
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.address")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(addressString)
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
    val eoriNumberExistsSummaryListRow: Seq[SummaryListRow] = answer.get(ImporterEORIExistsPage) match {
      case Some(eoriExists) =>
        val eoriNumberExists = if (eoriExists) messages("site.yes") else messages("site.no")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.eoriNumberExists")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(eoriNumberExists)
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
    val eoriNumberSummaryListRow: Seq[SummaryListRow] = answer.get(ImporterEORINumberPage) match {
      case Some(eoriNumber) =>
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.eoriNumber")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(eoriNumber)
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
    val VatRegisteredSummaryListRow: Seq[SummaryListRow] = answer.get(ImporterVatRegisteredPage) match {
      case Some(registered) =>
        val isVatRegistered = if (registered) messages("site.yes") else messages("site.no")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.vatRegistered")),
              classes = "govuk-!-width-two-thirds"
            ),
            value = Value(
              content = HtmlContent(isVatRegistered)
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

    answer.get(UserTypePage) match {
      case Some(userType) if userType == UserType.Representative =>
        val rows = importerNameSummaryListRow ++
          addressSummaryListRow ++
          eoriNumberExistsSummaryListRow ++
          eoriNumberSummaryListRow ++
          VatRegisteredSummaryListRow
        if (rows.nonEmpty) {
          Seq(CYASummaryList(
            messages(messages("cya.aboutImporter")),
            SummaryList(
              classes = "govuk-!-margin-bottom-9",
              rows = rows
            )
          ))
        } else {
          Seq.empty
        }
      case _ => Seq.empty
    }
  }
}
