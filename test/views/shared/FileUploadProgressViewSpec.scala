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

package views.shared

import base.ViewBaseSpec
import messages.shared.FileUploadProgressMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.shared.FileUploadProgressView

class FileUploadProgressViewSpec extends ViewBaseSpec {

  private lazy val injectedView: FileUploadProgressView = app.injector.instanceOf[FileUploadProgressView]
  private val reference: String                         = "11370e18-6e24-453e-b45a-76d3e32ea33d"
  private val action: String                            = "action/url"

  "Rendering the Progress page" when {

    "called normally" should {
      "have the correct button link" in {
        lazy val view: Html                  = injectedView(reference, action)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementAttributes("#main-content .govuk-button").get("href").get mustBe action
      }
    }
  }

  it should {
    lazy val view: Html                  = injectedView(reference, action)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(FileUploadProgressMessages.title)

    s"have the correct h1 of '${FileUploadProgressMessages.title}'" in {
      elementText("h1") mustBe FileUploadProgressMessages.title
    }

    s"have the correct text of '${FileUploadProgressMessages.waiting}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe FileUploadProgressMessages.waiting
    }

    s"have the correct text of '${FileUploadProgressMessages.refresh}'" in {
      elementText("#main-content p:nth-of-type(2)") mustBe FileUploadProgressMessages.request
    }

    s"have the correct continue button text" in {
      elementText("#main-content .govuk-button") mustBe FileUploadProgressMessages.refresh
    }

  }
}
