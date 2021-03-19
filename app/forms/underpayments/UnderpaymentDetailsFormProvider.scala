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

package forms.underpayments

import config.AppConfig
import forms.mappings.Mappings
import models.underpayments.UnderpaymentAmount
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages

import javax.inject.Inject


class UnderpaymentDetailsFormProvider @Inject()(implicit appConfig: AppConfig) extends Mappings {

  def apply()(implicit messages: Messages): Form[UnderpaymentAmount] =
    Form(
      mapping(
        "original" -> numeric(
          isCurrency = true,
          requiredKey = "underpaymentTypeTemp.error.originalNonEmpty",
          invalidDecimalPlacesKey = "underpaymentTypeTemp.error.originalNonNumber",
          nonNumericKey = "underpaymentTypeTemp.error.originalNonNumber"
        ).verifying(inRange[BigDecimal](0, 9999999999.99, "underpaymentTypeTemp.error.originalOutOfRange")),
        "amended" -> numeric(
          isCurrency = true,
          requiredKey = "underpaymentTypeTemp.error.amendedNonEmpty",
          invalidDecimalPlacesKey = "underpaymentTypeTemp.error.amendedNonNumber",
          nonNumericKey = "underpaymentTypeTemp.error.amendedNonNumber"
        ).verifying(inRange[BigDecimal](0, 9999999999.99, "underpaymentTypeTemp.error.originalOutOfRange"))
      )(UnderpaymentAmount.apply)(UnderpaymentAmount.unapply)
        .verifying(different("underpaymentTypeTemp.error.amendedDifferent"))
    )

  private[forms] def different(errorKey: String): Constraint[UnderpaymentAmount] =
    Constraint {
      input =>
        if (input.original != input.amended) {
          Valid
        } else {
          Invalid(errorKey)
        }
    }

}
