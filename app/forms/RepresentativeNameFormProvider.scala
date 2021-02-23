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

import forms.mappings.Mappings
import play.api.data.Form
import play.api.i18n.Messages

import javax.inject.Inject

class RepresentativeNameFormProvider @Inject() extends Mappings {

  def apply()(implicit messages: Messages): Form[String] =
    Form(
      "value" -> text("representativeName.error.nameNonEmpty")
        .verifying("representativeName.error.nameMinLength", value => value.length >= 2)
        .verifying("representativeName.error.nameMaxLength", value => value.length <= 50)
        .verifying(regexp("^[a-zA-Z '-]+$", "representativeName.error.nameAllowableCharacters")),
    )

}
