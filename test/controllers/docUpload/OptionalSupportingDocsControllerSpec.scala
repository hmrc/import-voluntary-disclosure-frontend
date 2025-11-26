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
import forms.docUpload.OptionalSupportingDocsFormProvider
import models.OptionalDocument._
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.docUpload.OptionalSupportingDocsPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.docUpload.OptionalSupportingDocsView

import scala.concurrent.Future

class OptionalSupportingDocsControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private lazy val optionalSupportingDocsView: OptionalSupportingDocsView =
    app.injector.instanceOf[OptionalSupportingDocsView]

  val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

  val formProvider: OptionalSupportingDocsFormProvider = injector.instanceOf[OptionalSupportingDocsFormProvider]
  val form: OptionalSupportingDocsFormProvider         = formProvider

  def controller(ua: Option[UserAnswers] = userAnswers): OptionalSupportingDocsController = {
    new OptionalSupportingDocsController(
      authenticatedAction,
      new FakeDataRetrievalAction(ua),
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      optionalSupportingDocsView,
      form,
      ec
    )
  }

  override def beforeEach(): Unit =
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

  "GET onLoad" should {
    "return OK" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val ua: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(OptionalSupportingDocsPage, Seq(ImportAndEntry)).success.value
      )
      val result: Future[Result] = controller(ua).onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" should {

    "payload contains valid data" should {

      "return a SEE OTHER response and redirect to correct location at least one option is selected" in {
        val request = fakeRequest.withFormUrlEncodedBody("optionalDocumentsList[]" -> "importAndEntry")
        lazy val result: Future[Result] = controller().onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.docUpload.routes.UploadFileController.onLoad().url)
      }

    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in {
        val result: Future[Result] = controller().onSubmit()(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
