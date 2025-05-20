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
import forms.paymentInfo.RepresentativeDanFormProvider
import messages.paymentInfo.{RepresentativeDanDutyMessages, RepresentativeDanMessages}
import messages.BaseMessages
import models.RepresentativeDan
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.paymentInfo.RepresentativeDanDutyView

class RepresentativeDanDutyViewSpec extends ViewBaseSpec with BaseMessages {

  val backLink = Call("GET", "backLinkUrl")

  val representativeName = "Traders ltd"

  private lazy val injectedView: RepresentativeDanDutyView = app.injector.instanceOf[RepresentativeDanDutyView]

  val formProvider: RepresentativeDanFormProvider = injector.instanceOf[RepresentativeDanFormProvider]

  def repDanFormWithValues(accountNumber: String, danType: String): Form[RepresentativeDan] =
    formProvider().bind(Map("accountNumber" -> accountNumber, "value" -> danType))

  "Rendering the RepresentativeDan Duty page" when {

    "no errors exist" should {
      val form: Form[RepresentativeDan] = formProvider.apply()
      lazy val view: Html = injectedView(form, "importer", representativeName, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(RepresentativeDanDutyMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the input field" in {
        document.select("#accountNumber-error").size mustBe 0
      }

      "not render an error message against the radio field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no value has been specified for the account number)" should {
      lazy val form: Form[RepresentativeDan] = repDanFormWithValues(emptyString, "A")
      lazy val view: Html = injectedView(form, "importer", representativeName, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(RepresentativeDanMessages.errorPrefix + RepresentativeDanDutyMessages.title)

      "render an error summary with the correct message " in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe thereIsAProblemPrefix + RepresentativeDanDutyMessages.accountNumberRequiredError
      }

      "render an error message against the field" in {
        elementText(
          "#accountNumber-error"
        ) mustBe RepresentativeDanDutyMessages.errorPrefix + RepresentativeDanDutyMessages.accountNumberRequiredError
      }
    }

    "an error exists (account number value is an invalid format)" should {
      lazy val form: Form[RepresentativeDan] = repDanFormWithValues("!234567", "A")
      lazy val view: Html = injectedView(form, "importer", representativeName, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(RepresentativeDanDutyMessages.errorPrefix + RepresentativeDanDutyMessages.title)

      "render an error summary with the correct message " in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe thereIsAProblemPrefix + RepresentativeDanDutyMessages.accountNumberFormatError
      }

      "render an error message against the field" in {
        elementText(
          "#accountNumber-error"
        ) mustBe RepresentativeDanDutyMessages.errorPrefix + RepresentativeDanDutyMessages.accountNumberFormatError
      }
    }

    "an error exists (dan type radio selection has not been provided)" should {
      lazy val form: Form[RepresentativeDan] = repDanFormWithValues("1234567", emptyString)
      lazy val view: Html = injectedView(form, "importer", representativeName, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(RepresentativeDanDutyMessages.errorPrefix + RepresentativeDanDutyMessages.title)

      "render an error summary with the correct message " in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe thereIsAProblemPrefix + RepresentativeDanDutyMessages.danTypeRequiredError
      }

      "render an error message against the field" in {
        elementText(
          "#value-error"
        ) mustBe RepresentativeDanDutyMessages.errorPrefix + RepresentativeDanDutyMessages.danTypeRequiredError
      }
    }

  }

  it should {
    lazy val form: Form[RepresentativeDan] = formProvider()
    lazy val view: Html = injectedView(form, "importer", representativeName, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(RepresentativeDanDutyMessages.title)

    s"have the correct h1 of '${RepresentativeDanDutyMessages.title}'" in {
      elementText("h1") mustBe RepresentativeDanDutyMessages.title
    }

    s"have the correct accountNumber label of '${RepresentativeDanDutyMessages.accountNumberLabel}'" in {
      elementText(".govuk-label[for=\"accountNumber\"]") mustBe RepresentativeDanDutyMessages.accountNumberLabel
    }

    s"have the correct danType label of '${RepresentativeDanDutyMessages.radioButtonLabel}'" in {
      elementText(
        "#main-content > div > div > form > div:nth-child(3) > fieldset > legend"
      ) mustBe RepresentativeDanDutyMessages.radioButtonLabel
    }

    s"have the correct value for the first radio button of '${RepresentativeDanDutyMessages.getRadioOne(representativeName)}'" in {
      elementText(
        "#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(1) > label"
      ) mustBe RepresentativeDanDutyMessages.getRadioOne(representativeName)
    }

    s"have the correct value for the second radio button of '${RepresentativeDanDutyMessages.radio2}'" in {
      elementText(
        "#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(2) > label"
      ) mustBe RepresentativeDanDutyMessages.radio2
    }

    s"have the correct value for the third radio button of '${RepresentativeDanDutyMessages.radio3}'" in {
      elementText(
        "#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(3) > label"
      ) mustBe RepresentativeDanDutyMessages.radio3
    }

    s"have the correct radio 2 hint of '${RepresentativeDanDutyMessages.radio2Hint}'" in {
      elementText("#value-2-item-hint") mustBe RepresentativeDanDutyMessages.radio2Hint
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "backLinkUrl")
    }
  }
}
