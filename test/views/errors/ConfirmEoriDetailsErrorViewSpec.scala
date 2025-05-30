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

package views.errors

import base.ViewBaseSpec
import messages.BaseMessages
import messages.errors.ConfirmEoriDetailsErrorMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.errors.ConfirmEoriDetailsErrorView

class ConfirmEoriDetailsErrorViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ConfirmEoriDetailsErrorView = app.injector.instanceOf[ConfirmEoriDetailsErrorView]

  "Rendering the ConfirmEoriDetailsErrorView page" should {
    lazy val view: Html                  = injectedView()(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(ConfirmEoriDetailsErrorMessages.title)

    s"have the correct page heading" in {
      elementText("h1") mustBe ConfirmEoriDetailsErrorMessages.title
    }

    s"have the correct instructions" in {
      elementText("#main-content p") mustBe ConfirmEoriDetailsErrorMessages.message
    }
  }
}
