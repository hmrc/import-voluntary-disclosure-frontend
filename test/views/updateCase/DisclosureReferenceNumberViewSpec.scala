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

package views.updateCase

import base.ViewBaseSpec
import forms.updateCase.DisclosureReferenceNumberFormProvider
import messages.DisclosureReferenceNumberMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.updateCase.DisclosureReferenceNumberView

class DisclosureReferenceNumberViewSpec extends ViewBaseSpec {

  private lazy val injectedView: DisclosureReferenceNumberView = app.injector.instanceOf[DisclosureReferenceNumberView]

  val formProvider: DisclosureReferenceNumberFormProvider = injector.instanceOf[DisclosureReferenceNumberFormProvider]

  private val backlink: Call =
    Call("GET", controllers.updateCase.routes.DisclosureReferenceNumberController.onLoad().url)

  "Rendering the Disclosure Reference Number page" when {
    "no errors exist" should {

      val form: Form[String]               = formProvider.apply()
      lazy val view: Html                  = injectedView(form, backlink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(DisclosureReferenceNumberMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "no data supplied" should {

      "an error exists" should {
        lazy val form: Form[String]          = formProvider().bind(Map("value" -> ""))
        lazy val view: Html                  = injectedView(form, backlink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(DisclosureReferenceNumberMessages.errorPrefix + DisclosureReferenceNumberMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe DisclosureReferenceNumberMessages.requiredError
        }

        "render an error message against the field" in {
          elementText(
            "#value-error"
          ) mustBe DisclosureReferenceNumberMessages.errorPrefix + DisclosureReferenceNumberMessages.requiredError
        }
      }
    }

    "data in invalid format supplied" should {

      "an error exists" should {
        lazy val form: Form[String]          = formProvider().bind(Map("value" -> "invalid"))
        lazy val view: Html                  = injectedView(form, backlink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(DisclosureReferenceNumberMessages.errorPrefix + DisclosureReferenceNumberMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe DisclosureReferenceNumberMessages.formatError
        }

        "render an error message against the field" in {
          elementText(
            "#value-error"
          ) mustBe DisclosureReferenceNumberMessages.errorPrefix + DisclosureReferenceNumberMessages.formatError
        }
      }
    }
  }

  it should {

    val form: Form[String]               = formProvider.apply()
    lazy val view: Html                  = injectedView(form, backlink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${DisclosureReferenceNumberMessages.title}'" in {
      elementText("h1") mustBe DisclosureReferenceNumberMessages.title
    }

    s"have the correct hint" in {
      elementText("#value-hint") mustBe DisclosureReferenceNumberMessages.hint
    }
    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain(
        "href" -> controllers.updateCase.routes.DisclosureReferenceNumberController.onLoad().url
      )
    }

    s"render the input field" in {
      document.select("#value").size mustBe 1
    }

    s"render the details block" in {
      document.select("#main-content > div > div > form > details > summary > span").size mustBe 1
    }

    s"render the details block text" in {
      elementText("#main-content > div > div > form > details > div") mustBe DisclosureReferenceNumberMessages.details
    }

    s"render the mailto link" in {
      element("#main-content .govuk-link").attr("href") mustBe DisclosureReferenceNumberMessages.mailLink
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe DisclosureReferenceNumberMessages.continue
    }
  }
}
