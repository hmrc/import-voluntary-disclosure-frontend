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

package views.docUpload

import base.ViewBaseSpec
import forms.docUpload.OptionalSupportingDocsFormProvider
import messages.BaseMessages
import messages.docUpload.OptionalDocumentsMessages
import models.OptionalDocument
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.docUpload.OptionalSupportingDocsView

class OptionalSupportingDocsViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: OptionalSupportingDocsView = app.injector.instanceOf[OptionalSupportingDocsView]

  val formProvider: OptionalSupportingDocsFormProvider = injector.instanceOf[OptionalSupportingDocsFormProvider]

  val backLink: Call = controllers.docUpload.routes.AnyOtherSupportingDocsController.onLoad()

  "Rendering the Optional Supporting Documents page" when {

    "no errors exist" should {

      val form: Form[Seq[OptionalDocument]] = formProvider.apply()
      lazy val view: Html                   = injectedView(form, backLink, Seq.empty)(fakeRequest, messages)
      lazy implicit val document: Document  = Jsoup.parse(view.body)

      checkPageTitle(OptionalDocumentsMessages.pageTitle)

      s"have the correct page header" in {
        elementText("h1") mustBe OptionalDocumentsMessages.pageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "error exist when no option selected exist" should {

      val form: Form[Seq[OptionalDocument]] = formProvider().bind(Map("" -> ""))
      lazy val view: Html                   = injectedView(form, backLink, Seq.empty)(fakeRequest, messages)
      lazy implicit val document: Document  = Jsoup.parse(view.body)

      checkPageTitle(errorPrefix + OptionalDocumentsMessages.pageTitle)

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe thereIsAProblemPrefix + OptionalDocumentsMessages.errorRequired
      }

      "render an error message against the field" in {
        elementText("#optionalDocumentsList-error") mustBe errorPrefix + OptionalDocumentsMessages.errorRequired
      }
    }

  }

  it should {

    val form: Form[Seq[OptionalDocument]] = formProvider.apply()
    lazy val view: Html                   = injectedView(form, backLink, Seq.empty)(fakeRequest, messages)
    lazy implicit val document: Document  = Jsoup.parse(view.body)

    s"have the correct hint" in {
      elementText("#optionalDocumentsList-hint") mustBe OptionalDocumentsMessages.hint
    }
    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

    s"the checkbox for importAndEntry is rendered" in {
      elementText(".govuk-checkboxes__label[for=importAndEntry]") mustBe OptionalDocumentsMessages.importAndEntry
    }

    s"the checkbox for airwayBill is rendered" in {
      elementText(".govuk-checkboxes__label[for=airwayBill]") mustBe OptionalDocumentsMessages.airwayBill
    }

    s"the checkbox for originProof is rendered" in {
      elementText(".govuk-checkboxes__label[for=originProof]") mustBe OptionalDocumentsMessages.originProof
    }

    s"the checkbox for other is rendered" in {
      elementText(".govuk-checkboxes__label[for=other]") mustBe OptionalDocumentsMessages.other
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }

}
