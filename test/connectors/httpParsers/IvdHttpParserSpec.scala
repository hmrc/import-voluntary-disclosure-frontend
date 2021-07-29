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

import connectors.httpParsers.IvdHttpParser.{EoriDetailsReads, SubmissionResponseReads, UpdateResponseReads}
import models._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import utils.ReusableValues

class IvdHttpParserSpec extends AnyWordSpec with Matchers with ReusableValues {

  val eoriDetailsJson: EoriDetails = EoriDetails(
    "GB987654321000",
    "Fast Food ltd",
    ContactAddress(
      addressLine1 = "99 Avenue Road",
      addressLine2 = None,
      city = "Anyold Town",
      postalCode = Some("99JZ 1AA"),
      countryCode = "GB"
    ),
    Some("987654321000")
  )

  val submissionResponseJson: JsObject = Json.obj(
    "id" -> "1234567890"
  )

  val submissionResponseModel = SubmissionResponse("1234567890")

  val updateResponseJson: JsObject = Json.obj(
    "id" -> "1234567890"
  )

  val updateResponseModel = UpdateCaseResponse("1234567890")

  "IVD Submission HttpParser" when {

    "called to parse a Eori Details" should {
      "the http response status is OK and valid content" in {
        EoriDetailsReads.read("", "",
          HttpResponse(Status.OK, cleanedDetailsJson, Map.empty[String, Seq[String]])) mustBe Right(eoriDetails)
      }

      "the http response status is OK with valid Json" in {
        EoriDetailsReads.read("", "",
          HttpResponse(Status.OK, cleanedDetailsJson, Map.empty[String, Seq[String]])) mustBe Right(eoriDetailsJson)
      }

      "return an ErrorModel when invalid Json is returned" in {
        EoriDetailsReads.read("", "",
          HttpResponse(Status.OK, Json.obj(), Map.empty[String, Seq[String]])) mustBe
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json returned from SUB09 API for EoriDetailsHttpParser"))
      }

      "return an ErrorModel when NOT_FOUND is returned" in {
        EoriDetailsReads.read("", "",
          HttpResponse(Status.NOT_FOUND, "")) mustBe
          Left(ErrorModel(Status.NOT_FOUND,
            "Downstream error returned when retrieving EoriDetails model from back end"))
      }
    }

    "called to parse a Submission Response" should {
      "the http response status is OK with valid Json" in {
        SubmissionResponseReads.read("", "",
          HttpResponse(Status.OK, submissionResponseJson, Map.empty[String, Seq[String]])) mustBe Right(submissionResponseModel)
      }

      "return an ErrorModel when invalid Json is returned" in {
        SubmissionResponseReads.read("", "",
          HttpResponse(Status.OK, Json.obj(), Map.empty[String, Seq[String]])) mustBe
          Left(ErrorModel(Status.INTERNAL_SERVER_ERROR,
            "Invalid Json returned from IVD Submission"))
      }

      "return an ErrorModel when NOT_FOUND is returned" in {
        SubmissionResponseReads.read("", "",
          HttpResponse(Status.NOT_FOUND, "")) mustBe
          Left(ErrorModel(Status.NOT_FOUND,
            "Downstream error returned when retrieving SubmissionResponse from back end"))
      }
    }

    "called to parse an Update Response" should {
      "the http response status is OK with valid Json" in {
        UpdateResponseReads.read("", "",
          HttpResponse(Status.OK, updateResponseJson, Map.empty[String, Seq[String]])) mustBe Right(updateResponseModel)
      }

      "return the correct error when received BAD_REQUEST for InvalidCaseId" in {
        val response = HttpResponse(Status.BAD_REQUEST, Json.obj("errorCode" -> 1, "errorMessage" -> "Invalid case ID").toString())
        UpdateResponseReads.read("", "", response) mustBe Left(UpdateCaseError.InvalidCaseId)
      }

      "return the correct error when received BAD_REQUEST for CaseAlreadyClosed" in {
        val response = HttpResponse(Status.BAD_REQUEST, Json.obj("errorCode" -> 2, "errorMessage" -> "Case is already closed").toString())
        UpdateResponseReads.read("", "", response) mustBe Left(UpdateCaseError.CaseAlreadyClosed)
      }

      "return an error when NOT_FOUND is returned" in {
        UpdateResponseReads.read("", "",
          HttpResponse(Status.NOT_FOUND, "")) mustBe
          Left(UpdateCaseError.UnexpectedError(Status.NOT_FOUND, Some("Downstream error returned when retrieving UpdateResponse from back end")))
      }
    }

  }
}
