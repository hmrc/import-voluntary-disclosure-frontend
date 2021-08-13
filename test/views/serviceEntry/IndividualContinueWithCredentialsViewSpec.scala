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

package views.serviceEntry

import base.ViewBaseSpec
import forms.serviceEntry.IndividualContinueWithCredentialsFormProvider
import messages.serviceEntry.IndividualContinueWithCredentialsMessages._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.serviceEntry.IndividualContinueWithCredentialsView

class IndividualContinueWithCredentialsViewSpec extends ViewBaseSpec {

  private lazy val injectedView: IndividualContinueWithCredentialsView = app.injector.instanceOf[IndividualContinueWithCredentialsView]

  val formProvider: IndividualContinueWithCredentialsFormProvider = injector.instanceOf[IndividualContinueWithCredentialsFormProvider]

  lazy val backLink: Option[Call] = Some(Call("GET", "url"))

  "Rendering the Individual Continue With Credentials page" when {

    "no errors exist VAT only" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(errorPrefix + title)

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe errorRequired
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe errorPrefix + errorRequired
      }
    }

  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(form, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct page heading" in {
      elementText("h1") mustBe heading
    }

    s"have the correct value for the first radio button of '${yesMessage}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label") mustBe yesMessage
    }

    s"have the correct value for the second radio button of '${noMessage}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label") mustBe noMessage
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }

}
