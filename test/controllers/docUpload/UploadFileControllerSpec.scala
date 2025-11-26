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

package controllers.docUpload

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.shared.UploadFileFormProvider
import messages.docUpload.UploadFileMessages
import mocks.config.MockAppConfig
import models.upscan.{FileUpload, Reference, UpScanInitiateResponse, UploadFormTemplate}
import models.{FileUploadInfo, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import pages.CheckModePage
import pages.docUpload.FileUploadPage
import pages.shared.AnyOtherSupportingDocsPage
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import repositories.{FileUploadRepository, SessionRepository}
import services.UpScanService
import views.html.docUpload.UploadFileView
import views.html.shared.FileUploadProgressView

import java.time.Instant
import scala.concurrent.Future

class UploadFileControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  private val callbackReadyJson: JsValue = Json.parse(s"""
                                                         | {
                                                         |   "reference" : "11370e18-6e24-453e-b45a-76d3e32ea33d",
                                                         |   "fileStatus" : "READY",
                                                         |   "downloadUrl" : "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
                                                         |   "uploadDetails": {
                                                         |     "uploadTimestamp": "2018-04-24T09:30:00Z",
                                                         |     "checksum": "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
                                                         |     "fileName": "test.pdf",
                                                         |     "fileMimeType": "application/pdf"
                                                         |   }
                                                         | }""".stripMargin)

  private val callbackFailedRejectedJson: JsValue = Json.parse(s"""
                                                                  | {
                                                                  |   "reference" : "11370e18-6e24-453e-b45a-76d3e32ea33d",
                                                                  |   "fileStatus" : "FAILED",
                                                                  |    "failureDetails": {
                                                                  |        "failureReason": "REJECTED",
                                                                  |        "message": "MIME type .foo is not allowed for service import-voluntary-disclosure-frontend"
                                                                  |    }
                                                                  | }""".stripMargin)

  val mockSessionRepository: SessionRepository       = mock[SessionRepository]
  val mockFileUploadRepository: FileUploadRepository = mock[FileUploadRepository]
  val mockUpScanService: UpScanService               = mock[UpScanService]

  val uploadFileView: UploadFileView             = app.injector.instanceOf[UploadFileView]
  val uploadProgressView: FileUploadProgressView = app.injector.instanceOf[FileUploadProgressView]

  val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

  def controller(userAnswers: Option[UserAnswers] = userAnswers): UploadFileController = {
    new UploadFileController(
      authenticatedAction,
      new FakeDataRetrievalAction(userAnswers),
      dataRequiredAction,
      messagesControllerComponents,
      mockFileUploadRepository,
      mockSessionRepository,
      mockUpScanService,
      uploadFileView,
      uploadProgressView,
      form,
      MockAppConfig.appConfig,
      ec
    )
  }

  val formProvider: UploadFileFormProvider = injector.instanceOf[UploadFileFormProvider]
  val form: UploadFileFormProvider         = formProvider

  override def beforeEach(): Unit = {
    when(mockFileUploadRepository.updateRecord(any())(any())).thenReturn(Future.successful(true))
    when(mockFileUploadRepository.getRecord(any())(any())).thenReturn(
      Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get))
    )
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
    when(mockUpScanService.initiateNewJourney()(any(), any())).thenReturn(
      Future.successful(
        UpScanInitiateResponse(
          Reference("11370e18-6e24-453e-b45a-76d3e32ea33d"),
          UploadFormTemplate(
            "https://bucketName.s3.eu-west-2.amazonaws.com",
            Map("Content-Type" -> "application/xml")
          )
        )
      )
    )
  }

  "GET onLoad" should {
    "return OK" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return correct back link for no additional documents" in {
      val userAnswers: Option[UserAnswers] =
        Some(UserAnswers("credId").set(AnyOtherSupportingDocsPage, false).success.value)
      val result: Future[Result] = controller(userAnswers).onLoad()(fakeRequest)
      contentAsString(result).contains(
        controllers.docUpload.routes.AnyOtherSupportingDocsController.onLoad().url
      ) mustBe true
    }

    "return correct back link for additional documents" in {
      val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(AnyOtherSupportingDocsPage, true).success.value
      )
      val result: Future[Result] = controller(userAnswers).onLoad()(fakeRequest)
      contentAsString(result).contains(
        controllers.docUpload.routes.OptionalSupportingDocsController.onLoad().url
      ) mustBe true
    }

    "return correct back link if come from Summary screen" in {
      val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            FileUploadPage,
            Seq(
              FileUploadInfo(
                reference = "file-ref-1",
                fileName = "file1",
                downloadUrl = "url",
                uploadTimestamp = Instant.now(),
                checksum = "checksum",
                fileMimeType = "mime"
              )
            )
          ).success.value
      )
      val result: Future[Result] = controller(userAnswers).onLoad()(fakeRequest)
      contentAsString(result).contains(
        controllers.docUpload.routes.UploadAnotherFileController.onLoad().url
      ) mustBe true
    }

    "return no back link if in check mode and all files removed" in {
      val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(CheckModePage, true).success.value
      )
      val result: Future[Result] = controller(userAnswers).onLoad()(fakeRequest)
      contentAsString(result).contains("govuk-back-link") mustBe false
    }

    "Display error when file uploaded is Too Small" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "TooSmall"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileTooSmall) mustBe true
    }

    "Display error when file uploaded is Too Big" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "TooBig"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileTooBig) mustBe true
    }

    "Display error when file uploaded is Unknown" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "Unknown"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileUnknown) mustBe true
    }

    "Display error when file uploaded is Rejected" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "Rejected"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileRejected) mustBe true
    }

    "Display error when file uploaded is Quarantined" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "Quarantined"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(UploadFileMessages.fileQuarantined) mustBe true
    }
  }

  "GET upscanResponseHandler" when {
    "upscan returns an error on upload" should {
      "redirect to error page" in {
        val result = controller().upscanResponseHandler(
          Some("key"),
          Some("errorCode"),
          Some("errorMessage"),
          Some("errorResource"),
          Some("errorRequestId")
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadFileController.onLoad().url)
      }
    }

    "upscan returns an error on upload when only an error code is returned" should {
      "redirect to error page" in {
        val result = controller().upscanResponseHandler(
          Some("key"),
          Some("errorCode"),
          None,
          None,
          None
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadFileController.onLoad().url)
      }
    }

    "upscan returns success on upload" should {
      "for a valid key, redirect to holding page" in {
        val result = controller().upscanResponseHandler(
          Some("key"),
          None,
          None,
          None,
          None
        )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.docUpload.routes.UploadFileController.uploadProgress("key").url
        )
      }
      "for a valid key, create record in file Repository" in {
        await(
          controller().upscanResponseHandler(
            Some("key"),
            None,
            None,
            None,
            None
          )(fakeRequest)
        )
      }

      "for an invalid key" in {
        val result = intercept[RuntimeException](
          await(
            controller().upscanResponseHandler(
              None,
              None,
              None,
              None,
              None
            )(fakeRequest)
          )
        )

        assert(result.getMessage.contains("No key returned for successful upload"))
      }
    }
  }

  "GET uploadProgress" when {
    "called following a successful file upload callback" should {
      "update UserAnswers and redirect to the Summary Page" in {
        val result: Future[Result] = controller().uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadAnotherFileController.onLoad().url)
      }
    }
    "called following a file upload callback for a failure" should {
      "NOT update userAnswers and redirect to the Error Page" in {
        reset(mockFileUploadRepository)
        when(mockFileUploadRepository.getRecord(any())(any())).thenReturn(
          Future.successful(Some(Json.fromJson[FileUpload](callbackFailedRejectedJson).get))
        )

        val result: Future[Result] = controller().uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadFileController.onLoad().url)
      }
    }
    "called before any file upload callback" should {
      "NOT update userAnswers and redirect to the Progress Page" in {
        reset(mockFileUploadRepository)
        when(mockFileUploadRepository.getRecord(any())(any())).thenReturn(
          Future.successful(Some(FileUpload("reference")))
        )

        val result: Future[Result] = controller().uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.OK
      }
    }
    "called for a Key no longer in repository" should {
      "return 500 Internal Server Error" in {
        reset(mockFileUploadRepository)
        when(mockFileUploadRepository.getRecord(any())(any())).thenReturn(Future.successful(None))

        val result: Future[Result] = controller().uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

}
