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
import models.ContactAddress
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import viewmodels.cya.CYASummaryList

object CheckYourAnswersData {
  val changeUrl = "Url"
  val cdUnderpayment: BigDecimal = BigDecimal(5000)
  val ivUnderpayment: BigDecimal = BigDecimal(900)
  val edUnderpayment: BigDecimal = BigDecimal(140)
  val cpc = "4000C09"
  val file = "Example.pdf"
  val fullName = "First Second"
  val email = "email@email.com"
  val phone = "1234567890"
  val traderAddress = ContactAddress("21 Street", None, "London", Some("SN6PY"), "UK")
  val numberOfEntries = "One"
  val epu = "123"
  val entryNumber = "123456Q"
  val entryDate = "01 December 2020"
  val yes = "Yes"
  val eoriNumber = "GB345834921000"

  val importerDetailsAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.aboutImporterDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.name),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(fullName)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.address),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(buildAddress(traderAddress))
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.eoriNumberExists),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(yes)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.eoriNumber),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(eoriNumber)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.vatRegistered),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(yes)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
      )
    )
  )

  val underpaymentDetailsAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.supportingDocuments,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.filesUploaded(1)),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(file)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change))
          )))
        )
      )
    )
  )

  val yourDetailsAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.yourDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.name),
            classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
          ),
          value = Value(
            HtmlContent(fullName),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change))),
            classes = "govuk-!-padding-bottom-0")),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.email),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            HtmlContent(email),
            classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          actions = None,
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.phone),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent(phone),
            classes = "govuk-!-padding-top-0"
          ),
          actions = None,
          classes = "govuk-!-padding-top-0"
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.address),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(buildAddress(traderAddress))
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change)))
          )))
      )
    )
  )


  val entryDetailsAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.disclosureDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.numberOfEntries),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(numberOfEntries)
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
            Text(CYAMessages.epu),
            classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
          ),
          value = Value(
            HtmlContent(epu),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              controllers.routes.EntryDetailsController.onLoad().url,
              Text(CYAMessages.change),
              Some(CYAMessages.epuChange)
            )
          ),
            classes = "govuk-!-padding-bottom-0")
          ),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.entryNumber),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            HtmlContent(entryNumber),
            classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          actions = None,
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.entryDate),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            HtmlContent(entryDate),
            classes = "govuk-!-padding-top-0"
          ),
          actions = None
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.acceptanceDate),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent(yes)
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

  val paymentDetailsAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.paymentInformation,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.payingByDeferment),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            HtmlContent("No")
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        )
      )
    )
  )

  val answers: Seq[CYASummaryList] = Seq(
    importerDetailsAnswers,
    entryDetailsAnswers,
    underpaymentDetailsAnswers,
    yourDetailsAnswers,
    paymentDetailsAnswers
  )

  def buildAddress(address: ContactAddress) =
    address.postalCode match {
      case Some(value) => address.addressLine1 + "<br/>" +
        address.city + "<br/>" +
        address.postalCode.get + "<br/>" +
        address.countryCode
      case None => address.addressLine1 + "<br/>" +
        address.city + "<br/>" +
        address.countryCode
    }
}
