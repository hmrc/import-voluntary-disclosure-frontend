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
import forms.ImportVATFormProvider
import messages.{BaseMessages, ImportVATMessages}
import models.UnderpaymentAmount
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.ImportVATView

class ImportVATViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: ImportVATView = app.injector.instanceOf[ImportVATView]

  val formProvider: ImportVATFormProvider = injector.instanceOf[ImportVATFormProvider]

  private final val fifty = "50"
  private final val nonNumericInput = "!@JdsJgbnmL"
  private final val originalErrorId = "#original-error"
  private final val amendedErrorId = "#amended-error"

  def underpaymentFormWithValues(originalValue: String, amendedValue: String): Form[UnderpaymentAmount] =
    formProvider().bind(Map("original" -> originalValue, "amended" -> amendedValue))

  "Rendering the UnderpaymentType page" when {

    "no errors exist" should {
      val form: Form[UnderpaymentAmount] = formProvider.apply()
      lazy val view: Html = injectedView(
        form
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

}