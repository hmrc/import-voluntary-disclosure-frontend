/*
 * Copyright 2023 HM Revenue & Customs
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

package views.data.cya

import messages.cya.CYAMessages
import models.SelectedDutyTypes.{Both, Duty, Vat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import viewmodels.cya.CYASummaryList

object CheckYourAnswersPaymentData {
  val changeUrl         = "Url"
  val payingByOther     = "By Bacs, CHAPS, Faster Payments, cheque or banker’s draft."
  val payingByDeferment = "By duty deferment"
  val accountNumberDuty = "1284958"
  val accountNumberVAT  = "5293747"
  val danTypeA          = "My deferment account (A)"
  val danTypeB          = "The importer’s account and I have authority to use it (B)"
  val danTypeC          = "The importer’s account and I have standing authority to use it (C)"
  val FileExample       = "FileExample.pdf"
  val dutyFileExample   = "DutyFileExample.pdf"
  val vatFileExample    = "VATFileExample.pdf"

  val paymentMethodOtherRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.paymentMethod),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(payingByOther),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.DefermentController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changePaymentMethod)
          )
        )
      )
    )
  )

  val paymentMethodDefermentRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.paymentMethod),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(payingByDeferment),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.DefermentController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changePaymentMethod)
          )
        )
      )
    )
  )

  val splitDefermentYesRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.splitDeferment),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text("Yes"),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.SplitPaymentController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            Some(CYAMessages.changeSplitDeferment)
          )
        )
      )
    )
  )

  val splitDefermentNoRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.splitDeferment),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text("No"),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.SplitPaymentController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            Some(CYAMessages.changeSplitDeferment)
          )
        )
      )
    )
  )

  val importerAccountNumberRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.importerAccountNumber),
      classes = s"govuk-!-width-one-third"
    ),
    value = Value(
      Text(accountNumberDuty),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.ImporterDanController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changeImporterAccountNumber)
          )
        )
      )
    ),
    classes = "govuk-summary-list__row"
  )

  def paymentDetailsAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    Some(CYAMessages.paymentDetails),
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    )
  )

  val repAccountNumberRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.repAccountNumber),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(accountNumberDuty),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changeRepDanAccountNumber)
          )
        )
      )
    )
  )

  val repAccountNumberDutyRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.repAccountNumber),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(accountNumberDuty),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repDutyAccountNumber)
          )
        )
      )
    )
  )

  val accountOwnerTypeAVATRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(danTypeA),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repVATAccountOwner)
          )
        )
      )
    )
  )

  val accountOwnerTypeADutyRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(danTypeA),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repDutyAccountOwner)
          )
        )
      )
    )
  )

  val accountOwnerRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(danTypeB),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changeRepDanAccountOwner)
          )
        )
      )
    )
  )

  val accountOwnerTypeBVATRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(danTypeB),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repVATAccountOwner)
          )
        )
      )
    )
  )

  val accountOwnerTypeBDutyRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(danTypeB),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repDutyAccountOwner)
          )
        )
      )
    )
  )

  val accountOwnerTypeBRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(danTypeB),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repDutyAccountNumber)
          )
        )
      )
    )
  )

  val accountOwnerTypeCVATRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(danTypeC),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repVATAccountOwner)
          )
        )
      )
    )
  )

  val accountOwnerTypeCDutyRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(danTypeC),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repDutyAccountOwner)
          )
        )
      )
    )
  )
  val proofOfAuthority = SummaryListRow(
    key = Key(
      Text(CYAMessages.proofOfAuthority),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(dutyFileExample),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.UploadAuthorityController.onLoad(Duty).url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changeProofOfAuthority)
          )
        )
      )
    )
  )

  val proofOfAuthorityBoth = SummaryListRow(
    key = Key(
      Text(CYAMessages.proofOfAuthority),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(FileExample),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.UploadAuthorityController.onLoad(Both).url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changeProofOfAuthority)
          )
        )
      )
    )
  )

  def defermentDutyAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    Some(CYAMessages.defermentInfoDuty),
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    ),
    smallHeader = true
  )

  val repAccountNumberVATRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.repAccountNumber),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(accountNumberVAT),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad().url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.repVATAccountNumber)
          )
        )
      )
    )
  )

  val proofOfAuthorityDuty = SummaryListRow(
    key = Key(
      Text(CYAMessages.proofOfAuthority),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(dutyFileExample),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.UploadAuthorityController.onLoad(Duty).url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changeProofOfAuthorityDutyOwed)
          )
        )
      )
    )
  )

  val proofOfAuthorityVat = SummaryListRow(
    key = Key(
      Text(CYAMessages.proofOfAuthority),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(vatFileExample),
      classes = "govuk-!-width-one-half"
    ),
    actions = Some(
      Actions(items =
        Seq(
          ActionItem(
            controllers.paymentInfo.routes.UploadAuthorityController.onLoad(Vat).url,
            HtmlContent("""<span aria-hidden="true">Change</span>"""),
            visuallyHiddenText = Some(CYAMessages.changeProofOfAuthorityVatOwed)
          )
        )
      )
    )
  )

  def defermentVATAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    Some(CYAMessages.defermentInfoVAT),
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    ),
    smallHeader = true
  )

  val answers: Seq[CYASummaryList] = Seq(
    paymentDetailsAnswers(Seq(paymentMethodDefermentRow, splitDefermentYesRow)),
    defermentDutyAnswers(Seq(repAccountNumberDutyRow, accountOwnerTypeBDutyRow)),
    defermentVATAnswers(Seq(repAccountNumberVATRow, accountOwnerTypeBVATRow))
  )

}
