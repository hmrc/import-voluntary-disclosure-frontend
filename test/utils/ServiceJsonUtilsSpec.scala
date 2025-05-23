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

package utils

import base.SpecBase
import play.api.libs.json.Json
import services.ServiceJsonUtils._

class ServiceJsonUtilsSpec extends SpecBase {
  "dropNullValues" should {
    "remove null values from nested JSON objects" in {
      val json = Json.obj(
        "aa" -> 1,
        "ab" -> Json.parse("null"),
        "bb" -> 2,
        "ba" -> Json.obj("aa" -> Json.obj("aa" -> 1, "ab" -> Json.parse("null")))
      )

      json.dropNullValues mustBe
        Json.obj("aa" -> 1, "bb" -> 2, "ba" -> Json.obj("aa" -> Json.obj("aa" -> 1)))
    }

    "remove None values from nested JSON objects" in {
      val json = Json.obj(
        "aa" -> Some(1),
        "ab" -> None,
        "bb" -> 2,
        "ba" -> Json.obj("aa" -> Json.obj("aa" -> Some(1), "ab" -> None))
      )

      json.dropNullValues mustBe
        Json.obj("aa" -> 1, "bb" -> 2, "ba" -> Json.obj("aa" -> Json.obj("aa" -> 1)))
    }
  }
}
