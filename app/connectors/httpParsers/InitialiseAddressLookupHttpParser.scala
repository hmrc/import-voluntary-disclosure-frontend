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

package connectors.httpParsers

import connectors.httpParsers.ResponseHttpParser.HttpPostResult
import models.ErrorModel
import models.addressLookup.AddressLookupOnRampModel
import play.api.http.HeaderNames.LOCATION
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object InitialiseAddressLookupHttpParser {

  implicit object InitialiseAddressLookupReads extends HttpReads[HttpPostResult[AddressLookupOnRampModel]] {

    override def read(method: String, url: String, response: HttpResponse): HttpPostResult[AddressLookupOnRampModel] = {

      response.status match {
        case Status.ACCEPTED =>
          response.header(LOCATION) match {
            case Some(redirectUrl) => Right(AddressLookupOnRampModel(redirectUrl))
            case _ =>
              Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Response Header did not contain location redirect"))
          }
        case status =>
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Downstream error returned from Address Lookup"))
      }
    }
  }
}
