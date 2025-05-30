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

package models

sealed trait OptionalDocument

object OptionalDocument extends Enumerable.Implicits[OptionalDocument] {

  case object ImportAndEntry extends WithName("importAndEntry") with OptionalDocument

  case object AirwayBill extends WithName("airwayBill") with OptionalDocument

  case object OriginProof extends WithName("originProof") with OptionalDocument

  case object Other extends WithName("other") with OptionalDocument

  val values: Seq[OptionalDocument] = Seq(
    ImportAndEntry,
    AirwayBill,
    OriginProof,
    Other
  )

  implicit val enumerable: Enumerable[OptionalDocument] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
