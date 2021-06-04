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

package pages

import models.{FileUploadInfo, SelectedDutyTypes, UploadAuthority, UserAnswers}
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json
import java.time.LocalDateTime

class SplitPaymentPageSpec extends WordSpec with MustMatchers {
  "SplitPayment page" when {
    "choosing not to pay by split deferment" should {
      "remove answers relating to split deferment" in {
        val fileDetails = FileUploadInfo("file.txt", "blah", LocalDateTime.now(), "some-sum", "mime-type")
        val uploadAuthority = UploadAuthority("A1234567", SelectedDutyTypes.Both, fileDetails)
        val splitDeferment = false
        val answers: UserAnswers = UserAnswers("some-cred")
          .set(DefermentTypePage, "A").success.value
          .set(DefermentAccountPage, "1234567").success.value
          .set(AdditionalDefermentTypePage, "A").success.value
          .set(AdditionalDefermentNumberPage, "1234567").success.value
          .set(UploadAuthorityPage, Seq(uploadAuthority)).success.value
        val answersAfterCleanUp = SplitPaymentPage.cleanup(Some(splitDeferment), answers).success.value
        val expectedAnswers = answers.copy(data = Json.obj())
        answersAfterCleanUp mustBe expectedAnswers
      }
    }
    "choosing to pay by split deferment" should {
      "not remove/update the answers" in {
        val fileDetails = FileUploadInfo("file.txt", "blah", LocalDateTime.now(), "some-sum", "mime-type")
        val uploadAuthority = UploadAuthority("A1234567", SelectedDutyTypes.Both, fileDetails)
        val splitDeferment = true
        val answers: UserAnswers = UserAnswers("some-cred")
          .set(DefermentTypePage, "A").success.value
          .set(DefermentAccountPage, "1234567").success.value
          .set(AdditionalDefermentTypePage, "A").success.value
          .set(AdditionalDefermentNumberPage, "1234567").success.value
          .set(UploadAuthorityPage, Seq(uploadAuthority)).success.value
        val answersAfterCleanUp = SplitPaymentPage.cleanup(Some(splitDeferment), answers).success.value
        answersAfterCleanUp mustBe answers
      }
    }
  }
}
