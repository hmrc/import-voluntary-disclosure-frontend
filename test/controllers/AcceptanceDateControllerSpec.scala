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
import forms.AcceptanceDateFormProvider
import mocks.repositories.MockSessionRepository
import models.NumberOfEntries._
import models.UserAnswers
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import pages.importDetails.NumberOfEntriesPage
import pages.{AcceptanceDatePage, CheckModePage}
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{AcceptanceDateBulkView, AcceptanceDateView}

import scala.concurrent.Future


class AcceptanceDateControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val acceptanceDateView: AcceptanceDateView = app.injector.instanceOf[AcceptanceDateView]
    private lazy val acceptanceDateBulkView: AcceptanceDateBulkView = app.injector.instanceOf[AcceptanceDateBulkView]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId").set(NumberOfEntriesPage, OneEntry).success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: AcceptanceDateFormProvider = injector.instanceOf[AcceptanceDateFormProvider]
    val form: AcceptanceDateFormProvider = formProvider

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

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new AcceptanceDateController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, form, acceptanceDateView, acceptanceDateBulkView, ec)
  }

  val acceptanceDateYes: Boolean = true

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(AcceptanceDatePage, acceptanceDateYes).success.value)
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header for Single Entry" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit()(request)
        redirectLocation(result) mustBe Some(controllers.routes.OneCustomsProcedureCodeController.onLoad().url)
      }

      "return the correct location header for Bulk Entry" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(NumberOfEntriesPage, MoreThanOneEntry).success.value
          )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit()(request)
        redirectLocation(result) mustBe Some(controllers.underpayments.routes.UnderpaymentStartController.onLoad().url)
      }

      "return the correct location header in check mode" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(AcceptanceDatePage, acceptanceDateYes).success.value
            .set(CheckModePage, true).success.value
          )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result] = controller.onSubmit()(request)
        redirectLocation(result) mustBe Some(controllers.routes.CheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit()(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST in Single Entry mode" in new Test {
        val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
      "return a BAD REQUEST in bulk mode" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(NumberOfEntriesPage, MoreThanOneEntry).success.value
          )
        val result: Future[Result] = controller.onSubmit()(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

  "backLink" when {

    "not in change mode" should {
      "when loading page back button should take you to Entry details page for Single Entry" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(AcceptanceDatePage, acceptanceDateYes).success.value
            .set(NumberOfEntriesPage, OneEntry).success.value
            .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.routes.EntryDetailsController.onLoad()
      }
      "when loading page back button should take you to NumberOfEntrues details page for Bulk Entry" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(AcceptanceDatePage, acceptanceDateYes).success.value
            .set(NumberOfEntriesPage, MoreThanOneEntry).success.value
            .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.importDetails.routes.NumberOfEntriesController.onLoad()
      }
    }

    "in change mode" should {
      "when loading page back button should take you to Check your answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(AcceptanceDatePage, acceptanceDateYes).success.value
            .set(CheckModePage, true).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.routes.CheckYourAnswersController.onLoad()
      }
    }

  }

}
