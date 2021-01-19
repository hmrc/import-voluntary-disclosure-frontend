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

import models.{UnderpaymentType, UserAnswers}
import play.api.libs.json.JsPath

import scala.util.Try

case object UnderpaymentTypePage extends QuestionPage[UnderpaymentType] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "underpaymentType"

  override def cleanup(value: Option[UnderpaymentType], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(answer) => {
        val cd = if (!answer.customsDuty) userAnswers.remove(CustomsDutyPage).getOrElse(userAnswers) else userAnswers
        val iv = if (!answer.importVAT) cd.remove(ImportVATPage).getOrElse(cd) else cd
        if (!answer.exciseDuty) iv.remove(ExciseDutyPage) else Try(iv)
      }
      case None => super.cleanup(value, userAnswers)
    }
  }
}
