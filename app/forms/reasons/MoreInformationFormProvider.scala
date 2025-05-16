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

package forms.reasons

import forms.mappings.Mappings
import forms.utils.TextAreaHelper.removeCarriageReturn
import play.api.data.Form

import javax.inject.Inject

class MoreInformationFormProvider @Inject() extends Mappings {

  val maxLength: Int = 1500

  def apply(isOneEntry: Boolean = true): Form[String] = {
    val requiredErrorMessage = if (isOneEntry) {
      "moreInformation.single.error.required"
    } else {
      "moreInformation.bulk.error.required"
    }
    val maxCharactersErrorMessage = if (isOneEntry) {
      "moreInformation.single.error.maxLength"
    } else {
      "moreInformation.bulk.error.maxLength"
    }

    Form(
      "value" -> text(requiredErrorMessage)
        .verifying(maxLength(maxLength, maxCharactersErrorMessage, removeCarriageReturn))
    )
  }

}
