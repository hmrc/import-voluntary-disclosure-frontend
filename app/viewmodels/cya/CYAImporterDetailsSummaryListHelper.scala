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
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya

trait CYAImporterDetailsSummaryListHelper {

  def buildImporterDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val importerNameSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(ImporterNamePage) match {
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
    val addressSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(ImporterAddressPage) match {
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
    val eoriNumberExistsSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(ImporterEORIExistsPage) match {
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
    val eoriNumberSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(ImporterEORINumberPage) match {
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
    val VatRegisteredSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(ImporterVatRegisteredPage) match {
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

    if (request.isRepFlow) {
      val rows = importerNameSummaryListRow ++
        addressSummaryListRow ++
        eoriNumberExistsSummaryListRow ++
        eoriNumberSummaryListRow ++
        VatRegisteredSummaryListRow
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
}
