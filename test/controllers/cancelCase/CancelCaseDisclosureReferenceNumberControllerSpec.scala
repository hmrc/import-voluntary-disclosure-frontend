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
import forms.cancelCase.CancelCaseDisclosureReferenceNumberFormProvider
import models.UserAnswers
import models.requests._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.CheckModePage
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import repositories.SessionRepository
import views.html.cancelCase.CancelCaseDisclosureReferenceNumberView

import scala.concurrent.Future

class CancelCaseDisclosureReferenceNumberControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private lazy val view: CancelCaseDisclosureReferenceNumberView =
    app.injector.instanceOf[CancelCaseDisclosureReferenceNumberView]

  val userAnswers: UserAnswers = UserAnswers("credId").set(CheckModePage, false).success.value
  val disclosureReference      = "C181234567890123456789"

  implicit lazy val dataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(
    OptionalDataRequest(
      IdentifierRequest(fakeRequest, "credId", "eori"),
      "credId",
      "eori",
      Some(userAnswers)
    ),
    "credId",
    "eori",
    userAnswers
  )

  val formProvider: CancelCaseDisclosureReferenceNumberFormProvider =
    injector.instanceOf[CancelCaseDisclosureReferenceNumberFormProvider]
  val form: CancelCaseDisclosureReferenceNumberFormProvider = formProvider

  def controller(ua: UserAnswers = userAnswers): CancelCaseReferenceNumberController = {
    new CancelCaseReferenceNumberController(
      authenticatedAction,
      new FakeDataRetrievalAction(Some(ua)),
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      view,
      ec
    )
  }

  override def beforeEach(): Unit =
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

  "GET onLoad" should {
    "return OK" in {
      val result: Future[Result] = controller().onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val result: Future[Result] = controller().onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data when check mode is false" should {

      "return a SEE OTHER response" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        lazy val result: Future[Result] = controller().onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        lazy val result: Future[Result] = controller().onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.cancelCase.routes.CancellationReasonController.onLoad().url)
      }

      "update the UserAnswers in session" in {
        val request = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        await(controller().onSubmit(request))
      }
    }

    "payload contains valid data when check mode is true" should {

      "return a SEE OTHER response" in {
        val userAnswers: UserAnswers = UserAnswers("some-cred-id").set(CheckModePage, true).success.value
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        lazy val result: Future[Result] = controller(userAnswers).onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in {
        val userAnswers: UserAnswers = UserAnswers("some-cred-id").set(CheckModePage, true).success.value
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        lazy val result: Future[Result] = controller(userAnswers).onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad().url
        )
      }

      "update the UserAnswers in session" in {
        val userAnswers: UserAnswers = UserAnswers("some-cred-id").set(CheckModePage, true).success.value
        val request                  = fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference)
        await(controller(userAnswers).onSubmit(request))
      }
    }

    "payload contains lowercase reference number" should {
      "update the UserAnswers in session with upper case reference number" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] =
          fakeRequest.withFormUrlEncodedBody("value" -> disclosureReference.toLowerCase)
        lazy val result: Future[Result] = controller().onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in {
        val result: Future[Result] = controller().onSubmit(fakeRequest.withFormUrlEncodedBody("value" -> "invalid"))
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

  "backLink" when {

    "not in change mode" should {
      "point to What Do You Want To Do page" in {
        val ua: UserAnswers   = UserAnswers("some-cred-id").set(CheckModePage, false).success.value
        lazy val result: Call = controller().backLink(dataRequest.copy(userAnswers = ua))
        result mustBe controllers.serviceEntry.routes.WhatDoYouWantToDoController.onLoad()
      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in {
        val ua: UserAnswers   = UserAnswers("some-cred-id").set(CheckModePage, true).success.value
        lazy val result: Call = controller().backLink(dataRequest.copy(userAnswers = ua))
        result mustBe controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad()
      }
    }

  }

}
