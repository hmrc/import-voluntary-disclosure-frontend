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

import models.UserType
import models.requests.DataRequest
import pages.{DeclarantContactDetailsPage, TraderAddressPage, UserTypePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{ActionItemHelper, cya}

//noinspection ScalaStyle
trait CYAYourDetailsSummaryListHelper {

  def buildYourDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val userTypeSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(UserTypePage) match {
      case Some(details) =>
        val userTypeValue = if (details.equals(UserType.Importer)) messages("cya.importer") else messages("cya.representative")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.userType")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
            ),
            value = Value(
              content = HtmlContent(userTypeValue),
              classes = "govuk-!-padding-top-0"
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("cya.change")))
              ),
              classes = "govuk-!-padding-bottom-0")
            ),
            classes = "govuk-!-padding-top-0"
          )
        )
      case None => Seq.empty
    }

    val contactDetailsSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(DeclarantContactDetailsPage) match {
      case Some(details) =>
        val contactDetailsVaalue = {
          details.fullName + "<br/>" +
            details.email + "<br/>" +
            details.phoneNumber
        }
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.contactDetails")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
            ),
            value = Value(
              content = HtmlContent(contactDetailsVaalue),
              classes = "govuk-!-padding-top-0"
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItemHelper.createChangeActionItem(
                  controllers.routes.DeclarantContactDetailsController.onLoad().url,
                  messages("cya.contactDetails.change")
                )
              )
              )
            )
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

    val rows = userTypeSummaryListRow ++ contactDetailsSummaryListRow ++ addressSummaryListRow
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
