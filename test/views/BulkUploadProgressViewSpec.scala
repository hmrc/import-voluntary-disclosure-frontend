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
import messages.BulkUploadProgressMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.ProgressView

class BulkUploadProgressViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ProgressView = app.injector.instanceOf[ProgressView]
  private val reference: String = "11370e18-6e24-453e-b45a-76d3e32ea33d"
  private val backLink: Call = Call("GET", "url")

  "Rendering the Progress page" when {

    "called from the Bulk Upload File page" should {
      "have the correct button link" in {
        lazy val view: Html = injectedView(reference, backLink, controllers.routes.BulkUploadFileController.onLoad().url)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementAttributes("#main-content .govuk-button").get("href").get mustBe
          controllers.routes.BulkUploadFileController.uploadProgress(reference).url
      }
    }

    "called from the Upload File page" should {
      "have the correct button link" in {
        lazy val view: Html = injectedView(reference, backLink, controllers.routes.UploadFileController.onLoad().url)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementAttributes("#main-content .govuk-button").get("href").get mustBe
          controllers.routes.UploadFileController.uploadProgress(reference).url
      }
    }

    "called from the Upload Authority File page" should {
      "have the correct button link" in {
        lazy val view: Html = injectedView(reference, backLink, controllers.routes.UploadAuthorityController.onLoad().url)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementAttributes("#main-content .govuk-button").get("href").get mustBe
          controllers.routes.UploadAuthorityController.uploadProgress(reference,).url
      }
    }
  }

  it should {
    lazy val view: Html = injectedView(reference, backLink)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(BulkUploadProgressMessages.title)

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct h1 of '${BulkUploadProgressMessages.h1}'" in {
      elementText("h1") mustBe BulkUploadProgressMessages.h1
    }

    s"have the correct text of '${BulkUploadProgressMessages.waiting}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe BulkUploadProgressMessages.waiting
    }

    s"have the correct continue button text" in {
      elementText("#main-content .govuk-button") mustBe BulkUploadProgressMessages.continueButton
    }

  }
}
