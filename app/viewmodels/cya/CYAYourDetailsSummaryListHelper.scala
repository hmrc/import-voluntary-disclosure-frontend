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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{ActionItemHelper, cya}

trait CYAYourDetailsSummaryListHelper {

  private def encodeMultilineText(content: Seq[String]): String = content.map(line => HtmlFormat.escape(line)).mkString("<br/>")

  def buildYourDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val userTypeSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(UserTypePage) match {
      case Some(details) =>
        val userTypeValue = if (details.equals(UserType.Importer)) messages("cya.importer") else messages("cya.representative")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.userType")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = Text(userTypeValue)
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("cya.change")))
              )
              ))
          )
        )
      case None => Seq.empty
    }

    val contactDetailsSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(DeclarantContactDetailsPage) match {
      case Some(details) =>
        val contactDetailsValue = Seq(details.fullName, details.email, details.phoneNumber)
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.contactDetails")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = HtmlContent(encodeMultilineText(contactDetailsValue))
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
        val addressString: Seq[String] = address.postalCode match {
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
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.address")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = HtmlContent(encodeMultilineText(addressString))
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
