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

package forms.cancelCase

import base.FormSpecBase
import play.api.data.Form

class CancelCaseUploadFileFormProviderSpec extends FormSpecBase {

  def formBuilder(file: String = ""): Map[String, String] = Map(
    "file" -> file
  )

  def formBinder(formValues: Map[String, String] = Map("file" -> "")): Form[String] =
    new CancelCaseUploadFileFormProvider()().bind(formValues)

  "Binding a form with invalid data" when {

    "with no data present" should {

      val missingOption: Map[String, String] = Map.empty
      val form                               = new CancelCaseUploadFileFormProvider()().bind(missingOption)

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }

      "throw one error" in {
        form.errors.size mustBe 1
      }

      "have an error with the correct message" in {
        form.errors.head.message mustBe "uploadFile.error.noFileSelected"
      }
    }

    "Binding a form with valid data" should {

      val form = formBinder(formBuilder("file"))

      "result in a form with no errors" in {
        form.hasErrors mustBe false
      }

    }

  }

}
