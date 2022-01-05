/*
 * Copyright 2022 HM Revenue & Customs
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

package forms.docUpload

import base.FormSpecBase

class OptionalSupportingDocsFormProviderSpec extends FormSpecBase {

  "Binding a form with invalid data" when {
    val data = Map("" -> "")
    val form = new OptionalSupportingDocsFormProvider()().bind(data)

    "Invalid data present" should {

      "result in a form with errors" in {
        form.hasErrors mustBe true
      }
    }

  }

  "Binding a form with valid data" when {
    val data = Map("optionalDocumentsList[0]" -> "importAndEntry")
    val form = new OptionalSupportingDocsFormProvider()().bind(data)

    "Valid data present" should {

      "result in a form without errors" in {
        form.hasErrors mustBe false
      }
    }

  }

}
