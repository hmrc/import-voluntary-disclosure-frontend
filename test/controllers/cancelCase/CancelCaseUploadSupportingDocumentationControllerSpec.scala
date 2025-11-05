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

package controllers.cancelCase

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.cancelCase.CancelCaseUploadFileFormProvider
import messages.cancelCase.CancelCaseUploadSupportingDocumentationMessages._
import mocks.config.MockAppConfig
import models.{FileUploadInfo, UserAnswers}
import models.requests._
import models.upscan._
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentMatchers, Mockito}
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import pages.CheckModePage
import pages.updateCase.UploadSupportingDocumentationPage
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.test.Helpers._
import repositories.{FileUploadRepository, SessionRepository}
import services.UpScanService
import views.html.shared.FileUploadProgressView
import views.html.cancelCase.CancelCaseUploadSupportingDocumentationView

import java.time.Instant
import scala.concurrent.Future

class CancelCaseUploadSupportingDocumentationControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

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

  private lazy val cancelCaseUploadSupportingDocumentationView: CancelCaseUploadSupportingDocumentationView =
    app.injector.instanceOf[CancelCaseUploadSupportingDocumentationView]
  private lazy val progressView: FileUploadProgressView = app.injector.instanceOf[FileUploadProgressView]

  val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

  implicit lazy val dataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(
    OptionalDataRequest(
      IdentifierRequest(fakeRequest, "credId", "eori"),
      "credId",
      "eori",
      userAnswers
    ),
    "credId",
    "eori",
    userAnswers.get
  )

  def controller(ua: Option[UserAnswers] = userAnswers): CancelCaseUploadSupportingDocumentationController = {
    new CancelCaseUploadSupportingDocumentationController(
      authenticatedAction,
      new FakeDataRetrievalAction(ua),
      dataRequiredAction,
      messagesControllerComponents,
      mockFileUploadRepository,
      mockSessionRepository,
      mockUpScanService,
      cancelCaseUploadSupportingDocumentationView,
      progressView,
      form,
      MockAppConfig.appConfig,
      ec
    )
  }

  val formProvider: CancelCaseUploadFileFormProvider = injector.instanceOf[CancelCaseUploadFileFormProvider]
  val form: CancelCaseUploadFileFormProvider         = formProvider

  override def beforeEach(): Unit = {
    when(mockFileUploadRepository.updateRecord(any())(any())).thenReturn(Future.successful(true))
    when(mockFileUploadRepository.getRecord(any())(any())).thenReturn(
      Future.successful(Some(Json.fromJson[FileUpload](callbackReadyJson).get))
    )
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
    when(mockUpScanService.initiateCancelCaseJourney()(any(), any())).thenReturn(
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
    "return OK with HTML when called" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest)

      status(result) mustBe Status.OK
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "Display error when file uploaded is Too Small" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "TooSmall"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(fileTooSmall) mustBe true
    }

    "Display error when file uploaded is Too Big" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "TooBig"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(fileTooBig) mustBe true
    }

    "Display error when file uploaded is Unknown" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "Unknown"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(fileUnknown) mustBe true
    }

    "Display error when file uploaded is Rejected" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "Rejected"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(fileRejected) mustBe true
    }

    "Display error when file uploaded is Quarantined" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest.withFlash("uploadError" -> "Quarantined"))
      status(result) mustBe Status.OK
      contentAsString(result).contains(fileQuarantined) mustBe true
    }
  }

  "GET upscanResponseHandler" when {
    "upscan returns an error on upload" should {
      "redirect to error page" in {
        val result =
          controller().upscanResponseHandler(
            Some("key"),
            Some("errorCode"),
            Some("errorMessage"),
            Some("errorResource"),
            Some("errorRequestId")
          )(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationController.onLoad().url
        )
      }
    }

    "upscan returns an error on upload when only an error code is returned" should {
      "redirect to error page" in {
        val result = controller().upscanResponseHandler(Some("key"), Some("errorCode"), None, None, None)(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationController.onLoad().url
        )
      }
    }

    "upscan returns success on upload" should {
      "for a valid key, redirect to holding page" in {
        val result = controller().upscanResponseHandler(Some("key"), None, None, None, None)(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationController.uploadProgress("key").url
        )
      }
      "for a valid key, create record in file Repository" in {
        await(controller().upscanResponseHandler(Some("key"), None, None, None, None)(fakeRequest))
      }

      "for an invalid key" in {
        val result = intercept[RuntimeException](
          await(controller().upscanResponseHandler(None, None, None, None, None)(fakeRequest))
        )

        assert(result.getMessage.contains("No key returned for successful upload"))
      }
    }
  }

  "GET uploadProgress" when {
    "called following a successful file upload callback" should {
      "update UserAnswers and redirect to the Summary Page" in {
        val result: Future[Result] = controller().uploadProgress(key = "key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationSummaryController.onLoad().url
        )

      }
    }
    "called following a file upload callback for a failure" should {
      "NOT update userAnswers and redirect to the Error Page" in {
        reset(mockFileUploadRepository)
        when(mockFileUploadRepository.getRecord(any())(any()))
          .thenReturn(Future.successful(Some(Json.fromJson[FileUpload](callbackFailedRejectedJson).get)))

        val result: Future[Result] = controller().uploadProgress("key")(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationController.onLoad().url
        )

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

  "backLink" when {

    "in change mode" should {
      "point to CYA page" in {
        val ua: UserAnswers   = UserAnswers("some-cred-id").set(CheckModePage, true).success.value
        lazy val result: Call = controller().backLink()(dataRequest.copy(userAnswers = ua))
        result mustBe controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad()
      }
    }

    "not in change mode" should {
      "point to upload another file page if there are files" in {
        val ua: UserAnswers =
          UserAnswers("credId")
            .set(CheckModePage, false).success.value
            .set(
              UploadSupportingDocumentationPage,
              Seq(
                FileUploadInfo(
                  reference = "file-ref-1",
                  fileName = "file.txt",
                  downloadUrl = "url",
                  uploadTimestamp = Instant.now,
                  checksum = "checksum",
                  fileMimeType = "application/txt"
                )
              )
            ).success.value
        val result: Call = controller().backLink()(dataRequest.copy(userAnswers = ua))
        result mustBe controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationSummaryController.onLoad()
      }

      "point to need to upload page if there are no files" in {
        val ua: UserAnswers = UserAnswers("credId").set(CheckModePage, false).success.value
        val result: Call    = controller().backLink()(dataRequest.copy(userAnswers = ua))
        result mustBe controllers.cancelCase.routes.AnyOtherSupportingCancellationDocsController.onLoad()
      }
    }
  }
}
