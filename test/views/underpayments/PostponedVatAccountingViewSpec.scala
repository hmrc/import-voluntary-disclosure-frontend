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

package views.underpayments

import base.ViewBaseSpec
import forms.underpayments.PostponedVatAccountingFormProvider
import messages.underpayments.PostponedVatAccountingMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.underpayments.PostponedVatAccountingView

class PostponedVatAccountingViewSpec extends ViewBaseSpec {

  private lazy val injectedView: PostponedVatAccountingView = app.injector.instanceOf[PostponedVatAccountingView]
  val importerName                                          = "importerName"
  val formProvider: PostponedVatAccountingFormProvider      = injector.instanceOf[PostponedVatAccountingFormProvider]

  "Rendering the Postponed VAT Accounting page" when {

    "no errors exist" should {
      lazy val form: Form[Boolean] = formProvider(importerName)
      lazy val view: Html =
        injectedView(form, importerName, Call("GET", "backLink"))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(PostponedVatAccountingMessages.pageTitle(importerName))

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean] = formProvider(importerName).bind(Map("value" -> ""))
      lazy val view: Html =
        injectedView(form, importerName, Call("GET", "backLink"))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(
        PostponedVatAccountingMessages.errorPrefix + PostponedVatAccountingMessages.pageTitle(importerName)
      )

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe PostponedVatAccountingMessages.thereIsAProblemPrefix + PostponedVatAccountingMessages.errorRequired(
          importerName
        )
      }

      "render an error message against the field" in {
        elementText(
          "#value-error"
        ) mustBe PostponedVatAccountingMessages.errorPrefix + PostponedVatAccountingMessages.errorRequired(
          importerName
        )
      }

    }
  }

  it should {
    lazy val form: Form[Boolean] = formProvider(importerName)
    lazy val view: Html =
      injectedView(form, importerName, Call("GET", "backLink"))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${PostponedVatAccountingMessages.pageTitle(importerName)}'" in {
      elementText("h1") mustBe PostponedVatAccountingMessages.pageTitle(importerName)
    }

    s"have the correct value for the first radio button of '${PostponedVatAccountingMessages.siteYes}'" in {
      elementText(
        "#main-content div.govuk-radios__item:nth-child(1)"
      ) mustBe PostponedVatAccountingMessages.siteYes
    }

    s"have the correct value for the second radio button of '${PostponedVatAccountingMessages.siteNo}'" in {
      elementText(
        "#main-content div.govuk-radios__item:nth-child(2)"
      ) mustBe PostponedVatAccountingMessages.siteNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "backLink")
    }
  }
}
