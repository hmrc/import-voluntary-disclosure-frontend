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

package utils

import java.time.Month

import play.api.libs.json.{Format, JsNull, JsObject, JsResult, JsValue, Json}

import scala.language.implicitConversions

trait JsonUtils {

  def jsonObjNoNulls(fields: (String, Json.JsValueWrapper)*): JsObject =
    JsObject(Json.obj(fields:_*).fields.filterNot(_._2 == JsNull).filterNot(_._2 == Json.obj()))

  implicit val monthFormat: Format[Month] = new Format[Month] {
    override def reads(json: JsValue): JsResult[Month] =
      json.validate[String].map(Month.valueOf)

    override def writes(month: Month): JsValue =
      Json.toJson(month.toString)
  }
}
