/*
 * Copyright 2022 HM Revenue & Customs
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

package filters

import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub}
import support.IntegrationSpec

class AllowListFilterISpec extends IntegrationSpec {

  override def overriddenConfig: Map[String, Any] = super.overriddenConfig + ("features.allowListActive" -> true)

  "calling an existing route when the filter is enabled" should {

    "return a Not Found response" in {

      AuditStub.audit()
      AuthStub.authorised()

      val request: WSRequest   = buildRequest("/disclosure/entry-details")
      val response: WSResponse = await(request.get())

      response.status shouldBe Status.NOT_FOUND

    }

  }

}
