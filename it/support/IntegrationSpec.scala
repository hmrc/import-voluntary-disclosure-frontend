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

package support

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, EitherValues}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.api.{Application, Environment, Mode}

trait IntegrationSpec
  extends AnyWordSpec
    with EitherValues
    with Matchers
    with FutureAwaits
    with DefaultAwaitTimeout
    with WireMockHelper
    with GuiceOneServerPerSuite
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  val mockHost: String = WireMockHelper.host
  val mockPort: String = WireMockHelper.wireMockPort.toString

  lazy val client: WSClient = app.injector.instanceOf[WSClient]

  private val servicesPath = "microservice.services"

  def overriddenConfig: Map[String, Any] = Map(
    s"$servicesPath.auth.host" -> mockHost,
    s"$servicesPath.auth.port" -> mockPort,
    s"$servicesPath.address-lookup-frontend.host" -> mockHost,
    s"$servicesPath.address-lookup-frontend.port" -> mockPort,
    "auditing.consumer.baseUri.port" -> mockPort
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(overriddenConfig)
    .build()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  def document(response: WSResponse): JsValue = Json.parse(response.body)

  private def bakeCookie(sessionData: (String, String)*): (String, String) =
    HeaderNames.COOKIE -> SessionCookieBaker.bakeSessionCookie(sessionData.toMap)

  def buildRequest(path: String): WSRequest =
    client.url(s"http://localhost:$port/disclose-import-taxes-underpayment$path")
      .withHttpHeaders(bakeCookie())
      .withFollowRedirects(false)

}
