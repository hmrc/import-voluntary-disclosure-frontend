/*
 * Copyright 2025 HM Revenue & Customs
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
import models.importDetails.UserType
import models.requests.DataRequest
import pages.importDetails.UserTypePage
import pages.contactDetails.{DeclarantContactDetailsPage, TraderAddressPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{SummaryListHelper, cya}

trait CYAYourDetailsSummaryListHelper extends SummaryListHelper {

  def buildYourDetailsSummaryList(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val answers = request.userAnswers
    val rows = Seq(
      buildUserTypeSummaryListRow(answers),
      buildContactDetailsSummaryListRow(answers),
      buildAddressSummaryListRow(answers)
    ).flatten

    if (rows.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          Some(messages(messages("cya.yourDetails"))),
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

  private def buildUserTypeSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(UserTypePage).map { details =>
      val userTypeValue =
        if (details.equals(UserType.Importer)) messages("cya.importer") else messages("cya.representative")
      createRow(
        keyText = Text(messages("cya.userType")),
        valueContent = Text(userTypeValue),
        action = Some(
          createChangeActionItem(
            controllers.importDetails.routes.UserTypeController.onLoad().url,
            messages("cya.userType.change")
          )
        )
      )
    }

  private def buildContactDetailsSummaryListRow(
    answers: UserAnswers
  )(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DeclarantContactDetailsPage).map { details =>
      val contactDetailsValue = Seq(details.fullName, details.email, details.phoneNumber)
      createRow(
        keyText = Text(messages("cya.contactDetails")),
        valueContent = HtmlContent(encodeMultilineText(contactDetailsValue)),
        action = Some(
          createChangeActionItem(
            controllers.contactDetails.routes.DeclarantContactDetailsController.onLoad().url,
            messages("cya.contactDetails.change")
          )
        )
      )
    }

  private def buildAddressSummaryListRow(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(TraderAddressPage).map { address =>
      val addressString: Seq[String] =
        Seq(
          address.addressLine1,
          address.addressLine2.getOrElse(""),
          address.city,
          address.postalCode.getOrElse(""),
          address.countryCode
        ).filter(_.nonEmpty)
      createRow(
        keyText = Text(messages("cya.address")),
        valueContent = HtmlContent(encodeMultilineText(addressString)),
        action = Some(
          createChangeActionItem(
            controllers.contactDetails.routes.TraderAddressCorrectController.onLoad().url,
            messages("cya.address.change")
          )
        )
      )
    }

}
