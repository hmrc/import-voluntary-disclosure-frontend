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
import forms.underpayments.RemoveUnderpaymentDetailsFormProvider
import messages.RemoveUnderpaymentReasonMessages
import messages.underpayments.RemoveUnderpaymentDetailsMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.underpayments.RemoveUnderpaymentDetailsView

class RemoveUnderpaymentDetailsViewSpec extends ViewBaseSpec {

  private lazy val injectedView: RemoveUnderpaymentDetailsView = app.injector.instanceOf[RemoveUnderpaymentDetailsView]

  val underpaymentType = "B00"

  val backLink: Call = controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType)

  val formProvider: RemoveUnderpaymentDetailsFormProvider = injector.instanceOf[RemoveUnderpaymentDetailsFormProvider]

  "Rendering the Remove Underpayment Details page" when {

    "no errors exist" should {
      val form: Form[Boolean] = formProvider.apply(underpaymentType)
      lazy val view: Html = injectedView(
        form,
        underpaymentType,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe RemoveUnderpaymentDetailsMessages.title
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists" should {
      lazy val form: Form[Boolean] = formProvider(underpaymentType).bind(Map("value" -> ""))
      lazy val view: Html = injectedView(
        form,
        underpaymentType,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "update the page title to include the error prefix" in {
        document.title mustBe RemoveUnderpaymentDetailsMessages.errorPrefix + RemoveUnderpaymentDetailsMessages.title
      }

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe RemoveUnderpaymentDetailsMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe RemoveUnderpaymentDetailsMessages.errorPrefix + RemoveUnderpaymentDetailsMessages.requiredError
      }
    }

  }

  it should {

    val form: Form[Boolean] = formProvider.apply(underpaymentType)
    lazy val view: Html = injectedView(
      form,
      underpaymentType,
      backLink
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct value for the first radio button of '${RemoveUnderpaymentReasonMessages.radioYes}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1) > label") mustBe RemoveUnderpaymentDetailsMessages.radioYes
    }

    s"have the correct value for the second radio button of '${RemoveUnderpaymentReasonMessages.radioNo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2) > label") mustBe RemoveUnderpaymentDetailsMessages.radioNo
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe RemoveUnderpaymentDetailsMessages.continue
    }

  }
}
