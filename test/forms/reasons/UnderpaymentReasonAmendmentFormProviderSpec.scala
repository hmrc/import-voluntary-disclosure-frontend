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

package forms.reasons

import base.FormSpecBase
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, UnderpaymentReasonValue}
import play.api.data.validation._
import play.api.data.{Form, FormError}

class UnderpaymentReasonAmendmentFormProviderSpec extends FormSpecBase {
  val originalKey                             = "original"
  val amendedKey                              = "amended"
  val originalFormatMessageKey                = "amendmentValue.error.original.format"
  val amendedFormatMessageKey                 = "amendmentValue.error.amended.format"
  val originalMissingMessageKey               = "amendmentValue.error.original.missing"
  val amendedMissingMessageKey                = "amendmentValue.error.amended.missing"
  val keysDifferentMessageKey                 = "amendmentValue.error.amended.different"
  val originalWeightMissingMessageKey         = "amendmentValue.error.original.weight.missing"
  val amendedWeightMissingMessageKey          = "amendmentValue.error.amended.weight.missing"
  val originalWeightFormatMessageKey          = "amendmentValue.error.original.weight.nonNumeric"
  val amendedWeightFormatMessageKey           = "amendmentValue.error.amended.weight.nonNumeric"
  val originalWeightDecimalMessageKey         = "amendmentValue.error.original.weight.invalidDecimals"
  val amendedWeightDecimalMessageKey          = "amendmentValue.error.amended.weight.invalidDecimals"
  val originalWeightRangeMessageKey           = "amendmentValue.error.original.weight.outOfRange"
  val amendedWeightRangeMessageKey            = "amendmentValue.error.amended.weight.outOfRange"
  val originalUnitMissingMessageKey           = "amendmentValue.error.original.unit.missing"
  val amendedUnitMissingMessageKey            = "amendmentValue.error.amended.unit.missing"
  val originalUnitFormatMessageKey            = "amendmentValue.error.original.unit.nonNumeric"
  val amendedUnitFormatMessageKey             = "amendmentValue.error.amended.unit.nonNumeric"
  val originalUnitDecimalMessageKey           = "amendmentValue.error.original.unit.invalidDecimals"
  val amendedUnitDecimalMessageKey            = "amendmentValue.error.amended.unit.invalidDecimals"
  val originalUnitRangeMessageKey             = "amendmentValue.error.original.unit.outOfRange"
  val amendedUnitRangeMessageKey              = "amendmentValue.error.amended.unit.outOfRange"
  val originalDecimalMissingMessageKey        = "amendmentValue.error.original.decimal.missing"
  val amendedDecimalMissingMessageKey         = "amendmentValue.error.amended.decimal.missing"
  val originalDecimalFormatMessageKey         = "amendmentValue.error.original.decimal.nonNumeric"
  val amendedDecimalFormatMessageKey          = "amendmentValue.error.amended.decimal.nonNumeric"
  val originalDecimalInvalidDecimalMessageKey = "amendmentValue.error.original.decimal.invalidDecimals"
  val amendedDecimalInvalidDecimalMessageKey  = "amendmentValue.error.amended.decimal.invalidDecimals"
  val originalDecimalRangeMessageKey          = "amendmentValue.error.original.decimal.outOfRange"
  val amendedDecimalRangeMessageKey           = "amendmentValue.error.amended.decimal.outOfRange"
  val adjustmentAmendedValue                  = "A12.5"
  val adjustmentOriginalValue                 = "12.5"
  val invalidAdjustmentAmendedValue           = ".5"
  val invalidAdjustmentOriginalValue          = "A.1"
  val commodityCodeAmendedValue               = "2204109400X411"
  val commodityCodeOriginalValue              = "2204109400X412"
  val commodityCodeOriginalLowerValue         = "2204109400x412"
  val invalidBoxAmendedValue                  = "2204109400X411"
  val invalidBoxOriginalValue                 = "2204109400X412"
  val foreignCurrencyAmendedValue             = "GBP50"
  val foreignCurrencyOriginalValue            = "GBP40"
  val foreignCurrencyOriginalLowerValue       = "gbp40"
  val nonNumeric                              = "@£$%FGB"
  val weightOriginalValue                     = 1500
  val weightAmendedValue                      = 3593.44
  val unitOriginalValue                       = 1500
  val unitAmendedValue                        = 3593.44
  val tooManyDecimalValue                     = 950.3829
  val outOfRangeValue                         = 95043953
  val decimalOriginalValue                    = 1500
  val decimalAmendedValue                     = 3593.44
  val decimalOutOfRangeValue: BigDecimal      = 9999999999999.99
  val vmNumberOriginalValue                   = "2"
  val vmNumberAmendedValue                    = "3"
  val box45Regex =
    """^[A-M]{1}$|^[A-M]{1}[0-9]{1,2}$|^[A-M]{1}[0-9]{1,2}[.][0-9]{1}$|^[0-9]{1,2}[.][0-9]{1}$|^[0-9]{1,2}$"""

  def formBuilder(original: String = "", amended: String = ""): Map[String, String] = Map(
    originalKey -> original,
    amendedKey  -> amended
  )

  def formBinderBox(
    formValues: Map[String, String] = Map(originalKey -> "", amendedKey -> ""),
    box: BoxNumber = BoxNumber.Box22
  ): Form[UnderpaymentReasonValue] =
    new UnderpaymentReasonAmendmentFormProvider()(box).bind(formValues)

  "UnderpaymentReasonAmendmentFormProvider" when {
    "mapping for a given box" should {
      "produce the correct form for box 22" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = foreignCurrencyOriginalValue, amended = foreignCurrencyAmendedValue),
          box = BoxNumber.Box22
        )
        form.value mustBe Some(UnderpaymentReasonValue(foreignCurrencyOriginalValue, foreignCurrencyAmendedValue))
      }
      "produce the correct form for box 33" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = commodityCodeOriginalValue, amended = commodityCodeAmendedValue),
          box = BoxNumber.Box33
        )
        form.value mustBe Some(UnderpaymentReasonValue(commodityCodeOriginalValue, commodityCodeAmendedValue))
      }
      "produce the correct form for box 62" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = foreignCurrencyOriginalValue, amended = foreignCurrencyAmendedValue),
          box = BoxNumber.Box62
        )
        form.value mustBe Some(UnderpaymentReasonValue(foreignCurrencyOriginalValue, foreignCurrencyAmendedValue))
      }
      "produce the correct form for box 43" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = vmNumberOriginalValue, amended = vmNumberAmendedValue),
          box = BoxNumber.Box43
        )
        form.value mustBe Some(UnderpaymentReasonValue(vmNumberOriginalValue, vmNumberAmendedValue))
      }
    }
  }

  "Binding a form with invalid data for a foreignCurrency box selected" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = BoxNumber.Box22).errors mustBe Seq(
          FormError(originalKey, originalMissingMessageKey),
          FormError(amendedKey, amendedMissingMessageKey)
        )
      }
    }

    "no original value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(amended = foreignCurrencyAmendedValue), box = BoxNumber.Box22).errors mustBe
          Seq(
            FormError(originalKey, originalMissingMessageKey)
          )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = foreignCurrencyAmendedValue), box = BoxNumber.Box22).errors mustBe
          Seq(
            FormError(amendedKey, amendedMissingMessageKey)
          )
      }
    }

    "non numeric values provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = nonNumeric, amended = nonNumeric),
          box = BoxNumber.Box22
        ).errors mustBe Seq(
          FormError(originalKey, originalFormatMessageKey),
          FormError(amendedKey, amendedFormatMessageKey)
        )
      }
    }

    "non numeric original value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = nonNumeric, amended = foreignCurrencyAmendedValue),
          box = BoxNumber.Box22
        ).errors mustBe
          Seq(
            FormError(originalKey, originalFormatMessageKey)
          )
      }
    }

    "non numeric amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = foreignCurrencyAmendedValue, amended = nonNumeric),
          box = BoxNumber.Box22
        ).errors mustBe
          Seq(
            FormError(amendedKey, amendedFormatMessageKey)
          )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = foreignCurrencyAmendedValue, amended = foreignCurrencyAmendedValue),
          box = BoxNumber.Box22
        ).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
      "result in a form with errors due to case sensitive diff" in {
        formBinderBox(
          formBuilder(
            original = foreignCurrencyAmendedValue.toUpperCase,
            amended = foreignCurrencyAmendedValue.toLowerCase
          ),
          box = BoxNumber.Box22
        ).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data for a foreignCurrency box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = foreignCurrencyOriginalValue, amended = foreignCurrencyAmendedValue),
          box = BoxNumber.Box22
        )
        form.hasErrors mustBe false
      }
    }
    "provided with valid values with spaces" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] =
          formBinderBox(formBuilder(original = "gbp 40", amended = " G b P 50 "), box = BoxNumber.Box22)
        form.hasErrors mustBe false
      }
    }
  }

  "A foreignCurrency form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue =
          UnderpaymentReasonValue(foreignCurrencyAmendedValue, foreignCurrencyOriginalValue)
        val form: Form[UnderpaymentReasonValue] =
          new UnderpaymentReasonAmendmentFormProvider()(BoxNumber.Box22).fill(model)
        form.data mustBe formBuilder(original = foreignCurrencyAmendedValue, amended = foreignCurrencyOriginalValue)
      }
    }
  }

  "Binding a form with invalid data for a textForm box selected" when {

    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = BoxNumber.Box33).errors mustBe Seq(
          FormError(originalKey, originalMissingMessageKey),
          FormError(amendedKey, amendedMissingMessageKey)
        )
      }
    }

    "no original value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(amended = commodityCodeAmendedValue), box = BoxNumber.Box33).errors mustBe
          Seq(
            FormError(originalKey, originalMissingMessageKey)
          )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = commodityCodeAmendedValue), box = BoxNumber.Box33).errors mustBe
          Seq(
            FormError(amendedKey, amendedMissingMessageKey)
          )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = commodityCodeAmendedValue, amended = commodityCodeAmendedValue),
          box = BoxNumber.Box33
        ).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
      "result in a form with errors if difference is case sensitivity" in {
        formBinderBox(
          formBuilder(
            original = commodityCodeAmendedValue.toUpperCase,
            amended = commodityCodeAmendedValue.toLowerCase
          ),
          box = BoxNumber.Box33
        ).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }

    "invalid format for box 45" should {
      "result in a form with errors for original field" in {
        formBinderBox(
          formBuilder(original = invalidAdjustmentOriginalValue, amended = adjustmentAmendedValue),
          box = BoxNumber.Box45
        ).errors mustBe
          Seq(
            FormError(originalKey, originalFormatMessageKey, Seq(box45Regex))
          )
      }

      "result in a form with errors for amendment field" in {
        formBinderBox(
          formBuilder(original = adjustmentOriginalValue, amended = invalidAdjustmentAmendedValue),
          box = BoxNumber.Box45
        ).errors mustBe
          Seq(
            FormError(amendedKey, amendedFormatMessageKey, Seq(box45Regex))
          )
      }
    }
  }

  "Binding a form with valid data for a textForm box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = commodityCodeOriginalValue, amended = commodityCodeAmendedValue),
          box = BoxNumber.Box33
        )
        form.hasErrors mustBe false
      }
    }
    "provided with valid values with spaces" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = "2204109400 x 412", amended = " 220410 9400X411 "),
          box = BoxNumber.Box33
        )
        form.hasErrors mustBe false
      }
    }
  }

  "A text form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue =
          UnderpaymentReasonValue(commodityCodeAmendedValue, commodityCodeOriginalValue)
        val form: Form[UnderpaymentReasonValue] =
          new UnderpaymentReasonAmendmentFormProvider()(BoxNumber.Box33).fill(model)
        form.data mustBe formBuilder(original = commodityCodeAmendedValue, amended = commodityCodeOriginalValue)
      }
    }
  }

  "Binding a form with invalid data for a weight box selected" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = BoxNumber.Box35).errors mustBe Seq(
          FormError(originalKey, originalWeightMissingMessageKey),
          FormError(amendedKey, amendedWeightMissingMessageKey)
        )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = weightOriginalValue.toString), box = BoxNumber.Box35).errors mustBe
          Seq(
            FormError(amendedKey, amendedWeightMissingMessageKey)
          )
      }
    }

    "non numeric values provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = nonNumeric, amended = nonNumeric),
          box = BoxNumber.Box35
        ).errors mustBe Seq(
          FormError(originalKey, originalWeightFormatMessageKey),
          FormError(amendedKey, amendedWeightFormatMessageKey)
        )
      }
    }

    "non numeric amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = weightOriginalValue.toString, amended = nonNumeric),
          box = BoxNumber.Box35
        ).errors mustBe
          Seq(
            FormError(amendedKey, amendedWeightFormatMessageKey)
          )
      }
    }

    "too many decimal values provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = tooManyDecimalValue.toString, amended = tooManyDecimalValue.toString),
          box = BoxNumber.Box35
        ).errors mustBe Seq(
          FormError(originalKey, originalWeightDecimalMessageKey),
          FormError(amendedKey, amendedWeightDecimalMessageKey)
        )
      }
    }

    "too many decimal original value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = tooManyDecimalValue.toString, amended = weightAmendedValue.toString),
          box = BoxNumber.Box35
        ).errors mustBe
          Seq(
            FormError(originalKey, originalWeightDecimalMessageKey)
          )
      }
    }

    "too many decimal amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = weightOriginalValue.toString, amended = tooManyDecimalValue.toString),
          box = BoxNumber.Box35
        ).errors mustBe
          Seq(
            FormError(amendedKey, amendedWeightDecimalMessageKey)
          )
      }
    }

    "out of range values provided" should {
      "result in a form with errors" in {
        val rangeValueArgs = Seq(0, 9999999.999)
        formBinderBox(
          formBuilder(original = outOfRangeValue.toString, amended = outOfRangeValue.toString),
          box = BoxNumber.Box35
        ).errors mustBe Seq(
          FormError(originalKey, originalWeightRangeMessageKey, rangeValueArgs),
          FormError(amendedKey, amendedWeightRangeMessageKey, rangeValueArgs)
        )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = weightAmendedValue.toString, amended = weightAmendedValue.toString),
          box = BoxNumber.Box35
        ).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data for a weightForm box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = weightOriginalValue.toString, amended = weightAmendedValue.toString),
          box = BoxNumber.Box35
        )
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = weightOriginalValue.toString, amended = weightAmendedValue.toString),
          box = BoxNumber.Box35
        )
        form.value mustBe Some(UnderpaymentReasonValue(weightOriginalValue.toString, weightAmendedValue.toString))
      }
    }
  }

  "A weight form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue =
          UnderpaymentReasonValue(weightOriginalValue.toString, weightAmendedValue.toString)
        val form: Form[UnderpaymentReasonValue] =
          new UnderpaymentReasonAmendmentFormProvider()(BoxNumber.Box35).fill(model)
        form.data mustBe formBuilder(original = weightOriginalValue.toString, amended = weightAmendedValue.toString)
      }
    }
  }

  "Binding a form with invalid data for a unit box selected" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = BoxNumber.Box41).errors mustBe Seq(
          FormError(originalKey, originalUnitMissingMessageKey),
          FormError(amendedKey, amendedUnitMissingMessageKey)
        )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = unitOriginalValue.toString), box = BoxNumber.Box41).errors mustBe
          Seq(
            FormError(amendedKey, amendedUnitMissingMessageKey)
          )
      }
    }

    "non numeric values provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = nonNumeric, amended = nonNumeric),
          box = BoxNumber.Box41
        ).errors mustBe Seq(
          FormError(originalKey, originalUnitFormatMessageKey),
          FormError(amendedKey, amendedUnitFormatMessageKey)
        )
      }
    }

    "non numeric amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = unitOriginalValue.toString, amended = nonNumeric),
          box = BoxNumber.Box41
        ).errors mustBe
          Seq(
            FormError(amendedKey, amendedUnitFormatMessageKey)
          )
      }
    }

    "too many decimal values provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = tooManyDecimalValue.toString, amended = tooManyDecimalValue.toString),
          box = BoxNumber.Box41
        ).errors mustBe Seq(
          FormError(originalKey, originalUnitDecimalMessageKey),
          FormError(amendedKey, amendedUnitDecimalMessageKey)
        )
      }
    }

    "too many decimal original value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = tooManyDecimalValue.toString, amended = unitAmendedValue.toString),
          box = BoxNumber.Box41
        ).errors mustBe
          Seq(
            FormError(originalKey, originalUnitDecimalMessageKey)
          )
      }
    }

    "too many decimal amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = unitOriginalValue.toString, amended = tooManyDecimalValue.toString),
          box = BoxNumber.Box41
        ).errors mustBe
          Seq(
            FormError(amendedKey, amendedUnitDecimalMessageKey)
          )
      }
    }

    "out of range values provided" should {
      "result in a form with errors" in {
        val rangeValueArgs = Seq(0, 9999999.999)
        formBinderBox(
          formBuilder(original = outOfRangeValue.toString, amended = outOfRangeValue.toString),
          box = BoxNumber.Box41
        ).errors mustBe Seq(
          FormError(originalKey, originalUnitRangeMessageKey, rangeValueArgs),
          FormError(amendedKey, amendedUnitRangeMessageKey, rangeValueArgs)
        )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = unitAmendedValue.toString, amended = unitAmendedValue.toString),
          box = BoxNumber.Box41
        ).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "Binding a form with valid data for a unitForm box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = unitOriginalValue.toString, amended = unitAmendedValue.toString),
          box = BoxNumber.Box41
        )
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = unitOriginalValue.toString, amended = unitAmendedValue.toString),
          box = BoxNumber.Box41
        )
        form.value mustBe Some(UnderpaymentReasonValue(unitOriginalValue.toString, unitAmendedValue.toString))
      }
    }
  }

  "A unit form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue =
          UnderpaymentReasonValue(unitOriginalValue.toString, unitAmendedValue.toString)
        val form: Form[UnderpaymentReasonValue] =
          new UnderpaymentReasonAmendmentFormProvider()(BoxNumber.Box41).fill(model)
        form.data mustBe formBuilder(original = unitOriginalValue.toString, amended = unitAmendedValue.toString)
      }
    }
  }

  "Binding a form with valid data for a decimal box selected" when {
    "provided with valid values" should {
      "result in a form with no errors" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = decimalOriginalValue.toString, amended = decimalAmendedValue.toString),
          box = BoxNumber.Box42
        )
        form.hasErrors mustBe false
      }

      "generate the correct model" in {
        val form: Form[UnderpaymentReasonValue] = formBinderBox(
          formBuilder(original = decimalOriginalValue.toString, amended = decimalAmendedValue.toString),
          box = BoxNumber.Box42
        )
        form.value mustBe Some(UnderpaymentReasonValue(decimalOriginalValue.toString, decimalAmendedValue.toString))
      }
    }
  }

  "Binding a form with invalid data for a decimal box selected" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinderBox(box = BoxNumber.Box42).errors mustBe Seq(
          FormError(originalKey, originalDecimalMissingMessageKey),
          FormError(amendedKey, amendedDecimalMissingMessageKey)
        )
      }
    }

    "no amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(formBuilder(original = decimalOriginalValue.toString), box = BoxNumber.Box42).errors mustBe
          Seq(
            FormError(amendedKey, amendedDecimalMissingMessageKey)
          )
      }
    }

    "non numeric values provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = nonNumeric, amended = nonNumeric),
          box = BoxNumber.Box42
        ).errors mustBe Seq(
          FormError(originalKey, originalDecimalFormatMessageKey),
          FormError(amendedKey, amendedDecimalFormatMessageKey)
        )
      }
    }

    "non numeric amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = decimalOriginalValue.toString, amended = nonNumeric),
          box = BoxNumber.Box42
        ).errors mustBe
          Seq(
            FormError(amendedKey, amendedDecimalFormatMessageKey)
          )
      }
    }

    "too many decimal values provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = tooManyDecimalValue.toString, amended = tooManyDecimalValue.toString),
          box = BoxNumber.Box42
        ).errors mustBe Seq(
          FormError(originalKey, originalDecimalInvalidDecimalMessageKey),
          FormError(amendedKey, amendedDecimalInvalidDecimalMessageKey)
        )
      }
    }

    "too many decimal original value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = tooManyDecimalValue.toString, amended = decimalAmendedValue.toString),
          box = BoxNumber.Box42
        ).errors mustBe
          Seq(
            FormError(originalKey, originalDecimalInvalidDecimalMessageKey)
          )
      }
    }

    "too many decimal amended value provided" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = decimalOriginalValue.toString, amended = tooManyDecimalValue.toString),
          box = BoxNumber.Box42
        ).errors mustBe
          Seq(
            FormError(amendedKey, amendedDecimalInvalidDecimalMessageKey)
          )
      }
    }

    "out of range values provided" should {
      "result in a form with errors" in {
        val bigDecimal: BigDecimal = 999999999999.99
        val rangeValueArgs         = Seq(0, bigDecimal)
        formBinderBox(
          formBuilder(original = decimalOutOfRangeValue.toString, amended = decimalOutOfRangeValue.toString),
          box = BoxNumber.Box42
        ).errors mustBe Seq(
          FormError(originalKey, originalDecimalRangeMessageKey, rangeValueArgs),
          FormError(amendedKey, amendedDecimalRangeMessageKey, rangeValueArgs)
        )
      }
    }

    "original and amended value are the same" should {
      "result in a form with errors" in {
        formBinderBox(
          formBuilder(original = decimalOriginalValue.toString, amended = decimalOriginalValue.toString),
          box = BoxNumber.Box42
        ).errors mustBe
          Seq(
            FormError("", keysDifferentMessageKey)
          )
      }
    }
  }

  "A decimal form " when {
    "built from a valid model" should {
      "generate the correct mapping" in {
        val model: UnderpaymentReasonValue =
          UnderpaymentReasonValue(decimalOriginalValue.toString, decimalAmendedValue.toString)
        val form: Form[UnderpaymentReasonValue] =
          new UnderpaymentReasonAmendmentFormProvider()(BoxNumber.Box42).fill(model)
        form.data mustBe formBuilder(original = decimalOriginalValue.toString, amended = decimalAmendedValue.toString)
      }
    }
  }

  "different Constraint" must {

    lazy val diff = new UnderpaymentReasonAmendmentFormProvider().different("error.key")

    "return Valid if strings is different" in {
      diff(UnderpaymentReasonValue("Field1", "NotField1")) mustEqual Valid
    }

    "return Invalid for identical strings" in {
      diff(UnderpaymentReasonValue("Field1", "Field1")) mustEqual Invalid("error.key")
    }
  }

  "minMaxRange Constraint" must {

    val minAmount = BigDecimal(0)
    val maxAmount = BigDecimal(100)
    lazy val rangeResult =
      new UnderpaymentReasonAmendmentFormProvider().minMaxRange(Some(minAmount), Some(maxAmount), "error.key")
    lazy val minResult: Constraint[BigDecimal] =
      new UnderpaymentReasonAmendmentFormProvider().minMaxRange(Some(minAmount), None, "error.key")
    lazy val maxResult = new UnderpaymentReasonAmendmentFormProvider().minMaxRange(None, Some(maxAmount), "error.key")

    "return Valid if value is greater than Min" in {
      minResult(BigDecimal(5)) mustEqual Valid
    }

    "return Invalid if value is less than Min" in {
      minResult(BigDecimal(-1)) mustEqual Invalid("error.key", minAmount)
    }

    "return Valid if value is less than Max" in {
      maxResult(BigDecimal(5)) mustEqual Valid
    }

    "return Invalid if value is greater than Max" in {
      maxResult(BigDecimal(101)) mustEqual Invalid("error.key", maxAmount)
    }

    "return Valid if value is in Range" in {
      rangeResult(BigDecimal(5)) mustEqual Valid
    }

    "return Invalid if value is greater than Range" in {
      rangeResult(BigDecimal(101)) mustEqual Invalid("error.key", minAmount, maxAmount)
    }

    "return Invalid if value is less than Range" in {
      rangeResult(BigDecimal(-1)) mustEqual Invalid("error.key", minAmount, maxAmount)
    }
  }

  "invalid format for other reason" should {
    "result in a required error" in {
      formBinderBox(formBuilder(original = ""), box = BoxNumber.OtherItem).errors mustBe
        Seq(FormError(originalKey, "otherReason.error.required"))
    }

    "result in a no emoji error" in {
      formBinderBox(formBuilder(original = "😀"), box = BoxNumber.OtherItem).errors mustBe
        Seq(FormError(originalKey, "otherReason.error.noEmoji"))
    }

    "result in a max length error" in {
      val maxLengthData: String = "c" * 1502
      formBinderBox(formBuilder(original = maxLengthData), box = BoxNumber.OtherItem).errors mustBe
        Seq(FormError(originalKey, "otherReason.error.maxLength", Seq(1500)))
    }
  }
}
