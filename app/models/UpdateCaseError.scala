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

package models

import play.api.libs.json.Reads

sealed trait UpdateCaseError extends Product with Serializable

object UpdateCaseError {
  case object InvalidCaseId extends UpdateCaseError

  case object CaseAlreadyClosed extends UpdateCaseError

  final case class UnexpectedError(status: Int, message: Option[String]) extends UpdateCaseError

  implicit val reads: Reads[UpdateCaseError] =
    Reads { json =>
      for {
        code    <- (json \ "errorCode").validate[Int]
        message <- (json \ "errorMessage").validateOpt[String]
      } yield {
        code match {
          case 1 => UpdateCaseError.InvalidCaseId
          case 2 => UpdateCaseError.CaseAlreadyClosed
          case 3 => UpdateCaseError.UnexpectedError(code, message)
        }
      }
    }
}
