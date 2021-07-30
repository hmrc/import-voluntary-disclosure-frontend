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

package connectors.httpParsers

import assets.AddressLookupTestConstants._
import connectors.httpParsers.AddressLookupHttpParser.AddressLookupReads
import models.ErrorModel
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse

class AddressLookupHttpParserSpec extends AnyWordSpec with Matchers {

  val errorModel: ErrorModel = ErrorModel(Status.BAD_REQUEST, "Error Message")

  "The AddressLookupHttpParser" when {

    "the http response status is OK with valid Json" should {

      "return a AddressLookupModel" in {
        AddressLookupReads.read("", "",
          HttpResponse(Status.OK, customerAddressJsonMin, Map.empty[String, Seq[String]])) mustBe Right(customerAddressMin)
      }
    }

    "the http response status is OK with invalid Json" should {

      "return an ErrorModel" in {
        AddressLookupReads.read("", "",
          HttpResponse(Status.OK, customerAddressJsonError, Map.empty[String, Seq[String]])) mustBe
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json returned from Address Lookup"))
      }
    }

    "the http response status is BAD_REQUEST" should {

      "return an ErrorModel" in {
        AddressLookupReads.read("", "",
          HttpResponse(Status.BAD_REQUEST, "")) mustBe
          Left(ErrorModel(Status.BAD_REQUEST, "Downstream error returned when retrieving CustomerAddressModel from AddressLookup"))
      }
    }

    "the http response status unexpected" should {

      "return an ErrorModel" in {
        AddressLookupReads.read("", "",
          HttpResponse(Status.SEE_OTHER, "")) mustBe
          Left(ErrorModel(Status.SEE_OTHER, "Downstream error returned when retrieving CustomerAddressModel from AddressLookup"))
      }
    }
  }
}
