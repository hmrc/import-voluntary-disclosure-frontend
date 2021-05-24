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
import messages.errors.UnauthorisedAgentAccessMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.errors.UnauthorisedAgentAccessView

class UnauthorisedAgentAccessViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnauthorisedAgentAccessView = app.injector.instanceOf[UnauthorisedAgentAccessView]

  "Rendering the UnauthorisedAgentAccess page" should {
    lazy val view: Html = injectedView()(fakeRequest, messages, appConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(UnauthorisedAgentAccessMessages.title)

    s"have the correct page heading" in {
      elementText("h1") mustBe UnauthorisedAgentAccessMessages.title
    }

    s"have the correct first paragraph of text" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UnauthorisedAgentAccessMessages.para1
    }

    s"have the correct second paragraph of text" in {
      elementText("#main-content p:nth-of-type(2)") mustBe UnauthorisedAgentAccessMessages.para2
    }

    "have the correct sign-out link" in {
      elementAttributes("p > a").get("href") mustBe Some(appConfig.signOutUrl)
    }
  }
}
