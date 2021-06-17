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
import messages.BulkUploadSuccessMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.BulkUploadSuccessView

class BulkUploadSuccessViewSpec extends ViewBaseSpec {

  private lazy val injectedView: BulkUploadSuccessView = app.injector.instanceOf[BulkUploadSuccessView]
  private val filename: String = BulkUploadSuccessMessages.filename
  private val action: String = "action/url"

  "Rendering the Bulk Upload success page" when {
    lazy val view: Html = injectedView(filename, action)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "called normally" should {
      "have the correct button link" in {
        elementAttributes("#main-content .govuk-button").get("href").get mustBe action
      }
    }
  }

  it should {
    lazy val view: Html = injectedView(filename, action)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(BulkUploadSuccessMessages.title)

    "not have a back link" in {
      elementExtinct("#back-link")
    }

    s"have the correct h1 of '${BulkUploadSuccessMessages.h1}'" in {
      elementText("h1") mustBe BulkUploadSuccessMessages.h1
    }

    s"have the correct text of '${BulkUploadSuccessMessages.bodyText}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe BulkUploadSuccessMessages.bodyText
    }

    s"have the correct Continue button text" in {
      elementText("#main-content .govuk-button") mustBe BulkUploadSuccessMessages.continue
    }

  }
}
