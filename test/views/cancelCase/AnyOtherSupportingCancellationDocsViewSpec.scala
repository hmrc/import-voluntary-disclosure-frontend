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

package views.cancelCase

import base.ViewBaseSpec
import forms.cancelCase.AnyOtherSupportingCancellationDocsFormProvider
import messages.BaseMessages
import messages.cancelCase.AnyOtherSupportingCancellationDocsMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.cancelCase.AnyOtherSupportingCancellationDocsView

class AnyOtherSupportingCancellationDocsViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: AnyOtherSupportingCancellationDocsView =
    app.injector.instanceOf[AnyOtherSupportingCancellationDocsView]

  val formProvider: AnyOtherSupportingCancellationDocsFormProvider =
    injector.instanceOf[AnyOtherSupportingCancellationDocsFormProvider]

  val backLink: Call = Call("GET", "url")

  "Rendering the AnyOtherSupportCancellationDocs page" when {
    "no errors exist" should {
      val form: Form[Boolean]              = formProvider.apply()
      lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(AnyOtherSupportingCancellationDocsMessages.pageTitle)

      "it" should {
        val form: Form[Boolean]              = formProvider.apply()
        lazy val view: Html                  = injectedView(form, backLink)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        s"have the correct page heading of '${AnyOtherSupportingCancellationDocsMessages.heading}'" in {
          elementText("h1") mustBe AnyOtherSupportingCancellationDocsMessages.heading
        }

        s"have the correct value for the first radio button of '${AnyOtherSupportingCancellationDocsMessages.yes}'" in {
          elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label") mustBe
            AnyOtherSupportingCancellationDocsMessages.yes
        }

        s"have the correct value for the second radio button of '${AnyOtherSupportingCancellationDocsMessages.no}'" in {
          elementText(
            "#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label"
          ) mustBe AnyOtherSupportingCancellationDocsMessages.no
        }

        "render a back link with the correct URL" in {
          elementAttributes("#back-link") must contain("href" -> "url")
        }

        s"have the correct Continue button" in {
          elementText(".govuk-button") mustBe continue
        }
      }
    }
  }
}
