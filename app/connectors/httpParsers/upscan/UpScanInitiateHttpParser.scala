/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package connectors.httpParsers.upscan

import models.upscan.UpScanInitiateResponse
import play.api.Logger
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.JsSuccess
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object UpScanInitiateHttpParser {
  type UpscanInitiateResponse = Either[ErrorResponse, UpScanInitiateResponse]

  implicit object UpScanInitiateResponseReads extends HttpReads[UpscanInitiateResponse] {

    def read(method: String, url: String, response: HttpResponse): UpscanInitiateResponse = {
      (response.status match {
        case OK =>
          response.json.validate[UpScanInitiateResponse](UpScanInitiateResponse.jsonReadsForUpScanInitiateResponse) match {
            case JsSuccess(model, _) => Right(model)
            case _ => Left(InvalidJson)
          }
        case BAD_REQUEST =>
          Logger.debug(s"[UpScanInitiateResponseReads][read]: Bad request returned with reason: ${response.body}")
          Left(BadRequest)
        case status =>
          Logger.warn(s"[UpScanInitiateResponseReads][read]: Unexpected response, status $status returned")
          Left(UnexpectedFailure(status, s"Unexpected response, status $status returned"))
      })
    }
  }

  trait ErrorResponse {
    val status: Int
    val body: String
  }

  case object InvalidJson extends ErrorResponse {
    override val status = BAD_REQUEST
    override val body = "Invalid JSON received"
  }

  case object BadRequest extends ErrorResponse {
    override val status: Int = BAD_REQUEST
    override val body: String = "Json body sent incorrect"
  }

  case class UnexpectedFailure(override val status: Int, override val body: String) extends ErrorResponse
}
