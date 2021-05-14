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
import messages.{BaseMessages, UnderpaymentStartMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.underpayments.UnderpaymentStartView

class UnderpaymentStartViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnderpaymentStartView = app.injector.instanceOf[UnderpaymentStartView]


  "Rendering the Underpayment start page" when {
    "no errors exist" should {
      lazy val view: Html = injectedView(controllers.routes.EnterCustomsProcedureCodeController.onLoad(), true, true)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title of '${UnderpaymentStartMessages.pageTitle}'" in {
        document.title mustBe UnderpaymentStartMessages.pageTitle
      }
    }

    "when in change mode back button" should {
      lazy val view: Html = injectedView(controllers.routes.EnterCustomsProcedureCodeController.onLoad(), true, false)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not be displayed" in {
        document.getElementsByClass("govuk-back-link").size() mustBe 0
      }
    }

  }

  "Dynamic bullet points depending on one entry or bulk" when {
    "one entry" should {
      lazy val view: Html = injectedView(controllers.routes.EnterCustomsProcedureCodeController.onLoad(), true, true)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

          s"have the correct page text of '${UnderpaymentStartMessages.oneEntryBullet1}'" in {
            elementText("#main-content li:nth-of-type(1)") mustBe UnderpaymentStartMessages.oneEntryBullet1
          }

          s"have the correct page text of '${UnderpaymentStartMessages.oneEntryBullet2}'" in {
            elementText("#main-content li:nth-of-type(2)") mustBe UnderpaymentStartMessages.oneEntryBullet2
          }
    }

    "bulk entry" should {
      lazy val view: Html = injectedView(controllers.routes.EnterCustomsProcedureCodeController.onLoad(), false, true)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page text of '${UnderpaymentStartMessages.bulkBullet1}'" in {
        elementText("#main-content li:nth-of-type(1)") mustBe UnderpaymentStartMessages.bulkBullet1
      }

      s"have the correct page text of '${UnderpaymentStartMessages.bulkBullet2}'" in {
        elementText("#main-content li:nth-of-type(2)") mustBe UnderpaymentStartMessages.bulkBullet2
      }
    }

  }

  "it" should {
    lazy val view: Html = injectedView(controllers.routes.EnterCustomsProcedureCodeController.onLoad(), true, true)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)
    s"have the correct page heading of '${UnderpaymentStartMessages.heading}'" in {
      elementText("h1") mustBe UnderpaymentStartMessages.heading
    }

    s"have the correct page text of '${UnderpaymentStartMessages.p1}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UnderpaymentStartMessages.p1
    }

    "render a continue button with the correct URL " in {
      elementAttributes(".govuk-button") must contain("href" -> controllers.underpayments.routes.UnderpaymentTypeController.onLoad().url)
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.EnterCustomsProcedureCodeController.onLoad().url)
    }
  }
}
