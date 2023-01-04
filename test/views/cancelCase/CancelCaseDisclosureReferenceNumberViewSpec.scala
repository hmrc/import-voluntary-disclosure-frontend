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

package views.cancelCase

import base.ViewBaseSpec
import forms.cancelCase.CancelCaseDisclosureReferenceNumberFormProvider
import messages.BaseMessages
import messages.cancelCase.CancelCaseDisclosureReferenceNumberMessages._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.cancelCase.CancelCaseDisclosureReferenceNumberView

class CancelCaseDisclosureReferenceNumberViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: CancelCaseDisclosureReferenceNumberView =
    app.injector.instanceOf[CancelCaseDisclosureReferenceNumberView]

  val formProvider: CancelCaseDisclosureReferenceNumberFormProvider =
    injector.instanceOf[CancelCaseDisclosureReferenceNumberFormProvider]

  private val backlink: Call =
    Call("GET", controllers.cancelCase.routes.CancelCaseReferenceNumberController.onLoad().url)

  "Rendering the Cancel Case Disclosure Reference Number page" when {
    "no errors exist" should {

      val form: Form[String]               = formProvider.apply()
      lazy val view: Html                  = injectedView(form, backlink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(title)

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

        checkPageTitle(errorPrefix + title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe requiredError
        }

        "render an error message against the field" in {
          elementText("#value-error") mustBe errorPrefix + requiredError
        }
      }
    }

    "data in invalid format supplied" should {

      "an error exists" should {
        lazy val form: Form[String]          = formProvider().bind(Map("value" -> "invalid"))
        lazy val view: Html                  = injectedView(form, backlink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(errorPrefix + title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe formatError
        }

        "render an error message against the field" in {
          elementText("#value-error") mustBe errorPrefix + formatError
        }
      }
    }
  }

  it should {

    val form: Form[String]               = formProvider.apply()
    lazy val view: Html                  = injectedView(form, backlink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '$title'" in {
      elementText("h1") mustBe title
    }

    s"have the correct hint" in {
      elementText("#value-hint") mustBe hint
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain(
        "href" -> controllers.cancelCase.routes.CancelCaseReferenceNumberController.onLoad().url
      )
    }

    s"render the input field" in {
      document.select("#value").size mustBe 1
    }

    s"render the details block" in {
      document.select("#main-content > div > div > form > details > summary > span").size mustBe 1
    }

    s"render the details block text" in {
      elementText("#main-content > div > div > form > details > div") mustBe details
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }
  }
}
