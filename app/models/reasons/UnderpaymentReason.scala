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

package models.reasons

import play.api.libs.json.{Json, OFormat}

case class UnderpaymentReason(
  boxNumber: BoxNumber.Value,
  itemNumber: Int = 0,
  original: String,
  amended: String
)

object UnderpaymentReason {

  implicit val format: OFormat[UnderpaymentReason] = Json.format[UnderpaymentReason]
}

case class ChangeUnderpaymentReason(
  original: UnderpaymentReason,
  changed: UnderpaymentReason
)

object ChangeUnderpaymentReason {

  implicit val format: OFormat[ChangeUnderpaymentReason] = Json.format[ChangeUnderpaymentReason]
}
