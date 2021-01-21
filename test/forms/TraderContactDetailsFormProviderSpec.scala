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
import models.{TraderContactDetails, UnderpaymentAmount}
import play.api.data.Form

class TraderContactDetailsFormProviderSpec extends SpecBase {

  private final val fullName = "fullName"
  private final val email = "email"
  private final val phoneNumber = "phoneNumber"
  private final val exampleName = "First Second"
  private final val exampleEmail = "email@email.com"
  private final val examplePhoneNumber = "0123456789"

  def formBuilder(fullName: String, email: String, phoneNumber: String): Map[String, String] = Map(
    "fullName" -> fullName,
    "email" -> email,
    "phoneNumber" -> phoneNumber
  )

  def formBinder(formValues: Map[String, String] = Map(fullName -> "", email -> "", phoneNumber -> "")): Form[TraderContactDetails] =
    new TraderContactDetailsFormProvider()(MockAppConfig).apply().bind(formValues)


  "Binding a form with valid data" should {
    val form = formBinder(formBuilder(fullName = exampleName, email = exampleEmail, phoneNumber = examplePhoneNumber ))

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
