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
import models.TraderContactDetails
import play.api.data.Form
import play.api.data.Forms.{email, mapping}
import play.api.data.validation.Constraints
import play.api.i18n.Messages

import javax.inject.Inject


class TraderContactDetailsFormProvider @Inject()(implicit appConfig: AppConfig) extends Mappings {

  def apply()(implicit messages: Messages): Form[TraderContactDetails] =
    Form(
      mapping(
        "fullName" -> text("traderContactDetails.error.nameNonEmpty") // need a regex for the name
          .verifying("traderContactDetails.error.nameMinLength", value => value.length >= 2)
          .verifying("traderContactDetails.error.nameMaxLength", value => value.length <= 50),
        "email" -> text("traderContactDetails.error.emailNonEmpty")
          .verifying(
            regexp(
              "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$",
              "traderContactDetails.error.emailInvalidFormat")
          ),
        "phoneNumber" -> text("traderContactDetails.error.phoneNumberNonEmpty")
      )(TraderContactDetails.apply)(TraderContactDetails.unapply)
    )

}
