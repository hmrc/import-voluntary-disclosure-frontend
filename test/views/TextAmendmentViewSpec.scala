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
import forms.UnderpaymentReasonAmendmentFormProvider
import messages.{AmendReasonValuesMessages, BaseMessages}
import models.{BoxType, UnderpaymentReasonValue}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.TextAmendmentView

class TextAmendmentViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: TextAmendmentView = app.injector.instanceOf[TextAmendmentView]

  val formProvider: UnderpaymentReasonAmendmentFormProvider = injector.instanceOf[UnderpaymentReasonAmendmentFormProvider]

  private final val boxType22: BoxType = BoxType(22,"entry", "text")
  private final val boxNumber: Int = 22
  private final val itemNumber: Int = 1
  private final val validValue = "GBP871.12"
  private final val originalErrorId = "#original-error"
  private final val amendedErrorId = "#amended-error"


  def underpaymentReasonFormWithValues(originalValue: String, amendedValue: String): Form[UnderpaymentReasonValue] =
    formProvider(boxNumber).bind(Map("original" -> originalValue, "amended" -> amendedValue))

  "Rendering the UnderpaymentType page" when {

    "no errors exist" should {
      val form: Form[UnderpaymentReasonValue] = formProvider.apply(boxNumber)
      lazy val view: Html = injectedView(
        form, boxType22 , itemNumber, Call("GET", controllers.routes.BoxNumberController.onLoad().url)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe AmendReasonValuesMessages.box22PageTitle
      }

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

    "an error exists (no value has been specified for original amount)" should {
      lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(emptyString, validValue)
      lazy val view: Html = injectedView(
        form, boxType22 , itemNumber, Call("GET", controllers.routes.BoxNumberController.onLoad().url)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box22PageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.originalNonEmpty
      }

      "render an error message against the field" in {
        elementText(originalErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.originalNonEmpty
      }
    }

    "an error exists (no value has been specified for amended amount)" should {
      lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(validValue, emptyString)
      lazy val view: Html = injectedView(
        form, boxType22 , itemNumber, Call("GET", controllers.routes.BoxNumberController.onLoad().url)
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page title" in {
        document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box22PageTitle
      }

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.amendedNonEmpty
      }

      "render an error message against the field" in {
        elementText(amendedErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedNonEmpty
      }
    }

//    "an error exists (same value has been specified for original and amended amount)" should {
//      lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(validValue, validValue)
//      lazy val view: Html = injectedView(
//        form, boxType22 , itemNumber, Call("GET", controllers.routes.BoxNumberController.onLoad().url)
//      )(fakeRequest, messages)
//      lazy implicit val document: Document = Jsoup.parse(view.body)
//
//      s"have the correct page title" in {
//        document.title mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box22PageTitle
//      }
//
//      "render an error summary with the correct message " in {
//        elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.amendedDifferent
//      }
//
//      "render an error message against the field" in {
//        elementText(amendedErrorId) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedDifferent
//      }
//    }

  }

}
