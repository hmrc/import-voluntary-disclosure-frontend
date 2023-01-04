/*
 * Copyright 2023 HM Revenue & Customs
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

package forms.updateCase

import base.FormSpecBase
import play.api.data.Form

class DisclosureReferenceNumberProviderSpec extends FormSpecBase {

  def formBuilder(value: String = ""): Map[String, String] = Map(
    "value" -> value
  )

  def formBinder(formValues: Map[String, String] = Map("value" -> "")): Form[String] =
    new DisclosureReferenceNumberFormProvider()().bind(formValues)

  "Binding a form with invalid data" when {

    "with no data present" should {

      val form = formBinder(Map.empty)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "disclosureReference.error.required"
      }
    }

    "with invalid format" should {

      val form = formBinder(Map("value" -> "invalid"))

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "disclosureReference.error.format"
      }
    }
  }

  "Binding a form with valid data" when {

    "Valid data present" should {
      val form = formBinder(formBuilder("C181234567890123456789"))

      "result in a form with no errors" in {
        form.hasErrors mustBe false
      }
    }

  }

  "Binding a form with valid data with spaces" should {

    val form = formBinder(formBuilder("C181234567890 123456789"))

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

    "result in a form with correct value" in {
      form.value mustBe Some("C181234567890123456789")
    }

  }

}
