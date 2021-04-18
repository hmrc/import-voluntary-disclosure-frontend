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
import forms.underpayments.UnderpaymentDetailsFormProvider
import messages.underpayments.ChangeUnderpaymentDetailsMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.underpayments.ChangeUnderpaymentDetailsView

class ChangeUnderpaymentDetailsViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ChangeUnderpaymentDetailsView = app.injector.instanceOf[ChangeUnderpaymentDetailsView]

  private val backLink: Call = Call("GET", "url")

  val underpaymentType = "B00"

  val formProvider: UnderpaymentDetailsFormProvider = injector.instanceOf[UnderpaymentDetailsFormProvider]


  "Rendering the ChangeUnderpaymentDetailsView page" when {
    "no errors exist" should {
      val form = formProvider.apply()
      lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe ChangeUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
      }

      "have correct heading" in {
        document.select("h1").text mustBe ChangeUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have correct legend for the original amount" in {
        elementText("#main-content > div > div > form > div:nth-child(2) > label") mustBe ChangeUnderpaymentDetailsMessages.originalValue
      }

      s"have correct legend for the amended amount" in {
        elementText("#main-content > div > div > form > div:nth-child(3) > label") mustBe ChangeUnderpaymentDetailsMessages.amendedValue
      }
    }

    "an error exists" should {
      lazy val form = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form, underpaymentType, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe ChangeUnderpaymentDetailsMessages.errorPrefix + ChangeUnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
      }

      // TODO: test error messages (these can be found in the underpaymentDetailsMessages file)
    }


  }
  it should {

    // TODO: test dynamic remove link messages (these need adding in the ChangeUnderpaymentDetailsMessages)


  }
}
