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

package controllers.cancelCase

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.cancelCase.CancelCaseDisclosureReferenceNumberFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.requests._
import org.scalamock.handlers.CallHandler
import pages.CheckModePage
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.cancelCase.CancelCaseDisclosureReferenceNumberView

import scala.concurrent.{ExecutionContext, Future}


class CancelCaseDisclosureReferenceNumberControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: CancelCaseDisclosureReferenceNumberView = app.injector.instanceOf[CancelCaseDisclosureReferenceNumberView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
      .set(CheckModePage, false).success.value
    )
    val disclosureReference = "C181234567890123456789"
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

    val formProvider: CancelCaseDisclosureReferenceNumberFormProvider = injector.instanceOf[CancelCaseDisclosureReferenceNumberFormProvider]
    val form: CancelCaseDisclosureReferenceNumberFormProvider = formProvider

    def expectSessionSet(): CallHandler[Future[Boolean]] = MockedSessionRepository.set(Future.successful(true))

    expectSessionSet()

    lazy val controller = new CancelCaseReferenceNumberController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, form, view, ec)
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(DisclosureReferenceNumberPage, disclosureReference).success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data when check mode is false" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.cancelCase.routes.CancellationReasonController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains valid data when check mode is true" should {

      "return a SEE OTHER response" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(CheckModePage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(CheckModePage, true).success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains lowercase reference number" should {
      "update the UserAnswers in session with upper case reference number" in new Test {
        override def expectSessionSet(): CallHandler[Future[Boolean]] =
          (mockSessionRepository.set(_: UserAnswers)(_: ExecutionContext))
            .expects(where((answers: UserAnswers, _: ExecutionContext) => answers.get(DisclosureReferenceNumberPage).contains(disclosureReference)))
            .returning(Future.successful(true))

        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference.toLowerCase)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER

        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest.withFormUrlEncodedBody("value" -> "invalid"))
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

  "backLink" when {

    "not in change mode" should {
      "point to What Do You Want To Do page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.serviceEntry.routes.WhatDoYouWantToDoController.onLoad()
      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad()
      }
    }

  }

}



