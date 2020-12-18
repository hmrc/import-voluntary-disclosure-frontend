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

package connectors.httpParsers

import models.responses.UpScanInitiateResponseModel
import play.api.Logger
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.JsSuccess
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object UpScanInitiateHttpParser {

  type UpScanInitiateResponse = Either[ErrorResponse, UpScanInitiateResponseModel]
  private val logger = Logger("application." + getClass.getCanonicalName)

  implicit object UpScanInitiateResponseReads extends HttpReads[UpScanInitiateResponse] {

    def read(method: String, url: String, response: HttpResponse): UpScanInitiateResponse = {
      (response.status match {
        case OK =>
          response.json.validate[UpScanInitiateResponseModel](UpScanInitiateResponseModel.jsonReadsForUpScanInitiateResponseModel) match {
            case JsSuccess(model, _) => Right(model)
            case _ => Left(InvalidJson)
          }
        case BAD_REQUEST =>
          logger.debug(s"[UpScanInitiateResponseReads][read]: Bad request returned with reason: ${response.body}")
          Left(BadRequest)
        case status =>
          logger.warn(s"[UpScanInitiateResponseReads][read]: Unexpected response, status $status returned")
          Left(UnexpectedFailure(status, s"Unexpected response, status $status returned"))
      })
    }
  }

  trait ErrorResponse {
    val status: Int
    val body: String
  }

  case class UnexpectedFailure(override val status: Int, override val body: String) extends ErrorResponse

  case object InvalidJson extends ErrorResponse {
    override val status: Int = BAD_REQUEST
    override val body = "Invalid JSON received"
  }

  case object BadRequest extends ErrorResponse {
    override val status: Int = BAD_REQUEST
    override val body: String = "Json body sent incorrect"
  }

}
