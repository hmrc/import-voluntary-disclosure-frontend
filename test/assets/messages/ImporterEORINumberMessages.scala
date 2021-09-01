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

package messages

object ImporterEORINumberMessages extends BaseMessages {

  val title: String           = "What is the importer’s EORI number?"
  val h1: String              = "What is the importer’s EORI number?"
  val hint: String            =
    "This must start with GB and be followed by either 12 or 15 numbers, for example GB345834921000 or GB123456789123000."
  val nonEmpty: String        = "Enter an EORI number"
  val incorrectFormat: String = "Enter an EORI number in the correct format"

}
