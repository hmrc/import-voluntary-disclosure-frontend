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

package views.components

import base.ViewBaseSpec
import messages.BaseMessages
import mocks.config.MockAppConfig.betaFeedbackUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.components.phaseBanner

class PhaseBannerViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val target: phaseBanner = app.injector.instanceOf[phaseBanner]

  "Rendering the phase banner" should {
    s"have the feedback url" in {

      lazy val markup: Html = target()
      lazy implicit val document: Document = Jsoup.parse(markup.toString)

      element("a").attr("href").contains(betaFeedbackUrl) mustBe true
      }
    }

  it should {
    lazy val markup: Html = target()
    lazy implicit val document: Document = Jsoup.parse(markup.toString)

    "render the correct banner text" in {
      elementText("p") mustBe "BETA This is a new service – your feedback will help us to improve it."
      elementText("a") mustBe "feedback"
    }

    "have the correct GDS CSS class" in {
      elementAttributes("a") must contain("class" -> "govuk-link")
    }
  }
}
