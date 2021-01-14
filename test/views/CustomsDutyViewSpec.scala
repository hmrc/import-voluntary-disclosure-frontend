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
import forms.CustomsDutyFormProvider
import messages.{BaseMessages, CustomsDutyMessages, EntryDetailsMessages}
import models.UnderpaymentAmount
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.CustomsDutyView

class CustomsDutyViewSpec extends ViewBaseSpec with BaseMessages {


  private lazy val injectedView: CustomsDutyView = app.injector.instanceOf[CustomsDutyView]

  val formProvider: CustomsDutyFormProvider = injector.instanceOf[CustomsDutyFormProvider]

  "Rendering the UnderpaymentType page" when {

    "no errors exist" should {
      val form: Form[UnderpaymentAmount] = formProvider.apply()
      lazy val view: Html = injectedView(
        form
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe CustomsDutyMessages.pageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no value has been specified for original amount)" should {
      lazy val form: Form[UnderpaymentAmount] = formProvider().bind(Map("original" -> "", "amended" -> "50"))
      lazy val view: Html = injectedView(
        form
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe  CustomsDutyMessages.errorPrefix + CustomsDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(".govuk-error-summary__list") mustBe CustomsDutyMessages.originalNonEmpty
      }

      // probs need to add ID for the error next to field rendering
//      "render an error message against the field" in {
//        elementText("#value-error") mustBe CustomsDutyMessages.errorPrefix + CustomsDutyMessages.originalNonEmpty
//      }
    }

    "an error exists (no value has been specified for amended amount)" should {
      lazy val form: Form[UnderpaymentAmount] = formProvider().bind(Map("original" -> "50", "amended" -> ""))
      lazy val view: Html = injectedView(
        form
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe  CustomsDutyMessages.errorPrefix + CustomsDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(".govuk-error-summary__list") mustBe CustomsDutyMessages.amendedNonEmpty
      }

      // probs need to add ID for the error next to field rendering
//      "render an error message against the field" in {
//        elementText("#value-error") mustBe CustomsDutyMessages.errorPrefix + CustomsDutyMessages.amendedNonEmpty
//      }
    }

    "an error exists (not a numeric value has been specified for original amount)" should {
      lazy val form: Form[UnderpaymentAmount] = formProvider().bind(Map("original" -> "a!@$", "amended" -> "50"))
      lazy val view: Html = injectedView(
        form
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe  CustomsDutyMessages.errorPrefix + CustomsDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(".govuk-error-summary__list") mustBe CustomsDutyMessages.originalNonNumber
      }

      // probs need to add ID for the error next to field rendering
      //      "render an error message against the field" in {
      //        elementText("#value-error") mustBe CustomsDutyMessages.errorPrefix + CustomsDutyMessages.originalNonNumber
      //      }
    }

    "an error exists (not a numeric value has been specified for amended amount)" should {
      lazy val form: Form[UnderpaymentAmount] = formProvider().bind(Map("original" -> "50", "amended" -> "a!@$"))
      lazy val view: Html = injectedView(
        form
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe  CustomsDutyMessages.errorPrefix + CustomsDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(".govuk-error-summary__list") mustBe CustomsDutyMessages.amendedNonNumber
      }

      // probs need to add ID for the error next to field rendering
      //      "render an error message against the field" in {
      //        elementText("#value-error") mustBe CustomsDutyMessages.errorPrefix + CustomsDutyMessages.amendedNonNumber
      //      }
    }

    "an error exists (the value for original amount exceeds the limit)" should {
      lazy val form: Form[UnderpaymentAmount] = formProvider().bind(Map("original" -> "10000000000", "amended" -> "50"))
      lazy val view: Html = injectedView(
        form
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe  CustomsDutyMessages.errorPrefix + CustomsDutyMessages.pageTitle
      }

      "render an error summary with the correct message " in {
        elementText(".govuk-error-summary__list") mustBe CustomsDutyMessages.originalUpperLimit
      }

      // probs need to add ID for the error next to field rendering
      //      "render an error message against the field" in {
      //        elementText("#value-error") mustBe CustomsDutyMessages.errorPrefix + CustomsDutyMessages.originalUpperLimit
      //      }
    }

  }

    it should {

    lazy val form: Form[UnderpaymentAmount] = formProvider()
    lazy val view: Html = injectedView(form)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page title of '${CustomsDutyMessages.pageTitle}'" in {
      document.title mustBe CustomsDutyMessages.pageTitle
    }

    s"have the correct h1 of '${CustomsDutyMessages.pageHeader}'" in {
      elementText("h1") mustBe CustomsDutyMessages.pageHeader
    }

    s"have correct legend for the original amount" in {
      elementText("#original-fieldset-legend") mustBe CustomsDutyMessages.originalAmount
    }

    s"have correct legend for the amended amount" in {
      elementText("#amended-fieldset-legend") mustBe CustomsDutyMessages.amendedAmount
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> controllers.routes.UnderpaymentTypeController.onLoad().url)
    }

  }

}
