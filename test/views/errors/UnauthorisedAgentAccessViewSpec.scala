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
import messages.errors.UnauthorisedAgentAccessMessages._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.errors.UnauthorisedAgentAccessView

class UnauthorisedAgentAccessViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UnauthorisedAgentAccessView = app.injector.instanceOf[UnauthorisedAgentAccessView]

  "Rendering the UnauthorisedAgentAccess page" should {
    lazy val view: Html                  = injectedView()(fakeRequest, messages, appConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(pageTitle)

    s"have the correct page heading" in {
      elementText("h1") mustBe pageTitle
    }

    s"have the correct first paragraph of text" in {
      elementText("#main-content > div > div > p:nth-child(2)") mustBe para1
    }

    s"have the correct sign in again link text" in {
      elementText("#main-content > div > div > p:nth-child(3)") mustBe signInAgain1 + signInAgain2
    }

    "have the correct sign-out link" in {
      elementAttributes("#main-content > div > div > p:nth-child(3) > a").get("href") mustBe Some(appConfig.signOutUrl)
    }

    s"have the correct bullet one text" in {
      elementText("#main-content > div > div > ul > li:nth-child(1)") mustBe bullet1
    }

    s"have the correct bullet two text" in {
      elementText("#main-content > div > div > ul > li:nth-child(2)") mustBe bullet2
    }

    s"have the correct details link text" in {
      elementText("#main-content > div > div > details > summary > span") mustBe detailsLinkText
    }

    s"have the correct details p1 text" in {
      elementText("#main-content > div > div > details > div") mustBe details1 + " " + details2
    }

  }
}
