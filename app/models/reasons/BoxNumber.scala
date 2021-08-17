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

package models.reasons

import play.api.libs.json.{Reads, Writes}

object BoxNumber extends Enumeration {
  type BoxNumber = Value

  val Box22: Value = Value(22)
  val Box33: Value = Value(33)
  val Box34: Value = Value(34) //
  val Box35: Value = Value(35) //
  val Box36: Value = Value(36)
  val Box37: Value = Value(37)
  val Box38: Value = Value(38)
  val Box39: Value = Value(39)
  val Box41: Value = Value(41)
  val Box42: Value = Value(42)
  val Box43: Value = Value(43)
  val Box45: Value = Value(45)
  val Box46: Value = Value(46)
  val Box62: Value = Value(62)
  val Box63: Value = Value(63)
  val Box66: Value = Value(66)
  val Box67: Value = Value(67)
  val Box68: Value = Value(68)
  val OtherItem: Value = Value(99)

  implicit val reads: Reads[BoxNumber.Value] = implicitly[Reads[Int]].map(BoxNumber.apply)
  implicit val writes: Writes[BoxNumber.Value] = implicitly[Writes[Int]].contramap(_.id)
}
