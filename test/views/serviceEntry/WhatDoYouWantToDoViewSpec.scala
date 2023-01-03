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

package views.serviceEntry

import base.ViewBaseSpec
import forms.serviceEntry.WhatDoYouWantToDoFormProvider
import messages.serviceEntry.WhatDoYouWantToDoMessages
import models.SubmissionType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.serviceEntry.WhatDoYouWantToDoView

class WhatDoYouWantToDoViewSpec extends ViewBaseSpec {

  lazy val backLink: Call = Call("GET", "url")

  private lazy val injectedView: WhatDoYouWantToDoView = app.injector.instanceOf[WhatDoYouWantToDoView]

  val formProvider: WhatDoYouWantToDoFormProvider = injector.instanceOf[WhatDoYouWantToDoFormProvider]

  "Rendering the WhatDoYouWantToDo page" when {
    "no errors exist" should {

      val form: Form[SubmissionType]       = formProvider.apply()
      lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(WhatDoYouWantToDoMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[SubmissionType]  = formProvider().bind(Map("value" -> ""))
      lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(WhatDoYouWantToDoMessages.errorPrefix + WhatDoYouWantToDoMessages.title)

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe WhatDoYouWantToDoMessages.error
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe WhatDoYouWantToDoMessages.errorPrefix + WhatDoYouWantToDoMessages.error
      }

    }
  }

  it should {

    val form: Form[SubmissionType]       = formProvider.apply()
    lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct page heading" in {
      elementText("h1") mustBe WhatDoYouWantToDoMessages.title
    }

    "have the correct value for the first radio button" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label"
      ) mustBe WhatDoYouWantToDoMessages.createCaseMsg
    }

    "have the correct value for the second radio button" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label"
      ) mustBe WhatDoYouWantToDoMessages.updateCaseMsg
    }

    "have the correct value for the third radio button" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(3) > label"
      ) mustBe WhatDoYouWantToDoMessages.cancelCaseMsg
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe WhatDoYouWantToDoMessages.continue
    }

  }

}
