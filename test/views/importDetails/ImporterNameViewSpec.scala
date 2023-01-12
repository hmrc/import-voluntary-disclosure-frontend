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
import forms.importDetails.ImporterNameFormProvider
import messages.BaseMessages
import messages.importDetails.ImporterNameMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.importDetails.ImporterNameView

class ImporterNameViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ImporterNameView = app.injector.instanceOf[ImporterNameView]

  val formProvider: ImporterNameFormProvider = injector.instanceOf[ImporterNameFormProvider]

  "Rendering the Importer's Name page" when {
    "no errors exist" should {

      val form: Form[String] = formProvider.apply()
      lazy val view: Html = injectedView(
        form,
        Call("GET", controllers.importDetails.routes.UserTypeController.onLoad().url)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ImporterNameMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "no data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] = formProvider().bind(Map("fullName" -> ""))
        lazy val view: Html = injectedView(
          form,
          Call("GET", controllers.importDetails.routes.UserTypeController.onLoad().url)
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(ImporterNameMessages.errorPrefix + ImporterNameMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe thereIsAProblemPrefix + ImporterNameMessages.nonEmpty
        }

        "render an error message against the field" in {
          elementText("#fullName-error") mustBe ImporterNameMessages.errorPrefix + ImporterNameMessages.nonEmpty
        }
      }
    }

    "too short data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] = formProvider().bind(Map("fullName" -> "a"))
        lazy val view: Html = injectedView(
          form,
          Call("GET", controllers.importDetails.routes.UserTypeController.onLoad().url)
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(ImporterNameMessages.errorPrefix + ImporterNameMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe thereIsAProblemPrefix + ImporterNameMessages.nameMinLength
        }

        "render an error message against the field" in {
          elementText("#fullName-error") mustBe ImporterNameMessages.errorPrefix + ImporterNameMessages.nameMinLength
        }
      }
    }

    "too long data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] =
          formProvider().bind(Map("fullName" -> "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        lazy val view: Html = injectedView(
          form,
          Call("GET", controllers.importDetails.routes.UserTypeController.onLoad().url)
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(ImporterNameMessages.errorPrefix + ImporterNameMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe thereIsAProblemPrefix + ImporterNameMessages.nameMaxLength
        }

        "render an error message against the field" in {
          elementText("#fullName-error") mustBe ImporterNameMessages.errorPrefix + ImporterNameMessages.nameMaxLength
        }
      }
    }

    "emoji data supplied" should {

      "an error exists" should {
        lazy val form: Form[String] = formProvider().bind(Map("fullName" -> "\uD83D\uDE0E\uD83E\uDD14\uD83E\uDD16"))
        lazy val view: Html = injectedView(
          form,
          Call("GET", controllers.importDetails.routes.UserTypeController.onLoad().url)
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(ImporterNameMessages.errorPrefix + ImporterNameMessages.title)

        "render an error summary with the correct message" in {
          elementText(
            "div.govuk-error-summary > div"
          ) mustBe thereIsAProblemPrefix + ImporterNameMessages.emojiNotAllowed
        }

        "render an error message against the field" in {
          elementText("#fullName-error") mustBe ImporterNameMessages.errorPrefix + ImporterNameMessages.emojiNotAllowed
        }
      }
    }
  }

  it should {

    val form: Form[String] = formProvider.apply()
    lazy val view: Html = injectedView(
      form,
      Call("GET", controllers.importDetails.routes.UserTypeController.onLoad().url)
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${ImporterNameMessages.title}'" in {
      elementText("h1") mustBe ImporterNameMessages.title
    }

    s"have the correct hint" in {
      elementText("#fullName-hint") mustBe ImporterNameMessages.hint
    }
    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain(
        "href" -> controllers.importDetails.routes.UserTypeController.onLoad().url
      )
    }

    s"the input field is rendered" in {
      document.select("#fullName").size mustBe 1
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }
  }
}
