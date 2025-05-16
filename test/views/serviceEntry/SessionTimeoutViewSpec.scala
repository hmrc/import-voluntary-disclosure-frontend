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

package views.serviceEntry

import base.ViewBaseSpec
import messages.BaseMessages
import messages.serviceEntry.SessionTimeoutMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.errors.SessionTimeoutView

class SessionTimeoutViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: SessionTimeoutView = app.injector.instanceOf[SessionTimeoutView]

  "Rendering the SessionTimeoutView page" should {
    lazy val view: Html                  = injectedView()(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title of '${SessionTimeoutMessages.pageTitle}'" in {
      elementText("h1") mustBe SessionTimeoutMessages.pageTitle
    }
    s"have the correct page heading of '${SessionTimeoutMessages.pageTitle}'" in {
      elementText("h1") mustBe SessionTimeoutMessages.pageTitle
    }

    s"have the correct page text of '${SessionTimeoutMessages.p}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe SessionTimeoutMessages.p
    }

    "render a sign in button with the correct URL " in {
      elementAttributes(".govuk-button") must contain("href" -> appConfig.loginContinueUrl)
      elementText(".govuk-button") mustBe SessionTimeoutMessages.button
    }
  }

}
