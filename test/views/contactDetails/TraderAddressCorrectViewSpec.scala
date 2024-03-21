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

package views.contactDetails

import base.ViewBaseSpec
import forms.contactDetails.TraderAddressCorrectFormProvider
import messages.contactDetails.ImporterAddressMessages
import models.ContactAddress
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import utils.ReusableValues
import views.html.contactDetails.TraderAddressCorrectView

class TraderAddressCorrectViewSpec extends ViewBaseSpec with ReusableValues {

  private lazy val injectedView: TraderAddressCorrectView = app.injector.instanceOf[TraderAddressCorrectView]

  private val backLink: Call = Call("GET", "url")

  private val traderName: String           = "First Last"
  private val importerName: Option[String] = Some("Traders ltd")

  val formProvider: TraderAddressCorrectFormProvider = injector.instanceOf[TraderAddressCorrectFormProvider]

  "Rendering the AcceptanceDate page" when {
    "no errors exist with full trader details" should {

      val form: Form[Boolean] = formProvider.apply(importerName.get)
      lazy val view: Html =
        injectedView(form, addressDetails, traderName, importerName, isRepFlow = true, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ImporterAddressMessages.getTitle(traderName))

      s"have the correct h1 of '${ImporterAddressMessages.getTitle(traderName)}'" in {
        elementText(
          "h1"
        ) mustBe ImporterAddressMessages.getTitle(traderName)
      }

      s"have the correct p of '${ImporterAddressMessages.getParagraph(traderName, importerName)}'" in {
        elementText(
          "#main-content > div > div > form > p"
        ) mustBe ImporterAddressMessages.getParagraph(traderName, importerName)
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "no errors exist with trader details without postcode" should {

      val form: Form[Boolean] = formProvider.apply(importerName.get)
      lazy val view: Html =
        injectedView(
          form,
          ContactAddress("first", None, "second", None, "fourth"),
          traderName,
          importerName,
          isRepFlow = true,
          backLink
        )(
          fakeRequest,
          messages
        )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ImporterAddressMessages.getTitle(traderName))

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean] = formProvider(importerName.get).bind(Map("value" -> ""))
      lazy val view: Html =
        injectedView(form, addressDetails, traderName, importerName, isRepFlow = true, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ImporterAddressMessages.errorPrefix + ImporterAddressMessages.getTitle(traderName))

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe ImporterAddressMessages.thereIsAProblemPrefix + ImporterAddressMessages.errorRequired
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe
          ImporterAddressMessages.errorPrefix + ImporterAddressMessages.errorRequired
      }

    }
  }

  it should {

    val form: Form[Boolean] = formProvider.apply(importerName.get)
    lazy val view: Html =
      injectedView(form, addressDetails, traderName, importerName, isRepFlow = true, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct value for the first radio button of '${ImporterAddressMessages.siteYes}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label"
      ) mustBe ImporterAddressMessages.siteYes
    }

    s"have the correct value for the second radio button of '${ImporterAddressMessages.siteNo}'" in {
      elementText(
        "#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label"
      ) mustBe ImporterAddressMessages.siteNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe ImporterAddressMessages.continue
    }

  }

}
