/*
 * Copyright 2023 HM Revenue & Customs
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

package views.cancelCase

import base.ViewBaseSpec
import messages.BaseMessages
import messages.cancelCase.CancelCaseDisclosureClosedMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.cancelCase.CancelCaseDisclosureClosedView

class CancelCaseDisclosureClosedViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: CancelCaseDisclosureClosedView =
    app.injector.instanceOf[CancelCaseDisclosureClosedView]

  val caseId: String = "C182107152124AQYVM6E34"

  "Rendering the Disclosure Closed page" when {
    "no errors exist" should {
      lazy val view: Html                  = injectedView(caseId)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(CancelCaseDisclosureClosedMessages.pageTitle)

      "it" should {
        lazy val view: Html                  = injectedView(caseId)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        s"have the correct page title of '${CancelCaseDisclosureClosedMessages.pageTitle}'" in {
          elementText("h1") mustBe CancelCaseDisclosureClosedMessages.pageTitle
        }

        s"have the correct page text of '${CancelCaseDisclosureClosedMessages.p1}'" in {
          elementText("#main-content p:nth-of-type(1)") mustBe CancelCaseDisclosureClosedMessages.p1
        }

        s"have the correct page text of '${CancelCaseDisclosureClosedMessages.p2}'" in {
          elementText("#main-content p:nth-of-type(2)") mustBe CancelCaseDisclosureClosedMessages.p2
        }

        s"have the correct mail link" in {
          elementAttributes("#main-content p:nth-of-type(2) a").get("href") mustBe
            Some(s"mailto:${appConfig.c18EmailAddress}")
        }

        s"have the correct bullet list" in {
          elementText("#main-content li:nth-of-type(1)") mustBe CancelCaseDisclosureClosedMessages.li1(caseId)
          elementText("#main-content li:nth-of-type(2)") mustBe CancelCaseDisclosureClosedMessages.li2
          elementText("#main-content li:nth-of-type(3)") mustBe CancelCaseDisclosureClosedMessages.li3
          elementText("#main-content li:nth-of-type(4)") mustBe CancelCaseDisclosureClosedMessages.li4
        }
      }
    }
  }
}
