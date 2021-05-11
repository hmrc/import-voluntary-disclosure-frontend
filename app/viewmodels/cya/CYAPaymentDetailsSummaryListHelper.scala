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

import models.requests.DataRequest
import pages.DefermentPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.cya

trait CYAPaymentDetailsSummaryListHelper {

  def buildPaymentDetailsSummaryList()(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] = {
    val paymentInformationSummaryListRow: Seq[SummaryListRow] = request.userAnswers.get(DefermentPage) match {
      case Some(deferment) =>
        val payingByDeferment = if (deferment) messages("site.yes") else messages("site.no")
        Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.payingByDeferment")),
              classes = "govuk-!-width-one-third"
            ),
            value = Value(
              content = Text(payingByDeferment)
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("cya.change")))
              ))
            )
          )
        )
      case None => Seq.empty
    }
    if (paymentInformationSummaryListRow.nonEmpty) {
      Seq(
        cya.CYASummaryList(
          messages(messages("cya.paymentInformation")),
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = paymentInformationSummaryListRow
          )
        )
      )
    } else {
      Seq.empty
    }
  }

}
