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
import forms.importDetails.UserTypeFormProvider
import messages.BaseMessages
import messages.importDetails.UserTypeMessages
import models.importDetails.UserType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.importDetails.UserTypeView

class UserTypeViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UserTypeView = app.injector.instanceOf[UserTypeView]
  val formProvider: UserTypeFormProvider      = injector.instanceOf[UserTypeFormProvider]

  "Rendering the UserType page" when {

    "no errors exist" should {
      lazy val form: Form[UserType] = formProvider()
      lazy val view: Html =
        injectedView(form, controllers.serviceEntry.routes.ConfirmEORIDetailsController.onLoad())(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UserTypeMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
      "contains a h2 in the - Before you continue - section" in {
        elementText("#main-content > div > div > form > h2") mustBe UserTypeMessages.beforeYouContinueh2
      }

      "contains a p1 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(4)"
        ) mustBe UserTypeMessages.beforeYouContinuep1
      }

      "contains a p2 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(5)"
        ) mustBe UserTypeMessages.beforeYouContinuep2
      }

      "contains a p3 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(6)"
        ) mustBe UserTypeMessages.beforeYouContinuep3
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[UserType] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html =
        injectedView(form, controllers.serviceEntry.routes.ConfirmEORIDetailsController.onLoad())(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UserTypeMessages.errorPrefix + UserTypeMessages.title)

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe thereIsAProblemPrefix + UserTypeMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe UserTypeMessages.errorPrefix + UserTypeMessages.requiredError
      }

      "contains a h2 in the - Before you continue - section" in {
        elementText("#main-content > div > div > form > h2") mustBe UserTypeMessages.beforeYouContinueh2
      }

      "contains a p1 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(4)"
        ) mustBe UserTypeMessages.beforeYouContinuep1
      }

      "contains a p2 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(5)"
        ) mustBe UserTypeMessages.beforeYouContinuep2
      }

      "contains a p3 in the - Before you continue - section" in {
        elementText(
          "#main-content > div > div > form > p:nth-child(6)"
        ) mustBe UserTypeMessages.beforeYouContinuep3
      }

    }
  }

  it should {
    lazy val form: Form[UserType] = formProvider()
    lazy val view: Html =
      injectedView(form, controllers.serviceEntry.routes.ConfirmEORIDetailsController.onLoad())(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${UserTypeMessages.h1}'" in {
      elementText("h1") mustBe UserTypeMessages.h1
    }

    s"have the correct value for the first radio button of '${UserTypeMessages.radioButtonOne}'" in {
      elementText("#main-content div.govuk-radios__item:nth-child(1)") mustBe UserTypeMessages.radioButtonOne
    }

    s"have the correct value for the second radio button of '${UserTypeMessages.radioButtonTwo}'" in {
      elementText("#main-content div.govuk-radios__item:nth-child(2)") mustBe UserTypeMessages.radioButtonTwo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain(
        "href" -> controllers.serviceEntry.routes.ConfirmEORIDetailsController.onLoad().url
      )
    }
  }
}
