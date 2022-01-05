/*
 * Copyright 2022 HM Revenue & Customs
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

package views.importDetails

import base.ViewBaseSpec
import forms.importDetails.AcceptanceDateFormProvider
import messages.BaseMessages
import messages.importDetails.AcceptanceDateBulkMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.importDetails.AcceptanceDateBulkView

class AcceptanceDateBulkViewSpec extends ViewBaseSpec with BaseMessages {

  lazy val backLink = Call("GET", "url")

  private lazy val injectedView: AcceptanceDateBulkView = app.injector.instanceOf[AcceptanceDateBulkView]

  val formProvider: AcceptanceDateFormProvider = injector.instanceOf[AcceptanceDateFormProvider]

  "Rendering the AcceptanceDateBulk page" when {
    "no errors exist" should {

      val form: Form[Boolean]              = formProvider.apply(false)
      lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(AcceptanceDateBulkMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean]         = formProvider(false).bind(Map("value" -> ""))
      lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(AcceptanceDateBulkMessages.errorPrefix + AcceptanceDateBulkMessages.title)

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe AcceptanceDateBulkMessages.requiredError
      }

      "render an error message against the field" in {
        elementText(
          "#value-error"
        ) mustBe AcceptanceDateBulkMessages.errorPrefix + AcceptanceDateBulkMessages.requiredError
      }

    }
  }

  it should {

    val form: Form[Boolean]              = formProvider.apply()
    lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct page heading" in {
      elementText("h1") mustBe AcceptanceDateBulkMessages.title
    }

    "have the correct page info" in {
      elementText("p:nth-child(2)") mustBe AcceptanceDateBulkMessages.info
    }

    "have the correct value for the first radio button" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(1)"
      ) mustBe AcceptanceDateBulkMessages.beforeRadio
    }

    "have the correct value for the second radio button" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(2)"
      ) mustBe AcceptanceDateBulkMessages.afterRadio
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
