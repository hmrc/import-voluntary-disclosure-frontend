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

package forms.updateCase

import forms.mappings.Mappings
import forms.utils.FormHelpers
import play.api.data.Form

import javax.inject.Inject

class DisclosureReferenceNumberFormProvider @Inject() extends Mappings with FormHelpers {

  def apply(): Form[String] =
    Form(
      "value" -> text("disclosureReference.error.required")
        .transform[String](toUpperNoSpaces(_), toUpperNoSpaces(_))
        .verifying(regexp("^[cC]18[a-zA-Z0-9]{19}$", "disclosureReference.error.format"))
    )
}
