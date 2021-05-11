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
import models.ContactDetails
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya
import viewmodels.cya.CYASummaryList

object CheckYourAnswersPaymentData {
  val changeUrl = "Url"
  val contactDetails = ContactDetails("First Second", "email@email.com", "1234567890")


  val paymentDetailsAnswers: CYASummaryList = cya.CYASummaryList(
    CYAMessages.paymentInformation,
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = Seq(
        SummaryListRow(
          key = Key(
            Text(CYAMessages.payingByDeferment),
            classes = "govuk-!-width-one-third"
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

    paymentDetailsAnswers
  )

}
