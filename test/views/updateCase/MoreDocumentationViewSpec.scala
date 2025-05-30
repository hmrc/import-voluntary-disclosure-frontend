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

package views.updateCase

import base.ViewBaseSpec
import forms.updateCase.MoreDocumentationFormProvider
import messages.BaseMessages
import messages.updateCase.MoreDocumentationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.updateCase.MoreDocumentationView

class MoreDocumentationViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: MoreDocumentationView = app.injector.instanceOf[MoreDocumentationView]

  val formProvider: MoreDocumentationFormProvider = injector.instanceOf[MoreDocumentationFormProvider]

  private lazy val backLink = controllers.updateCase.routes.MoreDocumentationController.onLoad()

  "Rendering the view" when {
    "no errors exist" should {

      val form: Form[Boolean]              = formProvider.apply()
      lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(MoreDocumentationMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean]         = formProvider().bind(Map("value" -> ""))
      lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(MoreDocumentationMessages.errorPrefix + MoreDocumentationMessages.title)

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe thereIsAProblemPrefix + MoreDocumentationMessages.requiredError
      }

      "render an error message against the field" in {
        elementText(
          "#value-error"
        ) mustBe MoreDocumentationMessages.errorPrefix + MoreDocumentationMessages.requiredError
      }

    }
  }

  it should {

    val form: Form[Boolean]              = formProvider.apply()
    lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${MoreDocumentationMessages.title}'" in {
      elementText("h1") mustBe MoreDocumentationMessages.title
    }

    s"have the correct value for the first radio button of '${MoreDocumentationMessages.siteYes}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(1)"
      ) mustBe MoreDocumentationMessages.siteYes
    }

    s"have the correct value for the second radio button of '${MoreDocumentationMessages.siteNo}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(2)"
      ) mustBe MoreDocumentationMessages.siteNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
