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

package views.data

import messages.CYAMessages
import models.TraderAddress
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.CYASummaryList
import views.ViewUtils.displayMoney

object CheckYourAnswersData {
  val changeUrl = "url"
  val cdUnderpayment: BigDecimal = BigDecimal(123.33)
  val ivUnderpayment: BigDecimal = BigDecimal(54321.99)
  val edUnderpayment: BigDecimal = BigDecimal(999.00)
  val cpc = "4000C09"
  val file = "Example.pdf"
  val fullName = "First Second"
  val email = "email@email.com"
  val phone = "1234567890"
  val traderAddress = TraderAddress("21 Street", "London", Some("SN6PY"), "UK")

  val underpaymentAnswers: CYASummaryList = CYASummaryList(
    CYAMessages.underpaymentDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.customsDuty),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(displayMoney(cdUnderpayment))
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.importVAT),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(displayMoney(ivUnderpayment))
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.exciseDuty),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(displayMoney(edUnderpayment))
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        )
      )
    )
  )

  val amendmentDetailsAnswers: CYASummaryList = CYASummaryList(
    CYAMessages.amendmentDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.cpc),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(cpc)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.cpcChanged),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent("No")
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
//        SummaryListRow(
//          key = Key(
//            Text(CYAMessages.numAmendments),
//            classes = "govuk-!-width-two-thirds"
//          ),
//          value = Value(
//            HtmlContent("5")
//          ),
//          actions = Some(Actions(items = Seq(
//            ActionItem(changeUrl,
//              Text(CYAMessages.change))
//          )))
//        )
      )
    )
  )

  val uploadFilesAnswers: CYASummaryList = CYASummaryList(
    CYAMessages.supportingDocuments,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.filesUploaded(1)),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent(file)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change)
            )
          )))
        )
      )
    )
  )

  val yourDetailsAnswers: CYASummaryList = CYASummaryList(
    CYAMessages.yourDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.name),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            HtmlContent(fullName)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change)
            )
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.email),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            HtmlContent(email)
          ),
          actions = None
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.phone),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent(phone)
          ),
          actions = None
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.address),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent(buildAddress(traderAddress))
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change)
            )
          )))
        )
      )
    )
  )

  val answers: Seq[CYASummaryList] = Seq(underpaymentAnswers, amendmentDetailsAnswers, uploadFilesAnswers, yourDetailsAnswers)

  def buildAddress(address: TraderAddress) =
    address.postalCode match {
      case Some(value) => address.streetAndNumber + "<br/>" +
        address.city + "<br/>" +
        address.postalCode.get + "<br/>" +
        address.countryCode
      case None => address.streetAndNumber + "<br/>" +
        address.city + "<br/>" +
        address.countryCode
    }
}
