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

package views.underpayments

import base.ViewBaseSpec
import forms.underpayments.UnderpaymentDetailSummaryFormProvider
import messages.BaseMessages
import messages.underpayments.UnderpaymentDetailSummaryMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import views.data.underpayments.UnderpaymentDetailSummaryData
import views.html.underpayments.UnderpaymentDetailSummaryView

class UnderpaymentDetailSummaryViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnderpaymentDetailSummaryView = app.injector.instanceOf[UnderpaymentDetailSummaryView]

  val formProvider: UnderpaymentDetailSummaryFormProvider = injector.instanceOf[UnderpaymentDetailSummaryFormProvider]

  val summaryList: SummaryList = UnderpaymentDetailSummaryData.summaryList

  val amountOwedSummaryList: SummaryList = UnderpaymentDetailSummaryData.amountOwedSummaryList


  "Rendering the Underpayment Summary page" when {

    "no errors exist single entry" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, summaryList, amountOwedSummaryList, 1, true)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UnderpaymentDetailSummaryMessages.pageTitle)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have the correct radio hint of '${UnderpaymentDetailSummaryMessages.radioMessageHintSingleEntry}'" in {
        elementText("#value-hint") mustBe UnderpaymentDetailSummaryMessages.radioMessageHintSingleEntry
      }

    }

    "no errors exist bulk entry" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, summaryList, amountOwedSummaryList, 1, false)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UnderpaymentDetailSummaryMessages.pageTitle)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have the correct radio hint of '${UnderpaymentDetailSummaryMessages.radioMessageHintSingleEntry}'" in {
        elementText("#value-hint") mustBe UnderpaymentDetailSummaryMessages.radioMessageHintBulkEntry
      }

    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form, summaryList, amountOwedSummaryList, 1, true)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UnderpaymentDetailSummaryMessages.errorPrefix + UnderpaymentDetailSummaryMessages.pageTitle)

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe UnderpaymentDetailSummaryMessages.errorRequired
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe UnderpaymentDetailSummaryMessages.errorPrefix + UnderpaymentDetailSummaryMessages.errorRequired
      }

    }

    "there is less than 10 Underpayments in the Summary List" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, summaryList, amountOwedSummaryList, 1, true)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct radio header of '${UnderpaymentDetailSummaryMessages.radioMessage}'" in {
        elementText("#main-content > div > div > form > div > fieldset > legend") mustBe UnderpaymentDetailSummaryMessages.radioMessage
      }

      s"have the correct value for the first radio button of '${UnderpaymentDetailSummaryMessages.siteYes}'" in {
        elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe UnderpaymentDetailSummaryMessages.siteYes
      }

      s"have the correct value for the second radio button of '${UnderpaymentDetailSummaryMessages.siteNo}'" in {
        elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe UnderpaymentDetailSummaryMessages.siteNo
      }

    }

    "there is 10 Underpayments in the Summary List" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, summaryList, amountOwedSummaryList, 10, true)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct message of '${UnderpaymentDetailSummaryMessages.fullList}'" in {
        elementText("#main-content > div > div > form > p") mustBe UnderpaymentDetailSummaryMessages.fullList
      }

    }

  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(form, summaryList, amountOwedSummaryList, 1, true)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${UnderpaymentDetailSummaryMessages.pageHeader}'" in {
      elementText("h1") mustBe UnderpaymentDetailSummaryMessages.pageHeader
    }

    s"have the correct h2 of '${UnderpaymentDetailSummaryMessages.pageHeaderSmall}'" in {
      elementText("h2") mustBe UnderpaymentDetailSummaryMessages.pageHeaderSmall
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }

}
