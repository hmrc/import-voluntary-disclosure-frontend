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

package forms.importDetails

import base.FormSpecBase

class ImporterVatRegisteredFormProviderSpec extends FormSpecBase {

  "Binding a form with invalid data" when {

    "no value is selected" should {

      val missingOption: Map[String, String] = Map.empty
      val form                               = new ImporterVatRegisteredFormProvider()("importer").bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "importerVatRegistered.error.required"
      }
    }
  }

  "Binding a form with valid data" should {

    val data = Map("value" -> "true")
    val form = new ImporterVatRegisteredFormProvider()("importer").bind(data)

    "throw no errors" in {
      form.errors.size mustBe 0
    }

  }

}
