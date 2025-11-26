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

package controllers.underpayments

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.underpayments.PostponedVatAccountingFormProvider
import models.SelectedDutyTypes.Both
import models.UserAnswers
import models.importDetails.{NumberOfEntries, UserType}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.CheckModePage
import pages.importDetails.{ImporterNamePage, NumberOfEntriesPage, UserTypePage}
import pages.underpayments.{TempUnderpaymentTypePage, UnderpaymentCheckModePage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.underpayments.PostponedVatAccountingView

import scala.concurrent.Future

class PostponedVatAccountingControllerSpec extends ControllerSpecBase {

  trait Test {
    private lazy val view: PostponedVatAccountingView =
      app.injector.instanceOf[PostponedVatAccountingView]

    val mockSessionRepository: SessionRepository = mock[SessionRepository]

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("credId")
        .set(ImporterNamePage, "importerName")
        .success.value
        .set(UserTypePage, UserType.Representative)
        .success.value
    )
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: PostponedVatAccountingFormProvider = injector.instanceOf[PostponedVatAccountingFormProvider]

    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

    lazy val controller = new PostponedVatAccountingController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      formProvider,
      view,
      errorHandler,
      ec
    )

  }

  "GET PostponedVatAccounting Page" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return Internal Server Error" in new Test {
      override val userAnswers   = Some(UserAnswers("credId"))
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "POST PostponedVatAccounting Page" when {
    "payload contains valid data" should {

      "return a SEE OTHER response" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header when value is set to true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.underpayments.routes.PVAHandoffController.onLoad().url)
      }

      "return the correct location header when value is set to false and we're not in bulk flow" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(ImporterNamePage, "importerName")
            .success.value
            .set(UserTypePage, UserType.Representative)
            .success.value
            .set(NumberOfEntriesPage, NumberOfEntries.OneEntry)
            .success.value
        )

        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.reasons.routes.BoxGuidanceController.onLoad().url)
      }

      "return the correct location header when value is set to false and we need to update payment details" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(ImporterNamePage, "importerName")
            .success.value
            .set(UserTypePage, UserType.Representative)
            .success.value
            .set(TempUnderpaymentTypePage, Both)
            .success.value
        )

        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.paymentInfo.routes.DefermentController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
      }

      "return Internal Server Error" in new Test {
        override val userAnswers                             = Some(UserAnswers("credId"))
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

  "Back Link" when {
    "we came directly from CYA" should {
      "take us back to CYA" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(CheckModePage, true).success.value
        )

        controller.backLink(userAnswers.get) mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }

    "we came in check mode via Underpayment Summary" should {
      "take us back to Underpayment Summary" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentCheckModePage, true).success.value
        )

        controller.backLink(
          userAnswers.get
        ) mustBe controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
      }
    }

    "we are not in check mode" should {
      "take us back to Underpayment Summary" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

        controller.backLink(
          userAnswers.get
        ) mustBe controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
      }
    }
  }
}
