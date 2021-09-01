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

package views.cancelCase

import base.ViewBaseSpec
import forms.cancelCase.CancelCaseRemoveUploadedFileFormProvider
import messages.cancelCase.CancelCaseRemoveUploadedFileMessages._
import models.Index
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.cancelCase.CancelCaseRemoveUploadedFileView

class CancelCaseRemoveUploadedFileViewSpec extends ViewBaseSpec {

  private lazy val injectedView: CancelCaseRemoveUploadedFileView =
    app.injector.instanceOf[CancelCaseRemoveUploadedFileView]

  val formProvider: CancelCaseRemoveUploadedFileFormProvider =
    injector.instanceOf[CancelCaseRemoveUploadedFileFormProvider]

  val filename: String = "Filename"

  val index: Index = Index.apply(1)

  "Rendering the Cancel Case Remove Uploaded File page" when {
    "no errors exist" should {

      val form: Form[Boolean]              = formProvider.apply()
      lazy val view: Html                  =
        injectedView(form, index, filename, Call("GET", "backlink"), Call("GET", "submit"))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean]         = formProvider().bind(Map("value" -> ""))
      lazy val view: Html                  =
        injectedView(form, index, filename, Call("GET", "backlink"), Call("GET", "submit"))(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(errorPrefix + title)

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe errorPrefix + requiredError
      }

    }
  }

  it should {

    val form: Form[Boolean]              = formProvider.apply()
    lazy val view: Html                  =
      injectedView(form, index, filename, Call("GET", "backlink"), Call("GET", "submit"))(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '$h1'" in {
      elementText("h1") mustBe h1
    }

    s"have the correct filename in the body" in {
      elementText("p:nth-child(2)") mustBe filename
    }

    s"have the correct value for the first radio button of '$siteYes'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe siteYes
    }

    s"have the correct value for the second radio button of '$siteNo'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe siteNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "backlink")
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
