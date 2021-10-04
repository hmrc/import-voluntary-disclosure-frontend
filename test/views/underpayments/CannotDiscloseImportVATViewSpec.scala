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

package views.underpayments

import base.ViewBaseSpec
import messages.underpayments.CannotDiscloseImportVATMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.reasons.CannotDiscloseImportVATView

class CannotDiscloseImportVATViewSpec extends ViewBaseSpec {

  private lazy val injectedView: CannotDiscloseImportVATView = app.injector.instanceOf[CannotDiscloseImportVATView]
  private val importerName: Option[String]                   = Some("fewfew")

  "Rendering the Cannot Disclose Import VAT page" when {

    "importer flow" should {
      lazy val view: Html                  = injectedView(isRepFlow = false, importerName)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)
      s"have the paragraph of '${CannotDiscloseImportVATMessages.importerMessage}'" in {
        elementText(
          "#main-content > div > div > p:nth-child(2)"
        ) mustBe CannotDiscloseImportVATMessages.importerMessage
      }
    }

    "rep flow" should {
      lazy val view: Html                  = injectedView(isRepFlow = true, importerName)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)
      s"have the paragraph of '${CannotDiscloseImportVATMessages.repMessage}'" in {
        elementText(
          "#main-content > div > div > p:nth-child(2)"
        ) mustBe CannotDiscloseImportVATMessages.repMessage
      }
    }

  }

  "it" should {
    lazy val view: Html                  = injectedView(isRepFlow = true, importerName)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(CannotDiscloseImportVATMessages.title)

    s"have the correct page heading of '${CannotDiscloseImportVATMessages.title}'" in {
      elementText("h1") mustBe CannotDiscloseImportVATMessages.title
    }

    s"have the correct value for '${CannotDiscloseImportVATMessages.findOut}'" in {
      elementText("#main-content > div > div > p:nth-child(3)") mustBe CannotDiscloseImportVATMessages.findOut
    }

  }
}
