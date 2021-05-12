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
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import viewmodels.cya.CYASummaryList

object CheckYourAnswersPaymentData {
  val changeUrl = "Url"
  val payingByOther = "By BACS, CHAPS, Faster Payments, cheque or banker’s draft."
  val payingByDeferment = "By duty deferment"
  val accountNumberDuty = "1284958"
  val accountNumberVat = "5293747"
  val danTypeB = "The importer’s account and I have authority to use it"
  val dutyFileExample = "DutyFileExample.pdf"
  val vatFileExample = "VATFileExample.pdf"


  val paymentDetailsAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.paymentDetails,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.paymentMethod),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(payingByDeferment)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.splitDeferment),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text("Yes")
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )))
        )
      )
    )
  )

  val defermentDutyAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.defermentInfoDuty,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.repAccountNumber),
            classes = "govuk-!-width-one-third govuk-!-padding-bottom-0"
          ),
          value = Value(
            Text(accountNumberDuty),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          ),
            classes = "govuk-!-padding-bottom-0"
          )),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.accountOwner),
            classes = "govuk-!-width-one-third govuk-!-padding-top-0"
          ),
          value = Value(
            Text(danTypeB),
            classes = "govuk-!-padding-top-0"

          )
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.proofOfAuthority),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(dutyFileExample)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )
          ))
        )
      )
    )
  )

  val defermentVATAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.defermentInfoVAT,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.repAccountNumber),
            classes = "govuk-!-width-one-third govuk-!-padding-bottom-0"
          ),
          value = Value(
            Text(accountNumberVat),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          ),
            classes = "govuk-!-padding-bottom-0"
          )),
          classes = "govuk-summary-list__row--no-border"
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.accountOwner),
            classes = "govuk-!-width-one-third govuk-!-padding-top-0"
          ),
          value = Value(
            Text(danTypeB),
            classes = "govuk-!-padding-top-0"

          )
        ),
        SummaryListRow(
          key = Key(
            Text(CYAMessages.proofOfAuthority),
            classes = "govuk-!-width-one-third"
          ),
          value = Value(
            Text(vatFileExample)
          ),
          actions = Some(Actions(items = Seq(
            ActionItem(changeUrl,
              Text(CYAMessages.change))
          )
          ))
        )
      )
    )
  )


  val answers: Seq[CYASummaryList] = Seq(

    paymentDetailsAnswers,
    defermentDutyAnswers,
    defermentVATAnswers
  )

}
