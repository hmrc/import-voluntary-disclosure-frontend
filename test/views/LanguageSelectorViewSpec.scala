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

package views

import base.ViewBaseSpec
import messages.{BaseMessages, LanguageSelector}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.components.languageSelector

class LanguageSelectorViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val target: languageSelector = app.injector.instanceOf[languageSelector]

  "Rendering the language toggle" should {
    "have a toggle for Welsh" in {
      lazy val markup: Html                = target()(messages)
      lazy implicit val document: Document = Jsoup.parse(markup.toString)

      element("ul > li:nth-child(2) > a").attr("href") mustBe "/disclose-import-taxes-underpayment/language/cy"
    }
  }

  it should {
    lazy val markup: Html                = target()(messages)
    lazy implicit val document: Document = Jsoup.parse(markup.toString)

    "render the correct text" in {
      elementText("span:nth-child(1)") mustBe LanguageSelector.en
      elementText("span:nth-child(2)") mustBe LanguageSelector.cy
    }
  }
}
