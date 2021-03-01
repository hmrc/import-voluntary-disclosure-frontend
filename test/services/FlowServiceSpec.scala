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

package services

import base.SpecBase
import models.UserType.{Importer, Representative}
import models.{UserAnswers, UserType}
import pages.UserTypePage


class FlowServiceSpec extends SpecBase {

  trait Test {
    lazy val userType: UserType = Importer
    lazy val service = new FlowService

    def setupUserAnswers = UserAnswers("some-cred-id").set(UserTypePage, userType).success.value
  }

  "isRep call" should {

    "return true for representative journey" in new Test {
      override lazy val userType = Representative
      service.isRepFlow(setupUserAnswers) mustBe true
    }

    "return false for Importer journey" in new Test {
      override lazy val userType = Importer
      service.isRepFlow(setupUserAnswers) mustBe false
    }
  }
}
