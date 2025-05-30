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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.Json
import support.WireMockMethods
import uk.gov.hmrc.auth.core.AffinityGroup

object AuthStub extends WireMockMethods {

  private val authoriseUri = "/auth/authorise"

  def authorised(): StubMapping =
    when(method = POST, uri = authoriseUri)
      .thenReturn(
        status = OK,
        body = Json.obj(
          "externalId" -> "some_external_id",
          "allEnrolments" -> Json.arr(
            Json.obj(
              "key" -> "HMRC-CTS-ORG",
              "identifiers" -> Json.arr(
                Json.obj(
                  "key"   -> "EORINumber",
                  "value" -> "GB987654321000"
                )
              ),
              "state" -> "Activated"
            )
          ),
          "affinityGroup" -> Json.toJson[AffinityGroup](AffinityGroup.Organisation)
        )
      )

  def unauthorised(): StubMapping =
    when(method = POST, uri = authoriseUri).thenReturn(status = UNAUTHORIZED)

}
