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

package views.errors

import base.ViewBaseSpec
import messages.BaseMessages
import messages.errors.UnauthorisedPrivateBetaAccessMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.errors.UnauthorisedPrivateBetaAccessView

class UnauthorisedPrivateBetaAccessViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnauthorisedPrivateBetaAccessView =
    app.injector.instanceOf[UnauthorisedPrivateBetaAccessView]

  "Rendering the UnauthorisedPrivateBetaAccessView page" should {
    lazy val view: Html                  = injectedView()(fakeRequest, messages, appConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(UnauthorisedPrivateBetaAccessMessages.title)

    s"have the correct page heading" in {
      elementText("h1") mustBe UnauthorisedPrivateBetaAccessMessages.title
    }

    s"have the correct first paragraph of text" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UnauthorisedPrivateBetaAccessMessages.para1
    }

    s"have the correct second paragraph of text" in {
      elementText(
        "#main-content p:nth-of-type(2)"
      ) mustBe s"${UnauthorisedPrivateBetaAccessMessages.para2_1}${UnauthorisedPrivateBetaAccessMessages.para2_2}"
    }

    s"have the correct link in the second paragraph of text" in {
      elementText("#main-content p:nth-of-type(2) a") mustBe UnauthorisedPrivateBetaAccessMessages.para2_2
    }

    "have the correct sign-out link" in {
      elementAttributes("p > a").get("href") mustBe Some(appConfig.c2001Url)
    }
  }
}
