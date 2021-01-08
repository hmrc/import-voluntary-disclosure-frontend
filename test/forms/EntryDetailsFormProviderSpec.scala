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

package forms

import java.time.LocalDate

import base.SpecBase
import mocks.config.MockAppConfig
import models.EntryDetails
import play.api.data.FormError

class EntryDetailsFormProviderSpec extends SpecBase {

  "Binding a form with invalid data" when {

    "no values provided" should {
      val missingOption: Map[String, String] = Map.empty
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(missingOption)

      "result in a form with errors" in {
        form.errors mustBe Seq(
          FormError("epu", "entryDetails.epu.error.missing"),
          FormError("entryNumber", "entryDetails.entryNumber.error.missing"),
          FormError("entryDate.day", "entryDetails.entryDate.error.required.all", List("day","month","year"))
        )
      }
    }

    "missing entry date day" should {
      val formData: Map[String, String] = Map(
        "epu" -> "123",
        "entryNumber" -> "123456Q",
        "entryDate.month" -> "12",
        "entryDate.year" -> "2020"
      )
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.required"
        form.errors.head.args mustBe List("day")
      }
    }
    "missing entry date month" should {
      val formData: Map[String, String] = Map(
        "epu" -> "123",
        "entryNumber" -> "123456Q",
        "entryDate.day" -> "12",
        "entryDate.year" -> "2020"
      )
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.month"
        form.errors.head.message mustBe "entryDetails.entryDate.error.required"
        form.errors.head.args mustBe List("month")
      }
    }
    "missing entry date year" should {
      val formData: Map[String, String] = Map(
        "epu" -> "123",
        "entryNumber" -> "123456Q",
        "entryDate.day" -> "12",
        "entryDate.month" -> "12",
      )
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.year"
        form.errors.head.message mustBe "entryDetails.entryDate.error.required"
        form.errors.head.args mustBe List("year")
      }
    }
    "missing multiple entry date fields" should {
      val formData: Map[String, String] = Map(
        "epu" -> "123",
        "entryNumber" -> "123456Q",
        "entryDate.month" -> "12",
      )
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.required.two"
        form.errors.head.args mustBe List("day", "year")
      }
    }

    "epu invalid" should {
      val formData: Map[String, String] = Map(
        "epu" -> "aaa",
        "entryNumber" -> "123456Q",
        "entryDate.day" -> "12",
        "entryDate.month" -> "12",
        "entryDate.year" -> "2020"
      )
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "epu"
        form.errors.head.message mustBe "entryDetails.epu.error.format"
      }
    }

    "entryNumber invalid" should {
      val formData: Map[String, String] = Map(
        "epu" -> "123",
        "entryNumber" -> "12345Q",
        "entryDate.day" -> "12",
        "entryDate.month" -> "12",
        "entryDate.year" -> "2020"
      )
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

      "result in a form with errors" in {
        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryNumber"
        form.errors.head.message mustBe "entryDetails.entryNumber.error.format"
      }
    }

    "entryDate invalid" should {
      "result in a form with Day format error" in {
        val formData: Map[String, String] = Map(
          "epu" -> "123",
          "entryNumber" -> "123456Q",
          "entryDate.day" -> "32",
          "entryDate.month" -> "12",
          "entryDate.year" -> "2020"
        )
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.invalid"
      }
      "result in a form with Month format error" in {
        val formData: Map[String, String] = Map(
          "epu" -> "123",
          "entryNumber" -> "123456Q",
          "entryDate.day" -> "12",
          "entryDate.month" -> "13",
          "entryDate.year" -> "2020"
        )
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.invalid"
      }
      "result in a form with Year format error" in {
        val formData: Map[String, String] = Map(
          "epu" -> "123",
          "entryNumber" -> "123456Q",
          "entryDate.day" -> "12",
          "entryDate.month" -> "12",
          "entryDate.year" -> "202A"
        )
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.year"
        form.errors.head.message mustBe "entryDetails.entryDate.error.invalid"
      }
      "result in a form with Year length error" in {
        val formData: Map[String, String] = Map(
          "epu" -> "123",
          "entryNumber" -> "123456Q",
          "entryDate.day" -> "12",
          "entryDate.month" -> "12",
          "entryDate.year" -> "20"
        )
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.year"
        form.errors.head.message mustBe "entryDetails.entryDate.error.year.length"
      }
      "result in a form with Date in Past error" in {
        val formData: Map[String, String] = Map(
          "epu" -> "123",
          "entryNumber" -> "123456Q",
          "entryDate.day" -> s"${LocalDate.now().getDayOfMonth + 1}",
          "entryDate.month" -> s"${LocalDate.now().getMonthValue}",
          "entryDate.year" -> s"${LocalDate.now().getYear}"
        )
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.past"
      }
      "result in a form with Not A Date error" in {
        val formData: Map[String, String] = Map(
          "epu" -> "123",
          "entryNumber" -> "123456Q",
          "entryDate.day" -> "43",
          "entryDate.month" -> s"${LocalDate.now().getMonthValue}",
          "entryDate.year" -> s"${LocalDate.now().getYear}"
        )
        val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

        form.errors.size mustBe 1
        form.errors.head.key mustBe "entryDate.day"
        form.errors.head.message mustBe "entryDetails.entryDate.error.invalid"
      }

    }

  }

  "Binding a form with valid data" should {

    val formData: Map[String, String] = Map(
      "epu" -> "123",
      "entryNumber" -> "123456Q",
      "entryDate.day" -> "12",
      "entryDate.month" -> "12",
      "entryDate.year" -> "2020"
    )
    val form = new EntryDetailsFormProvider()(MockAppConfig).apply().bind(formData)

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

    "generate the correct model" in {
      form.value mustBe Some(EntryDetails("123", "123456Q", LocalDate.of(2020, 12, 12)))
    }
  }

  "A form built from a valid model" should {
    "generate the correct mapping" in {
      val model = EntryDetails("123", "123456Q", LocalDate.of(2020, 12, 12))
      val form = new EntryDetailsFormProvider()(MockAppConfig).apply().fill(model)
      form.data mustBe Map(
        "epu" -> "123",
        "entryNumber" -> "123456Q",
        "entryDate.day" -> "12",
        "entryDate.month" -> "12",
        "entryDate.year" -> "2020"
      )
    }
  }

}

