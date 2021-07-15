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
import forms.NumberOfEntriesFormProvider
import mocks.repositories.MockSessionRepository
import models.NumberOfEntries.{MoreThanOneEntry, OneEntry}
import models.importDetails.UserType.{Importer, Representative}
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.{NumberOfEntries, UserAnswers}
import pages._
import pages.importDetails.{ImporterEORIExistsPage, ImporterEORINumberPage, UserTypePage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.NumberOfEntriesView

import scala.concurrent.Future

class NumberOfEntriesControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val numberOfEntriesPage: NumberOfEntriesView = app.injector.instanceOf[NumberOfEntriesView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
      .set(UserTypePage, Representative).success.value
      .set(ImporterEORIExistsPage, false).success.value)
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

    val formProvider: NumberOfEntriesFormProvider = injector.instanceOf[NumberOfEntriesFormProvider]
    val form: NumberOfEntriesFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new NumberOfEntriesController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, appConfig, messagesControllerComponents, form, numberOfEntriesPage, ec)
  }

  "GET onLoad" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
        .set(UserTypePage, Representative).success.value
        .set(ImporterEORIExistsPage, true).success.value
        .set(ImporterEORINumberPage, "GB345834921000").success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit for oneEntry" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return a SEE OTHER response when the existing value is equal to the submitted one" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterEORIExistsPage, false).success.value
          .set(NumberOfEntriesPage, OneEntry).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.EntryDetailsController.onLoad().url)
      }

      "return a SEE OTHER response when the existing value is not equal to the submitted one" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterEORIExistsPage, false).success.value
          .set(NumberOfEntriesPage, MoreThanOneEntry).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.EntryDetailsController.onLoad().url)
      }

      "return a SEE OTHER response when the existing value is equal to the submitted one in check mode" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterEORIExistsPage, false).success.value
          .set(NumberOfEntriesPage, OneEntry).success.value
          .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.CheckYourAnswersController.onLoad().url)
      }

      "return a SEE OTHER response when the existing value is not equal to the submitted one in check mode" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterEORIExistsPage, false).success.value
          .set(NumberOfEntriesPage, MoreThanOneEntry).success.value
          .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.EntryDetailsController.onLoad().url)
      }

      "return a SEE OTHER response when the existing OneEntry is not equal to the submitted MoreThanOne in check mode" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterEORIExistsPage, false).success.value
          .set(NumberOfEntriesPage, OneEntry).success.value
          .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.MoreThanOneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AcceptanceDateController.onLoad().url)
      }

      "return the correct location header" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.EntryDetailsController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.OneEntry.toString)
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

  "POST onSubmit for moreThanOneEntry" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.MoreThanOneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.MoreThanOneEntry.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.AcceptanceDateController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> NumberOfEntries.MoreThanOneEntry.toString)
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

  "backlink" when {

    "in check mode" should {

      "return SEE OTHER CYA" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterEORIExistsPage, false).success.value
          .set(NumberOfEntriesPage, OneEntry).success.value
          .set(CheckModePage, true).success.value
        )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.routes.CheckYourAnswersController.onLoad()
      }

    }

    "not in check mode" should {

      "return SEE OTHER ImporterVatRegisteredController" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterEORIExistsPage, true).success.value
          .set(NumberOfEntriesPage, OneEntry).success.value
          .set(CheckModePage, false).success.value
        )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.importDetails.routes.ImporterVatRegisteredController.onLoad()
      }

      "return SEE OTHER ImporterEORIExistsController" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterEORIExistsPage, false).success.value
          .set(NumberOfEntriesPage, OneEntry).success.value
          .set(CheckModePage, false).success.value
        )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.importDetails.routes.ImporterEORIExistsController.onLoad()
      }

      "return SEE OTHER UserTypeController" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, Importer).success.value
          .set(ImporterEORIExistsPage, false).success.value
          .set(NumberOfEntriesPage, OneEntry).success.value
          .set(CheckModePage, false).success.value
        )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.importDetails.routes.UserTypeController.onLoad()
      }

    }

  }

}
