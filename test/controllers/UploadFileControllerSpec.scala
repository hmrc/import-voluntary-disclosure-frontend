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

package controllers

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.config.MockAppConfig
import mocks.repositories.MockFileUploadRepository
import mocks.services.MockUpScanService
import models.upscan.{Reference, UpScanInitiateResponse, UploadFormTemplate}
import models.UserAnswers
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.UploadFileView

import scala.concurrent.Future

class UploadFileControllerSpec extends ControllerSpecBase {

  trait Test extends MockFileUploadRepository with MockUpScanService {
    private lazy val uploadFileView: UploadFileView = app.injector.instanceOf[UploadFileView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    def setupMocks():Unit = {
      MockedFileUploadRepository.updateRecord(Future.successful(true))

      MockedUpScanService.initiateNewJourney(
        Future.successful(UpScanInitiateResponse(
          Reference("11370e18-6e24-453e-b45a-76d3e32ea33d"),
          UploadFormTemplate(
            "https://bucketName.s3.eu-west-2.amazonaws.com",
            Map("Content-Type" -> "application/xml")
          )
        ))
      )
    }

    lazy val controller = {
      setupMocks()
      new UploadFileController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
        messagesControllerComponents, mockFileUploadRepository, mockUpScanService, uploadFileView, MockAppConfig)
    }
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)

      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "GET upscanResponseHandler" when {
    "upscan returns an error on upload" should {
      "redirect to error page" in new Test {
        val result = controller.upscanResponseHandler(
          Some("key"), Some("errorCode"), Some("errorMessage"), Some("errorResource"), Some("errorRequestId")
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadFileController.onLoad().url)
      }
    }

    "upscan returns success on upload" should {
      "for a valid key, redirect to holding page" in new Test {
        val result = controller.upscanResponseHandler(
          Some("key"), None, None, None, None
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadFileController.onLoad().url)
      }
      "for a valid key, create record in file Repository" in new Test {
        override def setupMocks(): Unit = {
          MockedFileUploadRepository.updateRecord(Future.successful(true))
        }
        val result = await(controller.upscanResponseHandler(
          Some("key"), None, None, None, None
        )(fakeRequest))

        MockedFileUploadRepository.verifyCalls()
      }

      "for an invalid key" in new Test {
        val result = intercept[RuntimeException](await(controller.upscanResponseHandler(
          None, None, None, None, None
        )(fakeRequest)))

        assert(result.getMessage.contains("No key returned for successful upload"))
      }
    }
  }
}
