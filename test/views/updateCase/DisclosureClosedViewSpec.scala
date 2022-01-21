/*
 * Copyright 2022 HM Revenue & Customs
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

package views.updateCase

import base.ViewBaseSpec
import messages.BaseMessages
import messages.updateCase.DisclosureClosedMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.updateCase.DisclosureClosedView

class DisclosureClosedViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: DisclosureClosedView = app.injector.instanceOf[DisclosureClosedView]

  val caseId: String = "C182107152124AQYVM6E34"

  "Rendering the Disclosure Closed page" when {
    "no errors exist" should {
      lazy val view: Html                  = injectedView(caseId)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(DisclosureClosedMessages.pageTitle)

      "it" should {
        lazy val view: Html                  = injectedView(caseId)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        s"have the correct page title of '${DisclosureClosedMessages.pageTitle}'" in {
          elementText("h1") mustBe DisclosureClosedMessages.pageTitle
        }

        s"have the correct page text of '${DisclosureClosedMessages.p1}'" in {
          elementText("#main-content p:nth-of-type(1)") mustBe DisclosureClosedMessages.p1
        }

        s"have the correct page text of '${DisclosureClosedMessages.p2}'" in {
          elementText("#main-content p:nth-of-type(2)") mustBe DisclosureClosedMessages.p2
        }

        s"have the correct page text of '${DisclosureClosedMessages.bullet1}'" in {
          elementText("#main-content li:nth-of-type(1)") mustBe DisclosureClosedMessages.bullet1
        }

        s"have the correct page text of '${DisclosureClosedMessages.bullet2}'" in {
          elementText("#main-content li:nth-of-type(2)") mustBe DisclosureClosedMessages.bullet2
        }

        s"have the correct page text of '${DisclosureClosedMessages.bullet3}'" in {
          elementText("#main-content li:nth-of-type(3)") mustBe DisclosureClosedMessages.bullet3
        }

        s"have the correct page text of '${DisclosureClosedMessages.bullet4}'" in {
          elementText("#main-content li:nth-of-type(4)") mustBe DisclosureClosedMessages.bullet4
        }

      }
    }
  }
}
