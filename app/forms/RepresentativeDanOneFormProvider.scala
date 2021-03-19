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

import config.AppConfig
import forms.mappings.Mappings
import javax.inject.Inject
import models.{EntryDetails, RepresentativeDanOne}
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

class RepresentativeDanOneFormProvider @Inject()(implicit appConfig: AppConfig) extends Mappings {

  def apply()(implicit messages: Messages): Form[RepresentativeDanOne] = {

    Form( mapping(
      "accountNumber" -> text("repDanOne.error.input.required")
        .verifying(regexp("[0-9]{7}","repDanOne.error.input.format")),
      "value" -> text("repDanOne.error.radio.required")
    )(RepresentativeDanOne.apply)(RepresentativeDanOne.unapply)
    )
  }
}
