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

package views

import base.ViewBaseSpec
import messages.{BaseMessages, SupportingDocMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.SupportingDocView

class SupportingDocViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: SupportingDocView = app.injector.instanceOf[SupportingDocView]


  "Rendering the Supportdoc page" should {

      lazy val view: Html = injectedView()(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title of '${SupportingDocMessages.pageTitle}'" in {
        document.title mustBe SupportingDocMessages.pageTitle
      }

      s"have the correct page heading of '${SupportingDocMessages.heading}'" in {
        elementText("h1") mustBe SupportingDocMessages.heading
      }

      s"have the correct page text of '${SupportingDocMessages.disclosure}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe SupportingDocMessages.disclosure
      }

      s"have the correct page text of '${SupportingDocMessages.fileSize}'" in {
      elementText("#main-content p:nth-of-type(2)") mustBe SupportingDocMessages.fileSize
      }

//      s"have the correct page text of '${SupportingDocMessages.bullet1}'" in {
//      elementText("bullets(Seq(1))") mustBe SupportingDocMessages.bullet1
//      }
     }
   }