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
import messages.FileUploadProgressMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.shared.FileUploadProgressView

class FileUploadProgressViewSpec extends ViewBaseSpec {
  private lazy val injectedView: FileUploadProgressView = app.injector.instanceOf[FileUploadProgressView]

  "Rendering the Progress page" when {
    lazy val view: Html                  = injectedView()(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "called normally" should {
      checkPageTitle(FileUploadProgressMessages.title)

      s"have the correct h1 of '${FileUploadProgressMessages.h1}'" in {
        elementText("h1") mustBe FileUploadProgressMessages.h1
      }

      s"have the loading spinner" in {
        element(".ccms-loader").tagName mustBe "div"
      }

      s"have the auto-refresh element" in {
        element("head > meta[http-equiv=\"refresh\"]").attr("content") mustBe "3"
      }
    }
  }
}
