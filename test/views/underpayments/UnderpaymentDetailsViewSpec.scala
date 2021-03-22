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
import messages.BaseMessages
import messages.underpayments.UnderpaymentDetailsMessages
import models.underpayments.UnderpaymentAmount
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.underpayments.UnderpaymentDetailsView

class UnderpaymentDetailsViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnderpaymentDetailsView = app.injector.instanceOf[UnderpaymentDetailsView]

  val formProvider: UnderpaymentDetailsFormProvider = injector.instanceOf[UnderpaymentDetailsFormProvider]

  private lazy val backLink = controllers.underpayments.routes.UnderpaymentTypeController.onLoad()


  "The Underpayment Reason Amendment page" when {
    Seq("B00", "A00", "E00", "A20", "A30", "A35", "A40", "A45", "A10", "D10").foreach { testType =>
      checkContent(testType)
    }

    def checkContent(underpaymentType: String): Unit = {
      s"rendered for type $underpaymentType" should {
        val form: Form[UnderpaymentAmount] = formProvider.apply()
        lazy val view: Html = injectedView(
          form, underpaymentType, backLink
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct page title" in {
          document.title mustBe UnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).title
        }

        "have the correct page heading" in {
          elementText("h1") mustBe UnderpaymentDetailsMessages.underpaymentTypeContent(underpaymentType).heading
        }

      }
    }

  }

  it should {

    lazy val form: Form[UnderpaymentAmount] = formProvider.apply()
    lazy val view: Html = injectedView(
      form, "A00", backLink
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have correct legend for the original amount" in {
      elementText("#original-fieldset-legend") mustBe UnderpaymentDetailsMessages.originalAmount
    }

    s"have correct legend for the amended amount" in {
      elementText("#amended-fieldset-legend") mustBe UnderpaymentDetailsMessages.amendedAmount
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> backLink.url)
    }

  }

}
