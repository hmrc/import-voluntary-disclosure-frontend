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

package controllers.serviceEntry

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.serviceEntry.WhatDoYouWantToDoFormProvider
import mocks.repositories.MockSessionRepository
import models.SubmissionType.CreateCase
import models.UserAnswers
import models.requests._
import pages.serviceEntry.WhatDoYouWantToDoPage
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.serviceEntry.WhatDoYouWantToDoView

import scala.concurrent.Future

class WhatDoYouWantToDoControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    private lazy val view: WhatDoYouWantToDoView = app.injector.instanceOf[WhatDoYouWantToDoView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

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

    val formProvider: WhatDoYouWantToDoFormProvider = injector.instanceOf[WhatDoYouWantToDoFormProvider]
    val form: WhatDoYouWantToDoFormProvider         = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new WhatDoYouWantToDoController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      view,
      ec
    )
  }

  "GET onLoad" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(WhatDoYouWantToDoPage, CreateCase).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER when the current saved value is the same as submitted value" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(WhatDoYouWantToDoPage, CreateCase).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "createCase"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.importDetails.routes.UserTypeController.onLoad().url)
      }

      "return a SEE OTHER when there's no current saved value and submitted is CreateCase" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "createCase"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.importDetails.routes.UserTypeController.onLoad().url)
      }

      "return a SEE OTHER when there's no current saved value and submitted is UpdateCase" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "updateCase"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.updateCase.routes.DisclosureReferenceNumberController.onLoad().url
        )
      }

      "return a SEE OTHER when there's no current saved value and submitted is CancelCase" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody(
          "value" -> "cancelCase"
        )
        lazy val result: Future[Result] = controller.onSubmit()(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.cancelCase.routes.CancelCaseReferenceNumberController.onLoad().url
        )
      }

    }

    "payload contains valid data" should {

      "return a bad request when no data is sent" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody()
        lazy val result: Future[Result]                      = controller.onSubmit()(request)
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

}
