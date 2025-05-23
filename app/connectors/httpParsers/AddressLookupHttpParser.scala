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

import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import models.ErrorModel
import models.addressLookup.AddressModel
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object AddressLookupHttpParser {

  implicit object AddressLookupReads extends HttpReads[HttpGetResult[AddressModel]] {

    private val logger = Logger("application." + getClass.getCanonicalName)

    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[AddressModel] = {

      response.status match {
        case Status.OK =>
          response.json.validate[AddressModel](AddressModel.customerAddressReads).fold(
            invalid => {
              logger.error("Failed to validate JSON with errors: " + invalid)
              Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json returned from Address Lookup"))
            },
            valid => Right(valid)
          )
        case status =>
          logger.error("Failed to validate JSON with status: " + status + " body: " + response.body)
          Left(ErrorModel(status, "Downstream error returned when retrieving CustomerAddressModel from AddressLookup"))
      }
    }
  }

}
