/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package models.upscan

import play.api.libs.json.{Json, Writes}

case class UpScanInitiateRequest(callbackUrl: String,
                                 successRedirect: String,
                                 errorRedirect: String,
                                 minimumFileSize: Int,
                                 maximumFileSize: Int
                             )
object UpScanInitiateRequest {
  implicit val jsonWrites: Writes[UpScanInitiateRequest] = Json.writes[UpScanInitiateRequest]
}
