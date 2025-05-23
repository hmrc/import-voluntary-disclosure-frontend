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

package views.reasons

import base.ViewBaseSpec
import forms.reasons.UnderpaymentReasonSummaryFormProvider
import messages.BaseMessages
import messages.reasons.ReasonSummary
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import views.data.reasons.UnderpaymentReasonSummaryData
import views.html.reasons.UnderpaymentReasonSummaryView

class UnderpaymentReasonSummaryViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnderpaymentReasonSummaryView = app.injector.instanceOf[UnderpaymentReasonSummaryView]

  val formProvider: UnderpaymentReasonSummaryFormProvider = injector.instanceOf[UnderpaymentReasonSummaryFormProvider]

  val summaryList: Option[SummaryList] = UnderpaymentReasonSummaryData.singleItemSummaryList

  "Rendering the Reason summary page" when {
    "no errors exist" should {

      val form: Form[Boolean]              = formProvider.apply()
      lazy val view: Html                  = injectedView(form, summaryList)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ReasonSummary.pageTitle)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean]         = formProvider().bind(Map("value" -> ""))
      lazy val view: Html                  = injectedView(form, summaryList)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ReasonSummary.errorPrefix + ReasonSummary.pageTitle)

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe ReasonSummary.thereIsAProblemPrefix + ReasonSummary.errorRequired
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe ReasonSummary.errorPrefix + ReasonSummary.errorRequired
      }

    }
  }

  it should {

    val form: Form[Boolean]              = formProvider.apply()
    lazy val view: Html                  = injectedView(form, summaryList)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct value for the first radio button of '${ReasonSummary.siteYes}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(1)"
      ) mustBe ReasonSummary.siteYes
    }

    s"have the correct value for the second radio button of '${ReasonSummary.siteNo}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(2)"
      ) mustBe ReasonSummary.siteNo
    }

    s"have the correct radio message'" in {
      elementText("#main-content > div > div > form > div > fieldset > legend") mustBe ReasonSummary.radioMessage
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }

}
