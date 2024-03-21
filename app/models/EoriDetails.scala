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

import play.api.libs.json.{Json, OFormat, Reads, __}

case class EoriDetails(eori: String, name: String, address: ContactAddress, vatNumber: Option[String])

object EoriDetails {

  implicit val reads: Reads[EoriDetails] = for {
    eori      <- (__ \\ "eori").read[String]
    name      <- (__ \\ "name").read[String]
    address   <- __.read[ContactAddress](ContactAddress.sub09Reads)
    vatNumber <- (__ \\ "vatNumber").readNullable[String]
  } yield EoriDetails(eori, name, address, vatNumber)

  implicit val format: OFormat[EoriDetails] = Json.format[EoriDetails]

}
