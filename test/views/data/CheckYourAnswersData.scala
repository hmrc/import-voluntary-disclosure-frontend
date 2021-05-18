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
import models.{ContactAddress, ContactDetails}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
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
  val traderAddress = ContactAddress("21 Street", Some("Mayfair"), "London", Some("SN6PY"), "UK")
  val importerAddress = ContactAddress("21 Street", Some("Mayfair"), "London", None, "UK")
  val numberOfEntries = "One Entry"
  val epu = "123"
  val entryNumber = "123456Q"
  val entryDate = "01 December 2020"
  val yes = "Yes"
  val acceptanceDate = "Before 1 January 2021"
  val eoriNumber = "GB345834921000"
  val amount = "£1.00"
  val reason = "1 reason given"
  val extraInformation = "Stock losses in warehouse."
  val userType = "Representative"
  val contactDetails = ContactDetails("First Second", "email@email.com", "1234567890")

  val importerDetailsAnswers: CYASummaryList = viewmodels.cya.CYASummaryList(
    CYAMessages.aboutImporterDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.name),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(fullName)
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  controllers.routes.ImporterNameController.onLoad().url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some(CYAMessages.changeImporterName)
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.address),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            HtmlContent(buildAddress(importerAddress))
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  controllers.routes.AddressLookupController.initialiseImporterJourney().url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some(CYAMessages.changeImporterAddress)
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.eoriNumberExists),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(yes)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.eoriNumber),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(eoriNumber)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.vatRegistered),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(yes)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
      )
    )
  )

  val underpaymentDetailsAnswers: CYASummaryList = viewmodels.cya.CYASummaryList(
    CYAMessages.underpaymentDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.totalOwed),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(amount)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad().url,
              HtmlContent("""<span aria-hidden="true">View summary</span>"""),
              Some(CYAMessages.viewSummaryChange)
            )
          )
          )),
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.reasonForUnderpayment),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(reason)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.viewSummary))
          )
          )),
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.tellUsAnythingElse),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(yes)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change))
          )
          ))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.extraInformation),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(extraInformation)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change))
          )
          ))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.filesUploaded(1)),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            HtmlContent(file)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              controllers.routes.UploadAnotherFileController.onLoad().url,
              HtmlContent("""<span aria-hidden="true">Change</span>"""),
              Some(CYAMessages.changeSupportingDocuments)
            )
          ))
          )
        )
      )
    ))

  val yourDetailsAnswers: CYASummaryList = viewmodels.cya.CYASummaryList(
    CYAMessages.yourDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.userType),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(userType),
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              changeUrl,
              Text(CYAMessages.change))),
          )),
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.contactDetails),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            HtmlContent(buildContactDetails(contactDetails)),
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  controllers.routes.DeclarantContactDetailsController.onLoad().url,
                  HtmlContent("""<span aria-hidden="true">Change</span>"""),
                  Some(CYAMessages.changeContactDetails)
                )
              )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.address),
            classes = "govuk-!-width-one-third"
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


  val entryDetailsAnswers: CYASummaryList = viewmodels.cya.CYASummaryList(
    CYAMessages.entryDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.numberOfEntries),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(numberOfEntries)
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
            classes = "govuk-!-width-one-third govuk-!-padding-bottom-0"
          ),
          value = Value(
            Text(epu),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              controllers.routes.EntryDetailsController.onLoad().url,
              HtmlContent("<span aria-hidden=\"true\">Change</span>"),
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
            classes = "govuk-!-width-one-third govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          value = Value(
            Text(entryNumber),
            classes = "govuk-!-padding-top-0 govuk-!-padding-bottom-0"
          ),
          actions = None,
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.entryDate),
            classes = "govuk-!-width-one-third govuk-!-padding-top-0"
          ),
          value = Value(
            Text(entryDate),
            classes = "govuk-!-padding-top-0"
          ),
          actions = None
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.acceptanceDate),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(acceptanceDate)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(
              controllers.routes.AcceptanceDateController.onLoad().url,
              HtmlContent("""<span aria-hidden="true">Change</span>"""),
              visuallyHiddenText = Some(CYAMessages.changeAcceptanceDate)
            )
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.oneCustomsProcedureCode),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(yes)
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
            Text(CYAMessages.cpc),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(cpc)
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


  val answers: Seq[CYASummaryList] = Seq(
    importerDetailsAnswers,
    entryDetailsAnswers,
    underpaymentDetailsAnswers,
    yourDetailsAnswers
  )

  def buildAddress(address: ContactAddress) =
    address.postalCode match {
      case Some(postcode) =>
        val street = if (address.addressLine2.isDefined) {
          address.addressLine1 + "<br/>" + address.addressLine2.get + "<br/>"
        } else {
          address.addressLine1 + "<br/>"
        }
        street +
          address.city + "<br/>" +
          postcode + "<br/>" +
          address.countryCode
      case None =>
        val street = if (address.addressLine2.isDefined) {
          address.addressLine1 + "<br/>" + address.addressLine2.get + "<br/>"
        } else {
          address.addressLine1 + "<br/>"
        }
        street +
          address.city + "<br/>" +
          address.countryCode
    }

  def buildContactDetails(contactDetails: ContactDetails): String = {
    contactDetails.fullName + "<br/>" +
    contactDetails.email + "<br/>" +
    contactDetails.phoneNumber
  }
}
