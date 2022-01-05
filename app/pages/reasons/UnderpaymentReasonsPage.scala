/*
 * Copyright 2022 HM Revenue & Customs
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

package pages.reasons

import models.UserAnswers
import models.reasons.UnderpaymentReason
import pages.QuestionPage
import play.api.libs.json.{JsArray, JsPath, Json, Writes}

import scala.util.Try

object UnderpaymentReasonsPage extends QuestionPage[Seq[UnderpaymentReason]] {

  override def path: JsPath = JsPath \ "underpayment-reasons"

  def arrayWrites: Writes[Seq[UnderpaymentReason]] =
    (reasons: Seq[UnderpaymentReason]) =>
      JsArray(reasons.map { reason =>
        Json.toJson(reason)
      })

  override def cleanup(value: Option[Seq[UnderpaymentReason]], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(answer) =>
        val boxNumber          = userAnswers.remove(UnderpaymentReasonBoxNumberPage).getOrElse(userAnswers)
        val itemNumber         = boxNumber.remove(UnderpaymentReasonItemNumberPage).getOrElse(userAnswers)
        val originalAndAmended = itemNumber.remove(UnderpaymentReasonAmendmentPage).getOrElse(userAnswers)
        Try(originalAndAmended)
      case None => super.cleanup(value, userAnswers)
    }
  }

}
