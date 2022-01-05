/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.importDetails

import base.ControllerSpecBase
import config.ErrorHandler
import controllers.actions.FakeDataRetrievalAction
import forms.importDetails.ImporterEORIExistsFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.importDetails.UserType
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import pages.CheckModePage
import pages.importDetails.{ImporterEORIExistsPage, ImporterNamePage, UserTypePage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.importDetails.ImporterEORIExistsView

import scala.concurrent.Future

class ImporterEORIExistsControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val importerEORIExistsView: ImporterEORIExistsView = app.injector.instanceOf[ImporterEORIExistsView]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(CheckModePage, false).success.value
        .set(UserTypePage, UserType.Representative).success.value
        .set(ImporterNamePage, "importer").success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
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

    val formProvider: ImporterEORIExistsFormProvider = injector.instanceOf[ImporterEORIExistsFormProvider]
    val form: ImporterEORIExistsFormProvider         = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new ImporterEORIExistsController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      injector.instanceOf[ErrorHandler],
      form,
      importerEORIExistsView,
      ec
    )
  }

  val importerEORIExistsYes: Boolean = true

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(UserAnswers("some-cred-id").set(ImporterEORIExistsPage, importerEORIExistsYes).success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data and check mode is false" should {

      "return a SEE OTHER for true request" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header for true request" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.importDetails.routes.ImporterEORINumberController.onLoad().url)
      }

      "return a SEE OTHER for false request" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header for false request" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.importDetails.routes.NumberOfEntriesController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains valid data and check mode is true" should {

      "return a SEE OTHER for true request" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
              .set(UserTypePage, UserType.Representative).success.value
              .set(ImporterNamePage, "importer").success.value
          )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header for true request" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
              .set(UserTypePage, UserType.Representative).success.value
              .set(ImporterNamePage, "importer").success.value
          )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.importDetails.routes.ImporterEORINumberController.onLoad().url)
      }

      "return a SEE OTHER for false request" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
              .set(UserTypePage, UserType.Representative).success.value
              .set(ImporterNamePage, "importer").success.value
          )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header for false request" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
              .set(UserTypePage, UserType.Representative).success.value
              .set(ImporterNamePage, "importer").success.value
          )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

  "backLink" when {

    "not in change mode" should {
      "point to Importer Name page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.importDetails.routes.ImporterNameController.onLoad()

      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }

  }

}
