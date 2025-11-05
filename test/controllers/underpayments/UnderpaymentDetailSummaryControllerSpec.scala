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
import forms.underpayments.UnderpaymentDetailSummaryFormProvider
import models.SelectedDutyTypes.{Both, Duty, Vat}
import models.UserAnswers
import models.importDetails.NumberOfEntries
import models.importDetails.UserType.Representative
import models.underpayments.UnderpaymentDetail
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.CheckModePage
import pages.importDetails.{NumberOfEntriesPage, UserTypePage}
import pages.paymentInfo.SplitPaymentPage
import pages.underpayments.{TempUnderpaymentTypePage, UnderpaymentDetailSummaryPage}
import play.api.mvc.Result
import play.api.test.Helpers
import play.api.test.Helpers.{contentType, defaultAwaitTimeout, redirectLocation, status}
import play.mvc.Http.Status
import repositories.SessionRepository
import utils.ReusableValues
import views.html.underpayments.UnderpaymentDetailSummaryView

import scala.concurrent.Future

class UnderpaymentDetailSummaryControllerSpec extends ControllerSpecBase with ReusableValues {

  trait Test {
    val mockSessionRepository: SessionRepository = mock[SessionRepository]

    private lazy val underpaymentDetailSummaryView: UnderpaymentDetailSummaryView =
      app.injector.instanceOf[UnderpaymentDetailSummaryView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UnderpaymentDetailSummaryFormProvider = injector.instanceOf[UnderpaymentDetailSummaryFormProvider]
    val form: UnderpaymentDetailSummaryFormProvider         = formProvider

    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

    lazy val controller = new UnderpaymentDetailSummaryController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      underpaymentDetailSummaryView,
      form,
      ec
    )
  }

  "GET onLoad" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return SEE_OTHER when no UnderpaymentDetails exist" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentDetailSummaryPage, allUnderpaymentDetailsSelected()).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      Helpers.charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER Underpayment Type page when true is selected" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId").set(
            UnderpaymentDetailSummaryPage,
            Seq(UnderpaymentDetail("A00", 0.0, 1.0))
          ).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "true")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.UnderpaymentTypeController.onLoad().url)
      }

      "return a SEE OTHER Box Guidance page when false is selected and One Entry" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.reasons.routes.BoxGuidanceController.onLoad().url)
      }

      "return a SEE OTHER Bulk Upload File page when false is selected and Bulk Entry" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.MoreThanOneEntry).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.docUpload.routes.BulkUploadFileController.onLoad().url)
      }

      "return a SEE OTHER Postponed VAT page when false is selected duty is VAT only" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.PostponedVatAccountingController.onLoad().url)
      }

      "return a SEE OTHER Postponed VAT page when false is selected duty is VAT only but we're in bulk flow" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.MoreThanOneEntry).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.docUpload.routes.BulkUploadFileController.onLoad().url)
      }

      "return a SEE OTHER Check Your Answers page when false is selected" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(CheckModePage, true).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "return a SEE OTHER Box Guidance page when in Representative flow in first pass and not Bulk entry" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(UserTypePage, Representative).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.reasons.routes.BoxGuidanceController.onLoad().url)
      }

      "return a SEE OTHER Box Guidance page when in Representative flow in first pass and Bulk entry" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(UserTypePage, Representative).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.MoreThanOneEntry).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.docUpload.routes.BulkUploadFileController.onLoad().url)
      }

      "return a SEE OTHER Check Your Answers page when in Representative flow Both and Both" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(TempUnderpaymentTypePage, Both).success.value
            .set(
              UnderpaymentDetailSummaryPage,
              Seq(
                UnderpaymentDetail("A00", 0.0, 1.0),
                UnderpaymentDetail("B00", 0.0, 1.0)
              )
            ).success.value
            .set(UserTypePage, Representative).success.value
            .set(CheckModePage, true).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "return a SEE OTHER Deferment page when in Representative flow VatOrDuty and Both" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(TempUnderpaymentTypePage, Vat).success.value
            .set(
              UnderpaymentDetailSummaryPage,
              Seq(
                UnderpaymentDetail("A00", 0.0, 1.0),
                UnderpaymentDetail("B00", 0.0, 1.0)
              )
            ).success.value
            .set(UserTypePage, Representative).success.value
            .set(CheckModePage, true).success.value
        )

        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.paymentInfo.routes.DefermentController.onLoad().url)
      }

      "return a SEE OTHER Deferment page when in Representative flow Both and VatOrDuty and Split initially" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(TempUnderpaymentTypePage, Both).success.value
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(UserTypePage, Representative).success.value
            .set(SplitPaymentPage, true).success.value
            .set(CheckModePage, true).success.value
        )

        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.paymentInfo.routes.DefermentController.onLoad().url)
      }

      "return a SEE OTHER Postponed VAT page when in Representative flow and duty is VAT only" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
        )

        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.PostponedVatAccountingController.onLoad().url)
      }

      "return a SEE OTHER Postponed VAT page when in Representative flow going from Both to Vat only" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(TempUnderpaymentTypePage, Both).success.value
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
            .set(UserTypePage, Representative).success.value
            .set(CheckModePage, true).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
        )

        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.PostponedVatAccountingController.onLoad().url)
      }

      "return a SEE OTHER Postponed VAT page when in Representative flow going from Duty to Vat only" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(TempUnderpaymentTypePage, Duty).success.value
            .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
            .set(UserTypePage, Representative).success.value
            .set(CheckModePage, true).success.value
            .set(NumberOfEntriesPage, NumberOfEntries.OneEntry).success.value
        )

        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.PostponedVatAccountingController.onLoad().url)
      }
    }

    "return a SEE OTHER Deferment page when in Representative flow going from Both to Vat only in Bulk flow" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId")
          .set(TempUnderpaymentTypePage, Both).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("B00", 0.0, 1.0))).success.value
          .set(UserTypePage, Representative).success.value
          .set(CheckModePage, true).success.value
          .set(NumberOfEntriesPage, NumberOfEntries.MoreThanOneEntry).success.value
          .set(SplitPaymentPage, true).success.value
      )

      lazy val result: Future[Result] = controller.onSubmit()(
        fakeRequest.withFormUrlEncodedBody("value" -> "false")
      )
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe
        Some(controllers.paymentInfo.routes.DefermentController.onLoad().url)
    }

    "payload contains invalid data" should {
      "return BAD REQUEST when no value is sent" in new Test {
        val result: Future[Result] = controller.onSubmit()(fakeRequest.withFormUrlEncodedBody("" -> ""))
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

  "cya" should {
    "return redirect to the onLoad of Underpayment Detail Summary Controller when called" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentDetailSummaryPage, Seq(UnderpaymentDetail("A00", 0.0, 1.0))).success.value
      )
      val result: Future[Result] = controller.cya()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe
        Some(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad().url)
    }
  }

}
