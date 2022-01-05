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

package support

import java.net.URLEncoder
import java.util.UUID

import play.api.Application
import play.api.libs.crypto.CookieSigner
import uk.gov.hmrc.crypto.{CompositeSymmetricCrypto, PlainText}
import uk.gov.hmrc.http.SessionKeys

object SessionCookieBaker {
  private val cookieKey = "gvBoGdgzqG1AarzF1LY0zQ=="
  val sessionId = s"stubbed-${UUID.randomUUID}"

  private def cookieValue(sessionData: Map[String, String])(implicit app: Application) = {
    def encode(data: Map[String, String]): PlainText = {
      val encoded = data.map {
        case (k, v) => URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8")
      }.mkString("&")
      val key = "yNhI04vHs9<_HWbC`]20u`37=NGLGYY5:0Tg5?y`W<NoJnXWqmjcgZBec@rOxb^G".getBytes

      val cookieSignerCache: Application => CookieSigner = Application.instanceCache[CookieSigner]
      def cookieSigner: CookieSigner = cookieSignerCache(app)

      PlainText(cookieSigner.sign(encoded, key) + "-" + encoded)
    }

    val encodedCookie = encode(sessionData)
    val encrypted = CompositeSymmetricCrypto.aesGCM(cookieKey, Seq()).encrypt(encodedCookie).value

    s"""mdtp="$encrypted"; Path=/; HTTPOnly"; Path=/; HTTPOnly"""
  }

  private def cookieData(additionalData: Map[String, String], timeStampRollback: Long): Map[String, String] = {

    val timeStamp = new java.util.Date().getTime
    val rollbackTimestamp = (timeStamp - timeStampRollback).toString

    Map(
      SessionKeys.sessionId -> sessionId,
      SessionKeys.authToken -> "token",
      SessionKeys.lastRequestTimestamp -> rollbackTimestamp,
      SessionKeys.authToken -> "auth"
    ) ++ additionalData
  }

  def bakeSessionCookie(additionalData: Map[String, String] = Map(), timeStampRollback: Long = 0)(implicit app: Application): String = {
    cookieValue(cookieData(additionalData, timeStampRollback))
  }
}
