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
import pages.{DeclarantContactDetailsPage, TraderAddressPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{ActionItemHelper, cya}

trait CYAYourDetailsSummaryListHelper {

  def buildYourDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val detailsSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(DeclarantContactDetailsPage) match {
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
                ActionItemHelper.createChangeActionItem(
                  controllers.routes.DeclarantContactDetailsController.onLoad().url,
                  messages("cya.contactDetails.change")
                )
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

    val addressSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(TraderAddressPage) match {
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
        cya.CYASummaryList(
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

}
