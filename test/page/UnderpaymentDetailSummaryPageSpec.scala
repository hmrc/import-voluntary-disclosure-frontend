/*
 * Copyright 2023 HM Revenue & Customs
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

package page

import base.SpecBase
import models.UserAnswers
import pages.underpayments.UnderpaymentDetailSummaryPage

import scala.util.Success

class UnderpaymentDetailSummaryPageSpec extends SpecBase {

  "cleanup" when {

    "there is no value for UnderpaymentDetail" must {
      "return the original UserAnswers without data for UnderpaymentDetails" in {

        val userAnswers = UserAnswers("123")

        val page = UnderpaymentDetailSummaryPage
        page.cleanup(None, userAnswers) mustBe Success(userAnswers)
      }
    }
  }

}
