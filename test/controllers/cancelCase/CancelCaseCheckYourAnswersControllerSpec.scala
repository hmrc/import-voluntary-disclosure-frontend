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
import models.*
import models.UpdateCaseError.CaseAlreadyClosed
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import repositories.SessionRepository
import services.UpdateCaseService
import views.html.cancelCase.{CancelCaseCheckYourAnswersView, CancelCaseConfirmationView}

import scala.concurrent.Future

class CancelCaseCheckYourAnswersControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]
  val mockUpdateCaseService: UpdateCaseService = mock[UpdateCaseService]

  private lazy val cancelCaseCheckYourAnswersView: CancelCaseCheckYourAnswersView =
    app.injector.instanceOf[CancelCaseCheckYourAnswersView]
  private lazy val cancelCaseConfirmationView: CancelCaseConfirmationView =
    app.injector.instanceOf[CancelCaseConfirmationView]

  val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

  def serviceMock: Either[UpdateCaseError, UpdateCaseResponse] =
    Right(UpdateCaseResponse("1234"))

  def controller(ua: Option[UserAnswers] = userAnswers): CancelCaseCheckYourAnswersController = {
    new CancelCaseCheckYourAnswersController(
      authenticatedAction,
      new FakeDataRetrievalAction(ua),
      dataRequiredAction,
      messagesControllerComponents,
      mockSessionRepository,
      mockUpdateCaseService,
      cancelCaseCheckYourAnswersView,
      cancelCaseConfirmationView,
      errorHandler,
      ec
    )
  }

  override def beforeEach(): Unit = {
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
    when(mockUpdateCaseService.updateCase(any(), any(), any())).thenReturn(
      Future.successful(Right(UpdateCaseResponse("1234")))
    )
  }

  "GET onLoad" should {
    "return OK" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val result: Future[Result] = controller().onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "GET onSubmit" should {
    "return Redirect to the confirmation view" in {
      val ua: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DisclosureReferenceNumberPage, "C181234567890123456789").success.value
      )
      val result: Future[Result] = controller(ua).onSubmit()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return Redirect to DisclosureNotFound on InvalidCaseId error" in {
      val caseId = "C181234567890123456789"

      reset(mockUpdateCaseService)
      when(mockUpdateCaseService.updateCase(any(), any(), any())).thenReturn(
        Future.successful(Left(UpdateCaseError.InvalidCaseId))
      )

      val ua: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DisclosureReferenceNumberPage, caseId).success.value
      )

      val result: Future[Result] = controller(ua).onSubmit()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(
        controllers.cancelCase.routes.CancelCaseDisclosureNotFoundController.onLoad().url
      )
    }

    "return Internal Server error when user answers incomplete for confirmation view" in {
      reset(mockSessionRepository)
      when(mockSessionRepository.remove(any())(any())).thenReturn(Future.successful("some-cred-id"))

      val ua: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
      val result: Future[Result]  = controller(ua).onSubmit()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

    "return Internal Server error is update fails" in {
      reset(mockUpdateCaseService)
      when(mockUpdateCaseService.updateCase(any(), any(), any())).thenReturn(
        Future.successful(Left(UpdateCaseError.UnexpectedError(Status.INTERNAL_SERVER_ERROR, Some("Not Working"))))
      )
      val result: Future[Result] = controller().onSubmit()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

    "return Redirect to CaseDisclosureClosed on CaseAlreadyClosed error" in {
      val caseId = "C181234567890123456789"

      reset(mockUpdateCaseService)
      when(mockUpdateCaseService.updateCase(any(), any(), any())).thenReturn(
        Future.successful(Left(CaseAlreadyClosed))
      )

      val ua: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DisclosureReferenceNumberPage, caseId).success.value
      )

      val result: Future[Result] = controller(ua).onSubmit()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(
        controllers.cancelCase.routes.CancelCaseDisclosureClosedController.onLoad().url
      )
    }

  }

}
