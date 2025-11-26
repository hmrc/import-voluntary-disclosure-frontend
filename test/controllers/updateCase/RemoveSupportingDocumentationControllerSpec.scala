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

package controllers.updateCase

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.shared.RemoveUploadedFileFormProvider
import models.{FileUploadInfo, Index, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.updateCase.UploadSupportingDocumentationPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.shared.RemoveUploadedFileView

import java.time.Instant
import scala.concurrent.Future

class RemoveSupportingDocumentationControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private lazy val RemoveUploadedFileView: RemoveUploadedFileView = app.injector.instanceOf[RemoveUploadedFileView]

  val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

  val formProvider: RemoveUploadedFileFormProvider = injector.instanceOf[RemoveUploadedFileFormProvider]
  val form: RemoveUploadedFileFormProvider         = formProvider

  val index: Index = Index.apply(0)

  def controller(ua: Option[UserAnswers] = userAnswers): RemoveSupportingDocumentationController = {
    new RemoveSupportingDocumentationController(
      messagesApi,
      mockSessionRepository,
      authenticatedAction,
      new FakeDataRetrievalAction(ua),
      dataRequiredAction,
      form,
      messagesControllerComponents,
      RemoveUploadedFileView,
      ec
    )
  }

  override protected def beforeEach(): Unit =
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

  "GET onLoad" should {
    "redirect to UploadSupportingDocumentation page if no uploaded-files in user answers" in {
      val result: Future[Result] = controller().onLoad(index)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "redirect to UploadSupportingDocumentation page if all files removed" in {
      val ua: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            UploadSupportingDocumentationPage,
            Seq.empty
          ).success.value
      )
      val result: Future[Result] = controller(ua).onLoad(index)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "redirect to RemoveSupportingDocumentation page if files exist" in {
      val ua: Option[UserAnswers] = Some(
        UserAnswers("credId")
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
      )
      val result: Future[Result] = controller(ua).onLoad(index)(fakeRequest)
      status(result) mustBe Status.OK
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller().onSubmit(index)(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return a SEE OTHER response when true" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller().onSubmit(index)(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller().onSubmit(index)(request)
        redirectLocation(result) mustBe Some(
          controllers.updateCase.routes.UploadSupportingDocumentationSummaryController.onLoad().url
        )
      }

      "update the UserAnswers in session" in {
        val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller().onSubmit(index)(request))
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in {
        val ua: Option[UserAnswers] = Some(
          UserAnswers("credId")
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
        )
        val result: Future[Result] = controller(ua).onSubmit(index)(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
      "return a Internal Server Error if data lost" in {
        val result: Future[Result] = controller().onSubmit(index)(fakeRequest)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

}
