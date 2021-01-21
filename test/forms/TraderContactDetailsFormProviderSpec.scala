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
import models.TraderContactDetails
import play.api.data.{Form, FormError}

class TraderContactDetailsFormProviderSpec extends SpecBase {

  private final val fullName = "fullName"
  private final val email = "email"
  private final val phoneNumber = "phoneNumber"
  private final val exampleName = "First Second"
  private final val exampleEmail = "email@email.com"
  private final val examplePhoneNumber = "0123456789"
  private final val fullNameNonEmptyKey = "traderContactDetails.error.nameNonEmpty"
  private final val emailNonEmptyKey = "traderContactDetails.error.emailNonEmpty"
  private final val phoneNumberNonEmptyKey = "traderContactDetails.error.phoneNumberNonEmpty"
  private final val fullNameTooShortKey = "traderContactDetails.error.nameMinLength"
  private final val fullNameTooLongKey = "traderContactDetails.error.nameMaxLength"
  private final val fullNameInvalidCharactersKey = "traderContactDetails.error.nameAllowableCharacters"

  def formBuilder(fullName: String = "", email: String = "", phoneNumber: String = ""): Map[String, String] = Map(
    "fullName" -> fullName,
    "email" -> email,
    "phoneNumber" -> phoneNumber
  )

  def formBinder(formValues: Map[String, String] = Map(fullName -> "", email -> "", phoneNumber -> "")): Form[TraderContactDetails] =
    new TraderContactDetailsFormProvider()(MockAppConfig).apply().bind(formValues)

  "Binding a form with invalid data" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinder().errors mustBe Seq(
          FormError(fullName, fullNameNonEmptyKey),
          FormError(email, emailNonEmptyKey),
          FormError(phoneNumber, phoneNumberNonEmptyKey)
        )
      }
    }

    "full name too short value" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(
            fullName = "a",
            email = exampleEmail,
            phoneNumber = examplePhoneNumber
          )
        ).errors mustBe Seq(FormError(fullName, fullNameTooShortKey))
      }
    }

    "full name too long value" should {
      "result in a form with errors" in {
        val longString = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        formBinder(
          formBuilder(
            fullName = longString,
            email = exampleEmail,
            phoneNumber = examplePhoneNumber
          )
        ).errors mustBe Seq(FormError(fullName, fullNameTooLongKey))
      }
    }

    "full name contains invalid characters value" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(
            fullName = "First Last/",
            email = exampleEmail,
            phoneNumber = examplePhoneNumber
          )
        ).errors mustBe Seq(FormError(fullName, fullNameInvalidCharactersKey, Seq("^[a-zA-Z '-]+$")))
      }
    }

  }


  "Binding a form with valid data" should {
    val form = formBinder(formBuilder(fullName = exampleName, email = exampleEmail, phoneNumber = examplePhoneNumber))

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

    "generate the correct model" in {
      form.value mustBe Some(TraderContactDetails(exampleName, exampleEmail, examplePhoneNumber))
    }

  }

  "A form built from a valid model" should {
    "generate the correct mapping" in {
      val model = TraderContactDetails(exampleName, exampleEmail, examplePhoneNumber)
      val form = new TraderContactDetailsFormProvider()(MockAppConfig).apply().fill(model)
      form.data mustBe formBuilder("First Second", "email@email.com", "0123456789")
    }
  }
}
