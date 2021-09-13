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

package views.reasons

import base.ViewBaseSpec
import forms.reasons.UnderpaymentReasonAmendmentFormProvider
import messages.{AmendReasonValuesMessages, BaseMessages}
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, UnderpaymentReasonValue}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.{Form, FormError}
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.reasons.TextAmendmentView

class TextAmendmentViewSpec extends ViewBaseSpec with BaseMessages {

  private val formAction = Call("POST", "formActionUrl")

  private lazy val injectedView: TextAmendmentView = app.injector.instanceOf[TextAmendmentView]

  val formProvider: UnderpaymentReasonAmendmentFormProvider =
    injector.instanceOf[UnderpaymentReasonAmendmentFormProvider]

  private final val boxNumber: BoxNumber = BoxNumber.Box22
  private final val itemNumber: Int      = 1
  private final val validValue           = "GBP871.12"
  private final val originalErrorId      = "#original-error"
  private final val amendedErrorId       = "#amended-error"
  private final val invalidValue         = "11.111"
  private final val invalidValue2        = "3adp4"

  def underpaymentReasonFormWithValues(originalValue: String, amendedValue: String): Form[UnderpaymentReasonValue] =
    formProvider(boxNumber).bind(Map("original" -> originalValue, "amended" -> amendedValue))

  "Rendering the Underpayment Reason Amendment page" when {

    "no errors exist" should {
      val form: Form[UnderpaymentReasonValue] = formProvider.apply(boxNumber)
      lazy val view: Html = injectedView(
        form,
        formAction,
        boxNumber,
        itemNumber
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(AmendReasonValuesMessages.box22PageTitle)

      s"have the correct p1 text of '${AmendReasonValuesMessages.box22P1}'" in {
        elementText("#main-content p:nth-of-type(1)") mustBe AmendReasonValuesMessages.box22P1
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
        form,
        formAction,
        boxNumber,
        itemNumber
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box22PageTitle)

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.originalNonEmpty
      }

      "render an error message against the field" in {
        elementText(
          originalErrorId
        ) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.originalNonEmpty
      }
    }

    "an error exists (no value has been specified for amended amount)" should {
      lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(validValue, emptyString)
      lazy val view: Html = injectedView(
        form,
        formAction,
        boxNumber,
        itemNumber
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box22PageTitle)

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.amendedNonEmpty
      }

      "render an error message against the field" in {
        elementText(
          amendedErrorId
        ) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedNonEmpty
      }
    }

    "an error exists (same value has been entered for original and amended amount)" should {
      lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(validValue, validValue)
        .discardingErrors
        .withError(FormError("amended", AmendReasonValuesMessages.amendedDifferent))
      lazy val view: Html = injectedView(
        form,
        formAction,
        boxNumber,
        itemNumber
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box22PageTitle)

      "render an error summary with the correct message " in {
        elementText(govErrorSummaryListClass) mustBe AmendReasonValuesMessages.amendedDifferent
      }

      "render an error message against the field" in {
        elementText(
          amendedErrorId
        ) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedDifferent
      }
    }

    "an error exists (value has been entered in an invalid format for both original and amended)" should {
      lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(invalidValue, invalidValue2)
      lazy val view: Html = injectedView(
        form,
        formAction,
        boxNumber,
        itemNumber
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.box22PageTitle)

      "render an error summary with the correct message " in {
        elementText(
          govErrorSummaryListClass
        ) mustBe AmendReasonValuesMessages.originalInvalidFormat + " " + AmendReasonValuesMessages.amendedInvalidFormat
      }

      "render an error message against the original field" in {
        elementText(
          originalErrorId
        ) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.originalInvalidFormat
      }

      "render an error message against the amended field" in {
        elementText(
          amendedErrorId
        ) mustBe AmendReasonValuesMessages.errorPrefix + AmendReasonValuesMessages.amendedInvalidFormat
      }
    }
  }

  "The Underpayment Reason Amendment page" when {
    Seq(
      BoxNumber.Box22,
      BoxNumber.Box33,
      BoxNumber.Box34,
      BoxNumber.Box36,
      BoxNumber.Box37,
      BoxNumber.Box39,
      BoxNumber.Box41,
      BoxNumber.Box42,
      BoxNumber.Box43,
      BoxNumber.Box45,
      BoxNumber.Box62,
      BoxNumber.Box63,
      BoxNumber.Box66,
      BoxNumber.Box67,
      BoxNumber.Box68
    ).map { testBox =>
      checkContent(testBox)
    }

    def checkContent(boxNumber: BoxNumber): Unit = {
      s"rendered for box $boxNumber" should {
        val form: Form[UnderpaymentReasonValue] = formProvider.apply(boxNumber)
        lazy val view: Html = injectedView(
          form,
          formAction,
          boxNumber,
          itemNumber
        )(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(AmendReasonValuesMessages.boxContent.get(boxNumber).get.title)

        "have the correct page heading" in {
          elementText("h1") mustBe AmendReasonValuesMessages.boxContent.get(boxNumber).get.heading
        }

        "have the correct body text (if applicable)" in {
          if (AmendReasonValuesMessages.boxContent.get(boxNumber).get.body.isDefined) {
            elementText("#main-content p:nth-of-type(1)") mustBe AmendReasonValuesMessages.boxContent.get(
              boxNumber
            ).get.body.get
          } else {
            assert(true)
          }
        }
      }
    }

  }

  it should {

    lazy val form: Form[UnderpaymentReasonValue] = underpaymentReasonFormWithValues(validValue, emptyString)
    lazy val view: Html = injectedView(
      form,
      formAction,
      boxNumber,
      itemNumber
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have correct legend for the original amount" in {
      elementText(
        "#main-content > div > div > form > div:nth-child(2) > label"
      ) mustBe AmendReasonValuesMessages.originalAmount
    }

    s"have correct legend for the amended amount" in {
      elementText(
        "#main-content > div > div > form > div:nth-child(3) > label"
      ) mustBe AmendReasonValuesMessages.amendedAmount
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

    "have the correct formAction" in {
      elementAttributes("form") must contain("action" -> formAction.url)
    }

  }

}
