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

package views.underpayments

import base.ViewBaseSpec
import messages.underpayments.PVAHandoffMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.reasons.PVAHandoffView

class PVAHandoffViewSpec extends ViewBaseSpec {

  private lazy val injectedView: PVAHandoffView = app.injector.instanceOf[PVAHandoffView]
  private val importerName: String              = "fewfew"

  "Rendering the Cannot Disclose Import VAT page" when {

    "importer flow" should {
      lazy val view: Html                  = injectedView(isRepFlow = false, importerName)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)
      s"have the paragraph of '${PVAHandoffMessages.importerMessage}'" in {
        elementText(
          "#main-content > div > div > p:nth-child(2)"
        ) mustBe PVAHandoffMessages.importerMessage
      }
    }

    "rep flow" should {
      lazy val view: Html                  = injectedView(isRepFlow = true, importerName)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)
      s"have the paragraph of '${PVAHandoffMessages.repMessage}'" in {
        elementText(
          "#main-content > div > div > p:nth-child(2)"
        ) mustBe PVAHandoffMessages.repMessage
      }
    }

  }

  "it" should {
    lazy val view: Html                  = injectedView(isRepFlow = true, importerName)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(PVAHandoffMessages.title)

    s"have the correct page heading of '${PVAHandoffMessages.title}'" in {
      elementText("h1") mustBe PVAHandoffMessages.title
    }

    s"have the correct value for '${PVAHandoffMessages.findOut}'" in {
      elementText("#main-content > div > div > p:nth-child(3)") mustBe PVAHandoffMessages.findOut
    }

    s"have the '${PVAHandoffMessages.findOut}' link" in {
      elementAttributes("#accountForImportVAT").get("href") mustBe
        Some("https://www.gov.uk/guidance/complete-your-vat-return-to-account-for-import-vat")
    }

    s"have the exit survey content" in {
      document.getElementsByClass("govuk-heading-m").text() must include(
        PVAHandoffMessages.exitSurveyHeading
      )
      document.getElementsByClass("govuk-body").text() must include(
        PVAHandoffMessages.exitSurveyParagraph
      )
      document.getElementsByClass("govuk-body").text() must include(PVAHandoffMessages.exitSurveyLink)
      document.getElementsByClass("govuk-body").text() must include(
        PVAHandoffMessages.exitSurveyParagraph2
      )
    }

  }
}
