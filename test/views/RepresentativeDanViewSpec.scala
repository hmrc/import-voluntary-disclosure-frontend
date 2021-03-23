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
import forms.RepresentativeDanFormProvider
import messages.{BaseMessages, RepresentativeDanMessages}
import models.RepresentativeDan
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.RepresentativeDanView

class RepresentativeDanViewSpec extends ViewBaseSpec with BaseMessages {

  def formDataSetup(accountNumber: Option[String] = Some("1234567"),
                    danType: Option[String] = Some("A")): Map[String, String] =
    (
      accountNumber.map(_ => "accountNumber" -> accountNumber.get) ++
        danType.map(_ => "value" -> danType.get)
      ).toMap

  private lazy val injectedView: RepresentativeDanView = app.injector.instanceOf[RepresentativeDanView]

  val formProvider: RepresentativeDanFormProvider = injector.instanceOf[RepresentativeDanFormProvider]

  val backLinkUrl = "backLinkUrl"

  "Rendering the Representative Dan One view" when {

    val missingAccountNumber = formDataSetup(accountNumber = None)
    val missingDanType = formDataSetup(danType = None)
    val formatAccountNumber = formDataSetup(accountNumber = Some("!23456"))

    // represents error scenario description, data and expected error message
    val testScenarios: Map[String, (Map[String, String], String)] = Map(
      "Account Number is missing" -> (missingAccountNumber -> RepresentativeDanMessages.accountNumberRequiredError),
      "Dan Type is missing" -> (missingDanType -> RepresentativeDanMessages.danTypeRequiredError),
      "Account Number is incorrect" -> (formatAccountNumber -> RepresentativeDanMessages.accountNumberFormatError)
    )

    testScenarios.foreach { scenario =>
      val (description, (formData, errorMessage)) = scenario

      description should {
        lazy val form: Form[RepresentativeDan] = formProvider().bind(formData)
        lazy val view: Html = injectedView(form, Call("GET", backLinkUrl))(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "update the page title to include the error prefix" in {
          document.title mustBe RepresentativeDanMessages.errorPrefix + RepresentativeDanMessages.title
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
    lazy val form: Form[RepresentativeDan] = formProvider()
    lazy val view: Html = injectedView(form, Call("GET", backLinkUrl))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title of '${RepresentativeDanMessages.title}'" in {
      document.title mustBe RepresentativeDanMessages.title
    }

    s"have the correct h1 of '${RepresentativeDanMessages.h1}'" in {
      elementText("h1") mustBe RepresentativeDanMessages.h1
    }

    s"have the correct accountNumber label of '${RepresentativeDanMessages.accountNumberLabel}'" in {
      elementText(".govuk-label[for=\"accountNumber\"]") mustBe RepresentativeDanMessages.accountNumberLabel
    }

    s"have the correct danType label of '${RepresentativeDanMessages.radioButtonLabel}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > legend") mustBe RepresentativeDanMessages.radioButtonLabel
    }

    s"have the correct value for the first radio button of '${RepresentativeDanMessages.radio1}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(1) > label") mustBe RepresentativeDanMessages.radio1
    }

    s"have the correct value for the second radio button of '${RepresentativeDanMessages.radio2}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(2) > label") mustBe RepresentativeDanMessages.radio2
    }

    s"have the correct value for the third radio button of '${RepresentativeDanMessages.radio3}'" in {
      elementText("#main-content > div > div > form > div:nth-child(3) > fieldset > div > div:nth-child(3) > label") mustBe RepresentativeDanMessages.radio3
    }

    s"have the correct radio 2 hint of '${RepresentativeDanMessages.radio2Hint}'" in {
      elementText("#value-2-item-hint") mustBe RepresentativeDanMessages.radio2Hint
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "backLinkUrl")
    }
  }
}
