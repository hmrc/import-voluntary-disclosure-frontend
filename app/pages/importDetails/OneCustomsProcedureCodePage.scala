/*
 * Copyright 2025 HM Revenue & Customs
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

package pages.importDetails

import models.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath

import scala.util.Try

case object OneCustomsProcedureCodePage extends QuestionPage[Boolean] {

  def path: JsPath = JsPath \ toString

  override def toString: String = "one-customs-procedure-code"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    if (value.get) {
      Try(userAnswers)
    } else {
      Try(userAnswers.remove(EnterCustomsProcedureCodePage).getOrElse(userAnswers))
    }
  }
}
