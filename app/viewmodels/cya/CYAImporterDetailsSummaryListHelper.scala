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

import models.UserAnswers
import models.requests.DataRequest
import pages._
import pages.importDetails.ImporterNamePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{ActionItemHelper, cya}
import viewmodels.cya.CYAHelper._

trait CYAImporterDetailsSummaryListHelper {

  def buildImporterDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    if (request.isRepFlow) {
      val answers = request.userAnswers
      val rows = Seq(
        buildImporterNameSummaryListRow(answers),
        buildAddressSummaryListRow(answers),
        buildEoriNumberExistsSummaryListRow(answers),
        buildEoriNumberSummaryListRow(answers),
        buildVatRegisteredSummaryListRow(answers)
      ).flatten

      if (rows.nonEmpty) {
        Seq(cya.CYASummaryList(
          messages(messages("cya.aboutImporter")),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        ))
      } else {
        Seq.empty
      }
    } else {
      Seq.empty
    }
  }

  private def buildImporterNameSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(ImporterNamePage).map { importerName =>
      createRow(
        keyText = Text(messages("cya.name")),
        valueContent = Text(importerName),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.importDetails.routes.ImporterNameController.onLoad().url,
          messages("cya.importerName.change")
        ))
      )
    }

  private def buildAddressSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(ImporterAddressPage).map { address =>
      val addressParts: Seq[String] = address.postalCode match {
        case Some(postcode) =>
          val street = if (address.addressLine2.isDefined) {
            Seq(address.addressLine1, address.addressLine2.get)
          } else {
            Seq(address.addressLine1)
          }
          street ++ Seq(address.city, postcode, address.countryCode)
        case None =>
          val street = if (address.addressLine2.isDefined) {
            Seq(address.addressLine1, address.addressLine2.get)
          } else {
            Seq(address.addressLine1)
          }
          street ++ Seq(address.city, address.countryCode)
      }
      createRow(
        keyText = Text(messages("cya.address")),
        valueContent = HtmlContent(encodeMultilineText(addressParts)),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.AddressLookupController.initialiseImporterJourney().url,
          messages("cya.importerAddress.change")
        ))
      )
    }

  private def buildEoriNumberExistsSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(ImporterEORIExistsPage).map { eoriExists =>
      val eoriNumberExists = if (eoriExists) messages("site.yes") else messages("site.no")
      createRow(
        keyText = Text(messages("cya.eoriNumberExists")),
        valueContent = Text(eoriNumberExists),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.ImporterEORIExistsController.onLoad().url,
          messages("cya.eoriExists.change")
        ))
      )
    }

  private def buildEoriNumberSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(ImporterEORINumberPage).map { eoriNumber =>
      createRow(
        keyText = Text(messages("cya.eoriNumber")),
        valueContent = Text(eoriNumber),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.ImporterEORINumberController.onLoad().url,
          messages("cya.eoriNumber.change")
        ))
      )
    }

  private def buildVatRegisteredSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(ImporterVatRegisteredPage).map { registered =>
      val isVatRegistered = if (registered) messages("site.yes") else messages("site.no")
      createRow(
        keyText = Text(messages("cya.vatRegistered")),
        valueContent = Text(isVatRegistered),
        action = Some(ActionItemHelper.createChangeActionItem(
          controllers.routes.ImporterVatRegisteredController.onLoad().url,
          messages("cya.vatRegistered.change")
        ))
      )
    }

}
