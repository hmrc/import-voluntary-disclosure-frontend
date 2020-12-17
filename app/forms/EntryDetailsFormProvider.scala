/*
 * Copyright 2020 HM Revenue & Customs
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

import config.AppConfig
import forms.mappings.Mappings
import javax.inject.Inject
import models.EntryDetails
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

class EntryDetailsFormProvider @Inject()(implicit appConfig: AppConfig) extends Mappings {

  def apply(): Form[EntryDetails] = {

    Form( mapping(
      "epu" -> text("entryDetails.epu.error.missing"),
      "entryNumber" -> text("entryDetails.entryNumber.error.missing")
    )(EntryDetails.apply)(EntryDetails.unapply)
//      "date" -> localDate(
//        invalidKey = "claimPeriodStartDate.error.invalid",
//        allRequiredKey = "claimPeriodStartDate.error.required.all",
//        twoRequiredKey = "claimPeriodStartDate.error.required.two",
//        requiredKey = "claimPeriodStartDate.error.required"
//      )
    )
  }
}
