/*
 * Copyright 2020 HM Revenue & Customs
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

import models.Enumerable

sealed trait FileStatusEnum
sealed trait UpscanError extends FileStatusEnum
sealed trait UpscanSuccess extends FileStatusEnum

object FileStatusEnum extends Enumerable.Implicits {

  case object READY extends UpscanSuccess
  case object FAILURE extends UpscanError
  case object NO_RESPONSE extends UpscanError

  val values: Seq[FileStatusEnum] = Seq(READY, FAILURE)

  implicit val enumerable: Enumerable[FileStatusEnum] =
    Enumerable(values.map(v => v.toString -> v): _*)
}