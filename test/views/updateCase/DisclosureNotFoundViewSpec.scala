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

package views.updateCase

import base.ViewBaseSpec
import messages.{BaseMessages, DisclosureNotFoundMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.updateCase.DisclosureNotFoundView

class DisclosureNotFoundViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: DisclosureNotFoundView = app.injector.instanceOf[DisclosureNotFoundView]
  private val caseId                                    = "C181234567890123456789"

  "DisclosureNotFound page" should {
    lazy val view: Html                  = injectedView(caseId)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(DisclosureNotFoundMessages.pageTitle)

    "it" should {
      lazy val view: Html                  = injectedView(caseId)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)
      s"have the correct page heading of '${DisclosureNotFoundMessages.pageTitle}'" in {
        elementText("h1") mustBe DisclosureNotFoundMessages.pageTitle
      }

      s"have the correct page text of '${DisclosureNotFoundMessages.p1(caseId)}'" in {
        elementText("#main-content p:nth-of-type(1)") mustBe DisclosureNotFoundMessages.p1(caseId)
      }

      s"have the correct page text of '${DisclosureNotFoundMessages.p2}'" in {
        elementText("#main-content p:nth-of-type(2)") mustBe DisclosureNotFoundMessages.p2
      }

      s"have the correct list item of '${DisclosureNotFoundMessages.li1}'" in {
        elementText("#main-content li:nth-of-type(1)") mustBe DisclosureNotFoundMessages.li1
      }

      s"have the correct link to DisclosureReferenceNumber page" in {
        elementAttributes("#main-content li:nth-of-type(1) a").get("href") mustBe
          Some(controllers.updateCase.routes.DisclosureReferenceNumberController.onLoad().url)
      }

      s"have the correct list item of '${DisclosureNotFoundMessages.li2}'" in {
        elementText("#main-content li:nth-of-type(2)") mustBe DisclosureNotFoundMessages.li2
      }

      s"have the correct mail link" in {
        elementAttributes("#main-content li:nth-of-type(2) a").get("href") mustBe
          Some("mailto:npcc@hmrc.gov.uk")
      }
    }
  }
}
