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
import forms.shared.UploadAnotherFileFormProvider
import models.UserAnswers
import pages._
import pages.updateCase.UploadSupportingDocumentationPage
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.updateCase.UploadSupportingDocumentationSummaryView

import scala.concurrent.Future

class UploadSupportingDocumentationSummaryControllerSpec extends ControllerSpecBase {

  trait Test {
    private lazy val view: UploadSupportingDocumentationSummaryView =
      app.injector.instanceOf[UploadSupportingDocumentationSummaryView]

    val data: JsObject = Json.obj(
      "uploaded-supporting-documentation" -> Json.arr(
        Json.obj(
          "reference"       -> "file-ref-1",
          "fileName"        -> "text.txt",
          "downloadUrl"     -> "http://localhost:9570/upscan/download/6f531dec-108d-4dc9-a586-9a97cf78bc34",
          "uploadTimestamp" -> "2021-01-26T13:22:59.388Z",
          "checksum"        -> "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
          "fileMimeType"    -> "application/txt"
        )
      )
    )

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId", data))

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UploadAnotherFileFormProvider = injector.instanceOf[UploadAnotherFileFormProvider]
    val form: UploadAnotherFileFormProvider         = formProvider

    lazy val controller = new UploadSupportingDocumentationSummaryController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      messagesControllerComponents,
      form,
      view,
      ec
    )
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "redirect to upload file page if file is empty" in new Test {
      override val data: JsObject = Json.obj("data" -> Json.arr())
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(
            UploadSupportingDocumentationPage,
            Seq.empty
          ).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      redirectLocation(result) mustBe Some(
        controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad().url
      )
    }

    "return SEE OTHER when uploaded-files is empty" in new Test {
      override val data: JsObject = Json.obj("uploaded-supporting-documentation" -> Json.arr())
      val result: Future[Result]  = controller.onLoad(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "return OK when UploadSupportingDocumentationPage is not there" in new Test {
      override val data: JsObject                   = Json.obj("data" -> "")
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("cred-id", data))
      val result: Future[Result]                    = controller.onLoad(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return a SEE OTHER response when true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header when true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad().url
        )
      }

      "return the correct location header when false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.updateCase.routes.UpdateAdditionalInformationController.onLoad().url
        )
      }

      "return to check your answers when in check mode and false" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("cred-id", data)
            .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.updateCase.routes.UpdateCaseCheckYourAnswersController.onLoad().url
        )
      }
    }

    "payload contains invalid data" should {

      "return a SEE OTHER when no user answers are present" in new Test {
        override val data: JsObject                   = Json.obj("data" -> "")
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id", data))

        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad().url
        )
      }

      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
