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

package views.reasons

import base.ViewBaseSpec
import forms.reasons.UnderpaymentReasonAmendmentFormProvider
import messages.BaseMessages
import messages.reasons.OtherReasonAmendmentMessages
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, UnderpaymentReasonValue}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.reasons.OtherReasonAmendmentView

class OtherReasonAmendmentViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: OtherReasonAmendmentView = app.injector.instanceOf[OtherReasonAmendmentView]

  val formProvider: UnderpaymentReasonAmendmentFormProvider =
    injector.instanceOf[UnderpaymentReasonAmendmentFormProvider]

  private final val boxNumber: BoxNumber = BoxNumber.OtherItem
  private final val itemNumber: Int      = 0
  private val formAction                 = Call("POST", "formActionUrl")
  private val backLink                   = Some(Call("GET", "backLinkUrl"))

  "Rendering the Other reason amendment page" when {

    "no errors exist" should {

      val form: Form[UnderpaymentReasonValue] = formProvider.apply(boxNumber)
      lazy val view: Html = injectedView(
        form,
        formAction,
        boxNumber,
        itemNumber,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(OtherReasonAmendmentMessages.title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      s"have the correct h1 of '${OtherReasonAmendmentMessages.title}'" in {
        elementText("h1") mustBe OtherReasonAmendmentMessages.title
      }

    }

    "error exists" should {
      lazy val form: Form[UnderpaymentReasonValue] =
        formProvider(boxNumber).bind(Map("original" -> "", "amended" -> ""))
      lazy val view: Html = injectedView(
        form,
        formAction,
        boxNumber,
        itemNumber,
        backLink
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(OtherReasonAmendmentMessages.errorPrefix + OtherReasonAmendmentMessages.title)

      "render an error summary with the correct message" in {
        elementText(
          "div.govuk-error-summary > div"
        ) mustBe thereIsAProblemPrefix + OtherReasonAmendmentMessages.requiredError
      }

      "render an error message against the field" in {
        elementText(
          "#original-error"
        ) mustBe OtherReasonAmendmentMessages.errorPrefix + OtherReasonAmendmentMessages.requiredError
      }

    }

  }

  it should {

    val form: Form[UnderpaymentReasonValue] = formProvider.apply(boxNumber)
    lazy val view: Html = injectedView(
      form,
      formAction,
      boxNumber,
      itemNumber,
      backLink
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }

}
