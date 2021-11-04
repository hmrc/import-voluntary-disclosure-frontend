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

package views.shared

import base.ViewBaseSpec
import messages.shared.FileUploadSuccessMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.shared.FileUploadSuccessView

class FileUploadSuccessViewSpec extends ViewBaseSpec {

  private lazy val injectedView: FileUploadSuccessView = app.injector.instanceOf[FileUploadSuccessView]
  private val filename: String                         = FileUploadSuccessMessages.filename
  private val action: String                           = "action/url"

  "Rendering the success page" when {
    lazy val view: Html                  = injectedView(filename, action)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)
    "called normally" should {
      "have the correct button link" in {
        elementAttributes("#main-content .govuk-button").get("href").get mustBe action
      }
    }
  }

  it should {
    lazy val view: Html                  = injectedView(filename, action)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(FileUploadSuccessMessages.title)

    "not have a back link" in {
      elementExtinct("#back-link")
    }

    s"have the correct h1 of '${FileUploadSuccessMessages.title}'" in {
      elementText("h1") mustBe FileUploadSuccessMessages.title
    }

    s"have the correct text of '${FileUploadSuccessMessages.bodyText}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe FileUploadSuccessMessages.bodyText
    }

    s"have the correct Continue button text" in {
      elementText("#main-content .govuk-button") mustBe FileUploadSuccessMessages.continue
    }

  }
}
