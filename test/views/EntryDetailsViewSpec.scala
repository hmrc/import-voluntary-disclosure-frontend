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

import java.time.LocalDate

import forms.EntryDetailsFormProvider
import messages.{BaseMessages, EntryDetailsMessages}
import models.EntryDetails
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import views.html.EntryDetailsView

class EntryDetailsViewSpec extends ViewBaseSpec with BaseMessages {

  val currDay = LocalDate.now().getDayOfMonth
  val currMonth = LocalDate.now().getMonthValue
  val currYear = LocalDate.now().getYear

  private lazy val injectedView: EntryDetailsView = app.injector.instanceOf[EntryDetailsView]

  val formProvider: EntryDetailsFormProvider = injector.instanceOf[EntryDetailsFormProvider]

  "Rendering the Entry Details view" when {

    "no errors exist" should {
      lazy val form: Form[EntryDetails] = formProvider()
      lazy val view: Html = injectedView(form)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page heading of '${EntryDetailsMessages.title}'" in {
        document.title mustBe EntryDetailsMessages.title
      }

      s"have Back link" in {
        elementText(".govuk-back-link") mustBe back_link
      }

      s"have the correct h1 of '${EntryDetailsMessages.h1}'" in {
        elementText("h1") mustBe EntryDetailsMessages.h1
      }

      s"have the correct EPU label of '${EntryDetailsMessages.epuLabel}'" in {
        elementText(".govuk-label[for=\"epu\"]") mustBe EntryDetailsMessages.epuLabel
      }

      s"have the correct Entry Number label of '${EntryDetailsMessages.entryNumberLabel}'" in {
        elementText(".govuk-label[for=\"entryNumber\"]") mustBe EntryDetailsMessages.entryNumberLabel
      }

      s"have the correct Entry Date label of '${EntryDetailsMessages.entryDateLabel}'" in {
        elementText(".govuk-fieldset__legend") mustBe EntryDetailsMessages.entryDateLabel
      }

      s"have the correct Entry Date Day label of '${EntryDetailsMessages.entryDateDayLabel}'" in {
        elementText(".govuk-label[for=\"entryDate.day\"]") mustBe EntryDetailsMessages.entryDateDayLabel
      }

      s"have the correct Entry Date Month label of '${EntryDetailsMessages.entryDateMonthLabel}'" in {
        elementText(".govuk-label[for=\"entryDate.month\"]") mustBe EntryDetailsMessages.entryDateMonthLabel
      }

      s"have the correct Entry Date Year label of '${EntryDetailsMessages.entryDateYearLabel}'" in {
        elementText(".govuk-label[for=\"entryDate.year\"]") mustBe EntryDetailsMessages.entryDateYearLabel
      }

      s"have the correct EPU hint of '${EntryDetailsMessages.epuHint}'" in {
        elementText("#epu-hint") mustBe EntryDetailsMessages.epuHint
      }

      s"have the correct Entry Number hint of '${EntryDetailsMessages.entryNumberHint}'" in {
        elementText("#entryNumber-hint") mustBe EntryDetailsMessages.entryNumberHint
      }

      s"have the correct Entry Date hint of '${EntryDetailsMessages.entryDateHint}'" in {
        elementText("#entryDate-hint") mustBe EntryDetailsMessages.entryDateHint
      }

      s"have the correct Continue button" in {
        elementText(".govuk-button") mustBe continue
      }
    }

    "an error exists" should {
      val missingEpu: Map[String, String] = Map("entryNumber" -> "123456Q", "entryDate.day" -> s"${currDay}", "entryDate.month" -> s"${currMonth}", "entryDate.year" -> s"${currYear}")
      val missingEntryNumber: Map[String, String] = Map("epu" -> "123", "entryDate.day" -> s"${currDay}", "entryDate.month" -> s"${currMonth}", "entryDate.year" -> s"${currYear}")
      val missingEntryDate: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q")
      val missingEntryDateDay: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.month" -> s"${currMonth}", "entryDate.year" -> s"${currYear}")
      val missingEntryDateDayAndMonth: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.year" -> s"${currYear}")
      val missingEntryDateDayAndYear: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.month" -> s"${currMonth}")
      val missingEntryDateMonth: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.day" -> s"${currDay}", "entryDate.year" -> s"${currYear}")
      val missingEntryDateMonthAndYear: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.day" -> s"${currDay}")
      val missingEntryDateYear: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.day" -> s"${currDay}", "entryDate.month" -> s"${currMonth}")
      val formatEpu: Map[String, String] = Map("epu" -> "12", "entryNumber" -> "123456Q", "entryDate.day" -> s"${currDay}", "entryDate.month" -> s"${currMonth}", "entryDate.year" -> s"${currYear}")
      val formatEntryNumber: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "1234Q56Q", "entryDate.day" -> s"${currDay}", "entryDate.month" -> s"${currMonth}", "entryDate.year" -> s"${currYear}")
      val realEntryDateError: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.day" -> "43", "entryDate.month" -> s"${currMonth}", "entryDate.year" -> s"${currYear}")
      val pastEntryDateError: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.day" -> s"${currDay+1}", "entryDate.month" -> s"${currMonth}", "entryDate.year" -> s"${currYear}")
      val twoDigitEntryDateYearError: Map[String, String] = Map("epu" -> "123", "entryNumber" -> "123456Q", "entryDate.day" -> s"${currDay}", "entryDate.month" -> s"${currMonth}", "entryDate.year" -> s"20")

      val errors: Map[String, Map[String, String]] = Map(
        EntryDetailsMessages.epuRequiredError -> missingEpu,
        EntryDetailsMessages.entryNumberRequiredError -> missingEntryNumber,
        EntryDetailsMessages.entryDateAllRequiredError -> missingEntryDate,
        EntryDetailsMessages.entryDateDayRequiredError -> missingEntryDateDay,
        EntryDetailsMessages.entryDateDayMonthRequiredError -> missingEntryDateDayAndMonth,
        EntryDetailsMessages.entryDateDayYearRequiredError -> missingEntryDateDayAndYear,
        EntryDetailsMessages.entryDateMonthRequiredError -> missingEntryDateMonth,
        EntryDetailsMessages.entryDateMonthYearRequiredError -> missingEntryDateMonthAndYear,
        EntryDetailsMessages.entryDateYearRequiredError -> missingEntryDateYear,
        EntryDetailsMessages.epuFormatError -> formatEpu,
        EntryDetailsMessages.entryNumberFormatError -> formatEntryNumber,
        EntryDetailsMessages.entryDateRealError -> realEntryDateError,
        EntryDetailsMessages.entryDatePastError -> pastEntryDateError,
        EntryDetailsMessages.entryDateTwoDigitYearError -> twoDigitEntryDateYearError
      )

      errors.map{ err =>
        s"have correct Error Summary for ${err._1}" in {
          getErrorSummary(err._2) mustBe err._1
        }
        s"have correct Error Message for ${err._1}" in {
          // Note: the error message includes a visually hidden "Error:" prompt to accessibility
          getErrorMessage(err._2) mustBe "Error: " + err._1
        }
      }

      def getErrorSummary(formData: Map[String,String]): String = {
        lazy val form: Form[EntryDetails] = formProvider().bind(formData)
        lazy val view: Html = injectedView(form)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(".govuk-error-summary__list")
      }

      def getErrorMessage(formData: Map[String,String]): String = {
        lazy val form: Form[EntryDetails] = formProvider().bind(formData)
        lazy val view: Html = injectedView(form)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(".govuk-error-message")
      }
    }

    "multiple errors" should {
      "produce correct error summary" in {
        lazy val form: Form[EntryDetails] = formProvider().bind(Map("entryNumber" -> "123456Q"))
        lazy val view: Html = injectedView(form)(fakeRequest, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(".govuk-error-summary__list") mustBe
          EntryDetailsMessages.epuRequiredError + " " + EntryDetailsMessages.entryDateAllRequiredError

      }
    }

  }


}