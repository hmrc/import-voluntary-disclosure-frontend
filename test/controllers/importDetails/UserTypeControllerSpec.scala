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

package controllers.importDetails

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.importDetails.UserTypeFormProvider
import mocks.config.MockAppConfig
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.importDetails.UserType
import models.requests.{IdentifierRequest, OptionalDataRequest}
import pages.CheckModePage
import pages.importDetails.UserTypePage
import play.api.http.Status
import play.api.mvc.{AnyContent, Result}
import play.api.test.Helpers._
import views.html.importDetails.UserTypeView

import scala.concurrent.Future

class UserTypeControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val userTypePage: UserTypeView = app.injector.instanceOf[UserTypeView]
    val userAnswers: Option[UserAnswers]        = None
    private lazy val dataRetrievalAction        = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UserTypeFormProvider = injector.instanceOf[UserTypeFormProvider]
    val form: UserTypeFormProvider         = formProvider
    lazy val appConfig = new MockAppConfig(
      privateBetaAllowList = List.empty,
      privateBetaAllowListEnabled = false,
      updateCaseEnabled = false,
      welshToggleEnabled = true,
      cancelCaseEnabled = false
    )

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new UserTypeController(
      authenticatedAction,
      dataRetrievalAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      userTypePage,
      appConfig,
      ec
    )
  }

  "GET onLoad" should {
    "return OK" in new Test {
      private val previousAnswers = UserAnswers("some cred ID").set(UserTypePage, UserType.Importer).success.value
      override val userAnswers: Option[UserAnswers] = Some(previousAnswers)
      val result: Future[Result]                    = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "submitting a 'Importer' answer in the the initial user journey" should {

      "return a SEE OTHER response" in new Test {
        private val request             = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "redirect to the number of entries page" in new Test {
        private val request             = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.importDetails.routes.NumberOfEntriesController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "submitting a 'Representative' answer in the the initial user journey" should {

      "return a SEE OTHER response" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Representative.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "redirect to the importer name page" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Representative.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.importDetails.routes.ImporterNameController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Representative.toString)
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "submitting a different answer whilst in the CYA journey" should {

      val answers = UserAnswers("some-cred-id")
        .set(UserTypePage, UserType.Importer).success.value
        .set(CheckModePage, true).success.value

      "return a SEE OTHER response" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(answers)
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Representative.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "redirect the user to next page in the initial journey" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(answers)
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Representative.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.importDetails.routes.ImporterNameController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(answers)
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Representative.toString)
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "submitting the same answer whilst in the CYA journey" should {

      val answers = UserAnswers("some-cred-id")
        .set(UserTypePage, UserType.Importer).success.value
        .set(CheckModePage, true).success.value

      "return a SEE OTHER response" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(answers)
        private val request             = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "redirect the user to the CYA page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(answers)
        private val request             = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(answers)
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> UserType.Importer.toString)
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

  "Calling backLink" when {
    "in the the initial user journey" should {
      "go to the confirm EORI details page" in new Test {
        val request: OptionalDataRequest[AnyContent] = OptionalDataRequest(
          IdentifierRequest(fakeRequest, "", ""),
          "cred-id",
          "eori",
          None
        )
        private val backLink = controller.backLink()(request)

        backLink mustBe controllers.serviceEntry.routes.ConfirmEORIDetailsController.onLoad()
      }

      "go to the confirm New Or Update Case page" in new Test {
        override lazy val appConfig = new MockAppConfig(
          privateBetaAllowList = List.empty,
          privateBetaAllowListEnabled = false,
          updateCaseEnabled = true,
          welshToggleEnabled = true,
          cancelCaseEnabled = false
        )
        val request: OptionalDataRequest[AnyContent] = OptionalDataRequest(
          IdentifierRequest(fakeRequest, "", ""),
          "cred-id",
          "eori",
          None
        )
        private val backLink = controller.backLink()(request)

        backLink mustBe controllers.serviceEntry.routes.WhatDoYouWantToDoController.onLoad()
      }

    }

    "in the the CYA user journey" should {
      "go to the CYA page" in new Test {
        private val answers = UserAnswers("cred-id").set(CheckModePage, true).success.value
        val request: OptionalDataRequest[AnyContent] = OptionalDataRequest(
          IdentifierRequest(fakeRequest, "", ""),
          "cred-id",
          "eori",
          Some(answers)
        )
        private val backLink = controller.backLink()(request)

        backLink mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }
  }
}
