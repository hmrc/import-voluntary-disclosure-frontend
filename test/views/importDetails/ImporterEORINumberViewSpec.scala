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

package views.importDetails

import base.ViewBaseSpec
import forms.importDetails.ImporterEORINumberFormProvider
import messages.BaseMessages
import messages.importDetails.ImporterEORINumberMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.importDetails.ImporterEORINumberView

class ImporterEORINumberViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ImporterEORINumberView = app.injector.instanceOf[ImporterEORINumberView]

  val formProvider: ImporterEORINumberFormProvider = injector.instanceOf[ImporterEORINumberFormProvider]

  "Rendering the Importer's EORI Number page" when {
    "no errors exist" should {

      val form: Form[String] = formProvider.apply("importer")
      lazy val view: Html = injectedView(
        form,
        "importer",
        Some(Call("GET", controllers.importDetails.routes.ImporterEORIExistsController.onLoad().url))
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ImporterEORINumberMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "no data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] = formProvider("importer").bind(Map("importerEORI" -> ""))
        lazy val view: Html = injectedView(
          form,
          "importer",
          Some(Call("GET", controllers.importDetails.routes.ImporterEORIExistsController.onLoad().url))
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(ImporterEORINumberMessages.errorPrefix + ImporterEORINumberMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe ImporterEORINumberMessages.nonEmpty
        }

        "render an error message against the field" in {
          elementText(
            "#importerEORI-error"
          ) mustBe ImporterEORINumberMessages.errorPrefix + ImporterEORINumberMessages.nonEmpty
        }
      }
    }

    "wrong format data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] = formProvider("importer").bind(Map("importerEORI" -> "345834921000"))
        lazy val view: Html = injectedView(
          form,
          "importer",
          Some(Call("GET", controllers.importDetails.routes.ImporterEORIExistsController.onLoad().url))
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(ImporterEORINumberMessages.errorPrefix + ImporterEORINumberMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe ImporterEORINumberMessages.incorrectFormat
        }

        "render an error message against the field" in {
          elementText(
            "#importerEORI-error"
          ) mustBe ImporterEORINumberMessages.errorPrefix + ImporterEORINumberMessages.incorrectFormat
        }
      }
    }
  }

  it should {

    val form: Form[String] = formProvider.apply("importer")
    lazy val view: Html = injectedView(
      form,
      "importer",
      Some(Call("GET", controllers.importDetails.routes.ImporterEORIExistsController.onLoad().url))
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${ImporterEORINumberMessages.title}'" in {
      elementText("h1") mustBe ImporterEORINumberMessages.title
    }

    s"have the correct hint" in {
      elementText("#importerEORI-hint") mustBe ImporterEORINumberMessages.hint
    }

    "render a back link with the correct back URL" in {
      elementAttributes("#back-link") must contain(
        "href" -> controllers.importDetails.routes.ImporterEORIExistsController.onLoad().url
      )
    }

    s"the input field is rendered" in {
      document.select("#importerEORI").size mustBe 1
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }
  }
}
