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

import base.SpecBase
import mocks.config.MockAppConfig
import models.UnderpaymentAmount
import play.api.data.FormError

class CustomsDutyFormProviderSpec extends SpecBase {

  "Binding a form with invalid data" when {

    "no values provided" should {
      val form = new CustomsDutyFormProvider()(MockAppConfig).apply().bind(Map("original" -> "", "amended" -> ""))

      "result in a form with errors" in {
        form.errors mustBe Seq(
          FormError("original", "customsDuty.error.originalNonEmpty"),
          FormError("amended", "customsDuty.error.amendedNonEmpty")
        )
      }

      "no original value provided" should {
        val form = new CustomsDutyFormProvider()(MockAppConfig).apply().bind(Map("original" -> "", "amended" -> "50"))

        "result in a form with errors" in {
          form.errors.head mustBe FormError("original", "customsDuty.error.originalNonEmpty")
        }
      }

      "no amended value provided" should {
        val form = new CustomsDutyFormProvider()(MockAppConfig).apply().bind(Map("original" -> "50", "amended" -> ""))

        "result in a form with errors" in {
          form.errors.head mustBe FormError("amended", "customsDuty.error.amendedNonEmpty")
        }
      }

      "non numeric values provided" should {
        val form = new CustomsDutyFormProvider()(MockAppConfig).apply().bind(Map("original" -> "@£$%FGB", "amended" -> "@£$%FGB"))

        "result in a form with errors" in {
          form.errors mustBe Seq(
            FormError("original", "customsDuty.error.originalNonNumber"),
            FormError("amended", "customsDuty.error.amendedNonNumber")
          )
        }
      }

      "non numeric original value provided" should {
        val form = new CustomsDutyFormProvider()(MockAppConfig).apply().bind(Map("original" -> "@£$%FGB", "amended" -> "50"))

        "result in a form with errors" in {
          form.errors.head mustBe FormError("original", "customsDuty.error.originalNonNumber")

        }
      }

      "non numeric amended value provided" should {
        val form = new CustomsDutyFormProvider()(MockAppConfig).apply().bind(Map("original" -> "50", "amended" -> "@£$%FGB"))

        "result in a form with errors" in {
          form.errors.head mustBe FormError("amended", "customsDuty.error.amendedNonNumber")

        }
      }

      "original amount exceeding the limit" should {
        val form = new CustomsDutyFormProvider()(MockAppConfig).apply().bind(Map("original" -> "10000000000", "amended" -> "50"))

        "result in a form with errors" in {
          form.errors.head mustBe FormError("original", messages("customsDuty.error.originalUpperLimit"))
        }
      }

    }

    "Binding a form with valid data" should {
      val form = new CustomsDutyFormProvider()(MockAppConfig).apply().bind(Map("original" -> "40", "amended" -> "50"))

      "result in a form with no errors" in {
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        form.value mustBe Some(UnderpaymentAmount(BigDecimal("40"), BigDecimal("50")))
      }
    }

    "A form built from a valid model" should {
      "generate the correct mapping" in {
        val model = UnderpaymentAmount(BigDecimal("0.0"), BigDecimal("60.0"))
        val form = new CustomsDutyFormProvider()(MockAppConfig).apply().fill(model)
        form.data mustBe Map("original" -> "0.0", "amended" -> "60.0")
      }
    }


  }

}
