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

package views.data.cya

import messages.CYAMessages
import models.SelectedDutyTypes.{Both, Duty, Vat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import viewmodels.cya.CYASummaryList

object CheckYourAnswersPaymentData {
  val changeUrl = "Url"
  val payingByOther = "By BACS, CHAPS, Faster Payments, cheque or banker’s draft."
  val payingByDeferment = "By duty deferment"
  val accountNumberDuty = "1284958"
  val accountNumberVAT = "5293747"
  val danTypeA = "My deferment account"
  val danTypeB = "The importer’s account and I have authority to use it"
  val danTypeC = "The importer’s account and I have standing authority to use it"
  val FileExample = "FileExample.pdf"
  val dutyFileExample = "DutyFileExample.pdf"
  val vatFileExample = "VATFileExample.pdf"


  val paymentMethodOtherRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.paymentMethod),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(payingByOther)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.DefermentController.onLoad().url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changePaymentMethod)
      )
    )
    ))
  )

  val paymentMethodDefermentRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.paymentMethod),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(payingByDeferment)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.DefermentController.onLoad().url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changePaymentMethod)
      )
    )
    ))
  )

  val splitDefermentYesRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.splitDeferment),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text("Yes")
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.SplitPaymentController.onLoad().url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        Some(CYAMessages.changeSplitDeferment)
      )
    )))
  )

  val splitDefermentNoRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.splitDeferment),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text("No")
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.SplitPaymentController.onLoad().url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        Some(CYAMessages.changeSplitDeferment)
      )
    )))
  )

  val importerAccountNumberRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.importerAccountNumber),
      classes = s"govuk-!-width-one-third"
    ),
    value = Value(
      Text(accountNumberDuty)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.ImporterDanController.onLoad().url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changeImporterAccountNumber)
      )
    )
    )),
    classes = "govuk-summary-list__row"
  )

  def paymentDetailsAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    CYAMessages.paymentDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    )
  )

  val repAccountNumberRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.repAccountNumber),
      classes = "govuk-!-width-one-third govuk-!-padding-bottom-1"
    ),
    value = Value(
      Text(accountNumberDuty),
      classes = "govuk-!-padding-bottom-1"
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.RepresentativeDanController.onLoad().url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changeRepDan)
      )
    ),
      classes = "govuk-!-padding-bottom-1"
    )),
    classes = "govuk-summary-list__row--no-border"
  )

  val repAccountNumberDutyRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.repAccountNumber),
      classes = "govuk-!-width-one-third govuk-!-padding-bottom-1"
    ),
    value = Value(
      Text(accountNumberDuty),
      classes = "govuk-!-padding-bottom-1"
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.RepresentativeDanDutyController.onLoad().url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changeRepDutyAccountNumber)
      )
    ),
      classes = "govuk-!-padding-bottom-1"
    )),
    classes = "govuk-summary-list__row--no-border"
  )

  val accountOwnerTypeARow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third govuk-!-padding-top-0"
    ),
    value = Value(
      Text(danTypeA),
      classes = "govuk-!-padding-top-0"
    )
  )

  val accountOwnerTypeBRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third govuk-!-padding-top-0"
    ),
    value = Value(
      Text(danTypeB),
      classes = "govuk-!-padding-top-0"
    )
  )

  val accountOwnerTypeCRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.accountOwner),
      classes = "govuk-!-width-one-third govuk-!-padding-top-0"
    ),
    value = Value(
      Text(danTypeC),
      classes = "govuk-!-padding-top-0"
    )
  )

  val proofOfAuthority = SummaryListRow(
    key = Key(
      Text(CYAMessages.proofOfAuthority),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(dutyFileExample)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.UploadAuthorityController.onLoad(Duty, accountNumberDuty).url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changeProofOfAuthority)
      )
    )
    ))
  )

  val proofOfAuthorityBoth = SummaryListRow(
    key = Key(
      Text(CYAMessages.proofOfAuthority),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(FileExample)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.UploadAuthorityController.onLoad(Both, accountNumberDuty).url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changeProofOfAuthority)
      )
    )
    ))
  )

  def defermentDutyAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    CYAMessages.defermentInfoDuty,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    )
  )

  val repAccountNumberVATRow = SummaryListRow(
    key = Key(
      Text(CYAMessages.repAccountNumber),
      classes = "govuk-!-width-one-third govuk-!-padding-bottom-1"
    ),
    value = Value(
      Text(accountNumberVAT),
      classes = "govuk-!-padding-bottom-1"
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.RepresentativeDanImportVATController.onLoad().url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changeRepVATAccountNumber)
      )
    ),
      classes = "govuk-!-padding-bottom-1"
    )),
    classes = "govuk-summary-list__row--no-border"
  )

  val proofOfAuthorityDuty = SummaryListRow(
    key = Key(
      Text(CYAMessages.proofOfAuthority),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(dutyFileExample)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.UploadAuthorityController.onLoad(Duty, accountNumberDuty).url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changeProofOfAuthorityDutyOwed)
      )
    )
    ))
  )

  val proofOfAuthorityVat = SummaryListRow(
    key = Key(
      Text(CYAMessages.proofOfAuthority),
      classes = "govuk-!-width-one-third"
    ),
    value = Value(
      Text(vatFileExample)
    ),
    actions = Some(Actions(items = Seq(
      ActionItem(
        controllers.routes.UploadAuthorityController.onLoad(Vat, accountNumberVAT).url,
        HtmlContent("""<span aria-hidden="true">Change</span>"""),
        visuallyHiddenText = Some(CYAMessages.changeProofOfAuthorityVatOwed)
      )
    )
    ))
  )

  def defermentVATAnswers(rows: Seq[SummaryListRow]): CYASummaryList = cya.CYASummaryList(
    CYAMessages.defermentInfoVAT,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = rows
    )
  )

  val answers: Seq[CYASummaryList] = Seq(

    paymentDetailsAnswers(Seq(paymentMethodDefermentRow, splitDefermentYesRow)),
    defermentDutyAnswers(Seq(repAccountNumberDutyRow, accountOwnerTypeBRow)),
    defermentVATAnswers(Seq(repAccountNumberVATRow, accountOwnerTypeBRow))
  )

}
