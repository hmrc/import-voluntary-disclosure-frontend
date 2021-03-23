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

package views

import base.ViewBaseSpec
import forms.RepresentativeDanOneFormProvider
import messages.{BaseMessages, RepresentativeDanOneMessages}
import models.RepresentativeDanOne
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.RepresentativeDanOneView

class RepresentativeDanOneViewSpec extends ViewBaseSpec with BaseMessages {

  def formDataSetup(accountNumber: Option[String] = Some("1234567"),
                    danType: Option[String] = Some("A")): Map[String, String] =
    (
      accountNumber.map(_ => "accountNumber" -> accountNumber.get) ++
        danType.map(_ => "value" -> danType.get)
      ).toMap

  private lazy val injectedView: RepresentativeDanOneView = app.injector.instanceOf[RepresentativeDanOneView]

  val formProvider: RepresentativeDanOneFormProvider = injector.instanceOf[RepresentativeDanOneFormProvider]

  val backLinkUrl = "backLinkUrl"

  "Rendering the Representative Dan One view" when {

    val missingAccountNumber = formDataSetup(accountNumber = None)
    val missingDanType = formDataSetup(danType = None)
    val formatAccountNumber = formDataSetup(accountNumber = Some("!23456"))

    // represents error scenario description, data and expected error message
    val testScenarios: Map[String, (Map[String, String], String)] = Map(
      "Account Number is missing" -> (missingAccountNumber -> RepresentativeDanOneMessages.accountNumberRequiredError),
      "Dan Type is missing" -> (missingDanType -> RepresentativeDanOneMessages.danTypeRequiredError),
      "Account Number is incorrect" -> (formatAccountNumber -> RepresentativeDanOneMessages.accountNumberFormatError)
    )

    testScenarios.foreach { scenario =>
      val (description, (formData, errorMessage)) = scenario

      description should {
        lazy val form: Form[RepresentativeDanOne] = formProvider().bind(formData)
        lazy val view: Html = injectedView(form, Call("GET", backLinkUrl))(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe RepresentativeDanOneMessages.errorPrefix + RepresentativeDanOneMessages.title
        }

        s"have correct message in the error summary" in {
          elementText(".govuk-error-summary__list") mustBe errorMessage
        }

        s"have correct error message against the field in error" in {
          // Note: the error message includes a visually hidden "Error:" prompt to accessibility
          elementText(".govuk-error-message") mustBe "Error: " + errorMessage
        }
      }
    }
  }

  it should {
    lazy val form: Form[RepresentativeDanOne] = formProvider()
    lazy val view: Html = injectedView(form, Call("GET", backLinkUrl))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title of '${RepresentativeDanOneMessages.title}'" in {
      document.title mustBe RepresentativeDanOneMessages.title
    }

    s"have the correct h1 of '${RepresentativeDanOneMessages.h1}'" in {
      elementText("h1") mustBe RepresentativeDanOneMessages.h1
    }

    s"have the correct accountNumber label of '${RepresentativeDanOneMessages.accountNumberLabel}'" in {
      elementText(".govuk-label[for=\"accountNumber\"]") mustBe RepresentativeDanOneMessages.accountNumberLabel
    }

    s"have the correct danType label of '${RepresentativeDanOneMessages.radioButtonLabel}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > legend") mustBe RepresentativeDanOneMessages.radioButtonLabel
    }

    s"have the correct value for the first radio button of '${RepresentativeDanOneMessages.radio1}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(1) > label") mustBe RepresentativeDanOneMessages.radio1
    }

    s"have the correct value for the second radio button of '${RepresentativeDanOneMessages.radio2}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(2) > label") mustBe RepresentativeDanOneMessages.radio2
    }

    s"have the correct value for the third radio button of '${RepresentativeDanOneMessages.radio3}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(3) > label") mustBe RepresentativeDanOneMessages.radio3
    }

    s"have the correct radio 2 hint of '${RepresentativeDanOneMessages.radio2Hint}'" in {
      elementText("#value-2-item-hint") mustBe RepresentativeDanOneMessages.radio2Hint
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "backLinkUrl")
    }
  }
}
