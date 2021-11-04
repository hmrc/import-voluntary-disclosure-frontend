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

package views.reasons

import base.ViewBaseSpec
import forms.reasons.MoreInformationFormProvider
import messages.BaseMessages
import messages.reasons.MoreInformationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.reasons.MoreInformationView

class MoreInformationViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: MoreInformationView = app.injector.instanceOf[MoreInformationView]

  val formProvider: MoreInformationFormProvider = injector.instanceOf[MoreInformationFormProvider]

  "Rendering the More Information page" when {

    "no errors exist single entry" should {

      val form: Form[String] = formProvider.apply()
      lazy val view: Html    = injectedView(form, Some(Call("GET", "url")), isSingleEntry = true)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(MoreInformationMessages.singleEntryTitle)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have the correct h1 of '${MoreInformationMessages.singleEntryTitle}'" in {
        elementText("h1") mustBe MoreInformationMessages.singleEntryTitle
      }

    }

    "no errors exist bulk entry" should {

      val form: Form[String] = formProvider.apply()
      lazy val view: Html = injectedView(form, Some(Call("GET", "url")), isSingleEntry = false)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(MoreInformationMessages.bulkEntryTitle)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have the correct h1 of '${MoreInformationMessages.bulkEntryTitle}'" in {
        elementText("h1") mustBe MoreInformationMessages.bulkEntryTitle
      }

      s"have the correct p1 of '${MoreInformationMessages.bulkEntryTitle}'" in {
        elementText("#main-content > div > div > p:nth-child(2)") mustBe MoreInformationMessages.bulkP1
      }

      s"have the correct p2 of '${MoreInformationMessages.bulkEntryTitle}'" in {
        elementText("#main-content > div > div > p:nth-child(3)") mustBe MoreInformationMessages.bulkP2
      }

    }

    "no data supplied" should {

      "an error exists single entry" should {
        lazy val form: Form[String] = formProvider().bind(Map("value" -> ""))
        lazy val view: Html = injectedView(form, Some(Call("GET", "url")), isSingleEntry = true)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(MoreInformationMessages.errorPrefix + MoreInformationMessages.singleEntryTitle)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe MoreInformationMessages.singleEntryRequiredError
        }

        "render an error message against the field" in {
          elementText(
            "#value-error"
          ) mustBe MoreInformationMessages.errorPrefix + MoreInformationMessages.singleEntryRequiredError
        }

      }

      "an error exists bulk entry" should {
        lazy val form: Form[String] = formProvider(false).bind(Map("value" -> ""))
        lazy val view: Html = injectedView(form, Some(Call("GET", "url")), isSingleEntry = false)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(MoreInformationMessages.errorPrefix + MoreInformationMessages.bulkEntryTitle)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe MoreInformationMessages.bulkEntryRequiredError
        }

        "render an error message against the field" in {
          elementText(
            "#value-error"
          ) mustBe MoreInformationMessages.errorPrefix + MoreInformationMessages.bulkEntryRequiredError
        }

      }

    }

  }

  it should {

    val form: Form[String] = formProvider.apply()
    lazy val view: Html    = injectedView(form, Some(Call("GET", "url")), isSingleEntry = true)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }

}
