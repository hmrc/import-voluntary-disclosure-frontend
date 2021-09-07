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

package views.updateCase

import base.ViewBaseSpec
import forms.updateCase.UpdateAdditionalInformationFormProvider
import messages.{BaseMessages, UpdateAdditionalInformationMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.updateCase.UpdateAdditionalInformationView

class UpdateAdditionalInformationViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UpdateAdditionalInformationView =
    app.injector.instanceOf[UpdateAdditionalInformationView]

  val formProvider: UpdateAdditionalInformationFormProvider =
    injector.instanceOf[UpdateAdditionalInformationFormProvider]

  "Rendering the Update Additional Information page" when {

    "no errors exist" should {

      val form: Form[String]               = formProvider.apply()
      lazy val view: Html                  = injectedView(form, Some(Call("GET", "url")))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UpdateAdditionalInformationMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have the correct h1 of '${UpdateAdditionalInformationMessages.h1}'" in {
        elementText("h1") mustBe UpdateAdditionalInformationMessages.h1
      }

    }

    "no data supplied" should {

      "an error exists " should {
        lazy val form: Form[String]          = formProvider().bind(Map("value" -> ""))
        lazy val view: Html                  = injectedView(form, Some(Call("GET", "url")))(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(UpdateAdditionalInformationMessages.errorPrefix + UpdateAdditionalInformationMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe UpdateAdditionalInformationMessages.requiredError
        }

        "render an error message against the field" in {
          elementText(
            "#value-error"
          ) mustBe UpdateAdditionalInformationMessages.errorPrefix + UpdateAdditionalInformationMessages.requiredError
        }

      }

    }

  }

  it should {

    val form: Form[String]               = formProvider.apply()
    lazy val view: Html                  = injectedView(form, Some(Call("GET", "url")))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }

}
