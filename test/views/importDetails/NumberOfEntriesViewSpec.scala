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

package views.importDetails

import base.ViewBaseSpec
import forms.importDetails.NumberOfEntriesFormProvider
import messages.BaseMessages
import messages.importDetails.NumberOfEntriesMessages
import models.importDetails.NumberOfEntries
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.importDetails.NumberOfEntriesView

class NumberOfEntriesViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: NumberOfEntriesView = app.injector.instanceOf[NumberOfEntriesView]

  val formProvider: NumberOfEntriesFormProvider = injector.instanceOf[NumberOfEntriesFormProvider]

  "Rendering the NumberOfEntries page" when {
    "no errors exist and representative flow is selected" should {

      val form: Form[NumberOfEntries] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        "importer",
        isRepFlow = true,
        controllers.importDetails.routes.UserTypeController.onLoad()
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(NumberOfEntriesMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have the correct value for the first radio button of '${NumberOfEntriesMessages.radioButtonOne}'" in {
        elementText(
          "#main-content > div > div > form > div > fieldset > div > div:nth-child(1)"
        ) mustBe NumberOfEntriesMessages.radioButtonOne
      }

      s"have the correct value for the second radio button of '${NumberOfEntriesMessages.radioButtonTwo}'" in {
        elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe
          s"${NumberOfEntriesMessages.radioButtonTwo} ${NumberOfEntriesMessages.hint}"
      }

      "contains a h2 in the - Before you continue - section" in {
        elementText("#main-content > div > div > form > h2") mustBe NumberOfEntriesMessages.beforeYouContinueh2
      }

      "contains a p1 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(4)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep1
      }

      "contains a p2 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(5)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep2
      }

      "contains a p3 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(6)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep3
      }
    }

    "no errors exist and importer flow is selected" should {

      val form: Form[NumberOfEntries] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        "importer",
        isRepFlow = false,
        controllers.importDetails.routes.UserTypeController.onLoad()
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(NumberOfEntriesMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have the correct value for the first radio button of '${NumberOfEntriesMessages.radioButtonOne}'" in {
        elementText(
          "#main-content > div > div > form > div > fieldset > div > div:nth-child(1)"
        ) mustBe NumberOfEntriesMessages.radioButtonOne
      }

      s"have the correct value for the second radio button of '${NumberOfEntriesMessages.radioButtonTwo}'" in {
        elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe
          s"${NumberOfEntriesMessages.radioButtonTwo}"
      }

      "contains a h2 in the - Before you continue - section" in {
        elementText("#main-content > div > div > form > h2") mustBe NumberOfEntriesMessages.beforeYouContinueh2
      }

      "contains a p1 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(4)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep1
      }

      "contains a p2 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(5)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep2
      }

      "contains a p3 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(6)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep3
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[NumberOfEntries] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(
        form,
        "importer",
        isRepFlow = true,
        controllers.importDetails.routes.UserTypeController.onLoad()
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(NumberOfEntriesMessages.errorPrefix + NumberOfEntriesMessages.title)

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe NumberOfEntriesMessages.thereIsAProblemPrefix + NumberOfEntriesMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe NumberOfEntriesMessages.errorPrefix + NumberOfEntriesMessages.requiredError
      }

      "contains a h2 in the - Before you continue - section" in {
        elementText("#main-content > div > div > form > h2") mustBe NumberOfEntriesMessages.beforeYouContinueh2
      }

      "contains a p1 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(4)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep1
      }

      "contains a p2 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(5)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep2
      }

      "contains a p3 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(6)"
        ) mustBe NumberOfEntriesMessages.beforeYouContinuep3
      }

    }
  }

  it should {

    val form: Form[NumberOfEntries] = formProvider.apply()
    lazy val view: Html = injectedView(
      form,
      "importer",
      isRepFlow = true,
      controllers.importDetails.routes.UserTypeController.onLoad()
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${NumberOfEntriesMessages.title}'" in {
      elementText("h1") mustBe NumberOfEntriesMessages.title
    }

  }
}
