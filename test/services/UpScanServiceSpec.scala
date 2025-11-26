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

package services

import base.ServiceSpecBase
import connectors.UpScanConnector
import connectors.httpParsers.UpScanInitiateHttpParser.UpscanInitiateResponse
import mocks.config.MockAppConfig
import models.InvalidJson
import models.upscan.*
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.InternalServerException

import scala.concurrent.Future

class UpScanServiceSpec extends ServiceSpecBase {

  private val dutyType = "duty"

  private val callBackUrl = MockAppConfig.appConfig.upScanCallbackUrlForSuccessOrFailureOfFileUpload
  private val success     = MockAppConfig.appConfig.upScanSuccessRedirectForUser
  private val error       = MockAppConfig.appConfig.upScanErrorRedirectForUser
  private val minFileSize = MockAppConfig.appConfig.upScanMinFileSize
  private val maxFileSize = MockAppConfig.appConfig.upScanMaxFileSize

  private val upScanAuthoritySuccessRedirectForUser =
    MockAppConfig.appConfig.upScanAuthoritySuccessRedirectForUser ++ s"/$dutyType/upscan-response"

  private val upScanAuthorityErrorRedirectForUser =
    MockAppConfig.appConfig.upScanAuthorityErrorRedirectForUser ++ s"/$dutyType/upscan-response"

  val mockUpScanConnector: UpScanConnector = mock[UpScanConnector]
  val service                              = new UpScanService(mockUpScanConnector, MockAppConfig.appConfig)

  "buildInitiateRequest" should {
    "return all values from AppConfig" in {
      service.buildInitiateRequest mustBe UpScanInitiateRequest(
        callBackUrl,
        success,
        error,
        minFileSize,
        maxFileSize
      )
    }
  }

  "buildAuthorityInitiateRequest" should {
    "return all correct values from AppConfig including augmented callback" in {
      service.buildAuthorityInitiateRequest("duty") mustBe UpScanInitiateRequest(
        callBackUrl,
        upScanAuthoritySuccessRedirectForUser,
        upScanAuthorityErrorRedirectForUser,
        minFileSize,
        maxFileSize
      )
    }
  }

  "initiateNewJourney" should {
    val model   = UpScanInitiateResponse(Reference("foo"), UploadFormTemplate("", Map.empty))
    val request = UpScanInitiateRequest(callBackUrl, success, error, minFileSize, maxFileSize)

    "throw exception if Left returned from connector" in {
      val res: UpscanInitiateResponse = Left(InvalidJson)

      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      intercept[InternalServerException](await(service.initiateNewJourney()))
    }

    "return model if Right model returned from connector" in {
      val res: UpscanInitiateResponse = Right(model)

      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      await(service.initiateNewJourney()) mustBe model
    }
  }

  "initiateAuthorityJourney" should {
    val model = UpScanInitiateResponse(Reference("foo"), UploadFormTemplate("", Map.empty))
    val request = UpScanInitiateRequest(
      callBackUrl,
      upScanAuthoritySuccessRedirectForUser,
      upScanAuthorityErrorRedirectForUser,
      minFileSize,
      maxFileSize
    )

    "throw exception if Left returned from connector" in {
      val res: UpscanInitiateResponse = Left(InvalidJson)

      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      intercept[InternalServerException](await(service.initiateAuthorityJourney(dutyType)))
    }

    "return model if Right model returned from connector" in {
      val res: UpscanInitiateResponse = Right(model)

      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      await(service.initiateAuthorityJourney(dutyType)) mustBe model
    }
  }

  "initiateBulkJourney" should {
    "return model if Right model returned from connector" in {
      val model = UpScanInitiateResponse(Reference("foo"), UploadFormTemplate("", Map.empty))
      val request = UpScanInitiateRequest(
        callBackUrl,
        MockAppConfig.appConfig.upScanSuccessRedirectForBulk,
        MockAppConfig.appConfig.upScanErrorRedirectForBulk,
        minFileSize,
        maxFileSize
      )

      val res: UpscanInitiateResponse = Right(model)
      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      await(service.initiateBulkJourney()) mustBe model
    }

    "throw exception if Left returned from connector" in {
      val request = UpScanInitiateRequest(
        callBackUrl,
        MockAppConfig.appConfig.upScanSuccessRedirectForBulk,
        MockAppConfig.appConfig.upScanErrorRedirectForBulk,
        minFileSize,
        maxFileSize
      )

      val res: UpscanInitiateResponse = Left(InvalidJson)

      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      intercept[InternalServerException](await(service.initiateBulkJourney()))
    }
  }

  "initiateSupportingDocJourney" should {
    "return model if Right model returned from connector" in {
      val model = UpScanInitiateResponse(Reference("foo"), UploadFormTemplate("", Map.empty))
      val request = UpScanInitiateRequest(
        callBackUrl,
        MockAppConfig.appConfig.upScanSupportingDocSuccessRedirectForUser,
        MockAppConfig.appConfig.upScanSupportingDocErrorRedirectForUser,
        minFileSize,
        maxFileSize
      )

      val res: UpscanInitiateResponse = Right(model)
      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      await(service.initiateSupportingDocJourney()) mustBe model
    }

    "throw exception if Left returned from connector" in {
      val request = UpScanInitiateRequest(
        callBackUrl,
        MockAppConfig.appConfig.upScanSupportingDocSuccessRedirectForUser,
        MockAppConfig.appConfig.upScanSupportingDocErrorRedirectForUser,
        minFileSize,
        maxFileSize
      )

      val res: UpscanInitiateResponse = Left(InvalidJson)

      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      intercept[InternalServerException](await(service.initiateSupportingDocJourney()))
    }
  }

  "initiateCancelCaseJourney" should {
    "return model if Right model returned from connector" in {
      val model = UpScanInitiateResponse(Reference("foo"), UploadFormTemplate("", Map.empty))
      val request = UpScanInitiateRequest(
        callBackUrl,
        MockAppConfig.appConfig.upScanCancelCaseRedirectForUser,
        MockAppConfig.appConfig.upScanCancelCaseDocErrorRedirectForUser,
        minFileSize,
        maxFileSize
      )

      val res: UpscanInitiateResponse = Right(model)
      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      await(service.initiateCancelCaseJourney()) mustBe model
    }

    "throw exception if Left returned from connector" in {
      val request = UpScanInitiateRequest(
        callBackUrl,
        MockAppConfig.appConfig.upScanCancelCaseRedirectForUser,
        MockAppConfig.appConfig.upScanCancelCaseDocErrorRedirectForUser,
        minFileSize,
        maxFileSize
      )

      val res: UpscanInitiateResponse = Left(InvalidJson)

      when(mockUpScanConnector.postToInitiate(request)(headerCarrier)).thenReturn(Future.successful(res))
      intercept[InternalServerException](await(service.initiateCancelCaseJourney()))
    }
  }

}
