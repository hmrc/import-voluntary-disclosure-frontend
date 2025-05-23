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

package views.paymentInfo

import base.ViewBaseSpec
import forms.paymentInfo.SplitPaymentFormProvider
import messages.BaseMessages
import messages.paymentInfo.SplitPaymentMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.paymentInfo.SplitPaymentView

class SplitPaymentViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: SplitPaymentView = app.injector.instanceOf[SplitPaymentView]

  val formProvider: SplitPaymentFormProvider = injector.instanceOf[SplitPaymentFormProvider]

  "Rendering the Split Payment page" when {

    "no errors exist" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        controllers.paymentInfo.routes.DefermentController.onLoad()
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(SplitPaymentMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(
        form,
        controllers.paymentInfo.routes.DefermentController.onLoad()
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(SplitPaymentMessages.errorPrefix + SplitPaymentMessages.title)

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe thereIsAProblemPrefix + SplitPaymentMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe SplitPaymentMessages.errorPrefix + SplitPaymentMessages.requiredError
      }
    }

  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(
      form,
      Call("GET", controllers.paymentInfo.routes.DefermentController.onLoad().url)
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct value for the first radio button of '${SplitPaymentMessages.radioYes}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label"
      ) mustBe SplitPaymentMessages.radioYes
    }

    s"have the correct value for the second radio button of '${SplitPaymentMessages.radioNo}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label"
      ) mustBe SplitPaymentMessages.radioNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain(
        "href" -> controllers.paymentInfo.routes.DefermentController.onLoad().url
      )
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
