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
import forms.RepresentativeDanFormProvider
import mocks.repositories.MockSessionRepository
import models.SelectedDutyTypes.{Duty, Vat}
import models._
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.underpayments.UnderpaymentDetail
import pages._
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import views.html.RepresentativeDanImportVATView
import java.time.LocalDateTime

import models.importDetails.UserType
import pages.importDetails.UserTypePage

import scala.concurrent.Future

class RepresentativeDanImportVATControllerSpec extends ControllerSpecBase {

  def buildForm(accountNumber: Option[String] = Some("1234567"),
                danType: Option[String] = Some("A")): Seq[(String, String)] =
    (
      (accountNumber.map(_ => "accountNumber" -> accountNumber.get) ++
        danType.map(_ => "value" -> danType.get)).toSeq
      )

  trait Test extends MockSessionRepository {
    private lazy val representativeDanView: RepresentativeDanImportVATView = app.injector.instanceOf[RepresentativeDanImportVATView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
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

    val formProvider: RepresentativeDanFormProvider = injector.instanceOf[RepresentativeDanFormProvider]
    val form: RepresentativeDanFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new RepresentativeDanImportVATController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, representativeDanView, form, ec)
  }

  "GET Representative Dan Import VAT page" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(
          UserAnswers("some-cred-id")
            .set(AdditionalDefermentTypePage, "A").success.value
            .set(AdditionalDefermentNumberPage, "1234567").success.value
        )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST Representative Dan Import VAT page" when {
    "payload contains valid data and check mode is false" should {

      "return a SEE OTHER response and redirect to correct location when dan type is A" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("A")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "return a SEE OTHER response and redirect to correct location when dan type is B" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("B")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.onLoad(Vat, "1234567").url)
      }

      "return a SEE OTHER response and redirect to correct location when dan type is C" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("C")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(): _*)
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains valid data and check mode is true" should {
      "return a SEE OTHER response and redirect to correct location when in check mode" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
          )
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("C")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "payload contains valid data and user answers are changed from CYA" should {
        "redirect to CYA when user supplies account number 7654321 and user answers holds account number 1234567" in new Test {
          override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
            .set(UserTypePage, UserType.Representative).success.value
            .set(UnderpaymentDetailSummaryPage, Seq(
              UnderpaymentDetail("B00", 0.0, 1.0),
              UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(SplitPaymentPage, true).success.value
            .set(AdditionalDefermentTypePage, "A").success.value
            .set(AdditionalDefermentNumberPage, "1234567").success.value
          )
          private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("7654321"), danType = Some("A")): _*)
          lazy val result: Future[Result] = controller.onSubmit(request)
          redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
        }

        "redirect to CYA when user supplies account type C and user answers holds account type A" in new Test {
          override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
            .set(UserTypePage, UserType.Representative).success.value
            .set(UnderpaymentDetailSummaryPage, Seq(
              UnderpaymentDetail("B00", 0.0, 1.0),
              UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(SplitPaymentPage, true).success.value
            .set(AdditionalDefermentTypePage, "A").success.value
            .set(AdditionalDefermentNumberPage, "1234567").success.value
          )
          private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("C")): _*)
          lazy val result: Future[Result] = controller.onSubmit(request)
          redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
        }

        "redirect to UploadAuthority page when user supplies account type B and user answers holds account type C" in new Test {
          override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
            .set(UserTypePage, UserType.Representative).success.value
            .set(UnderpaymentDetailSummaryPage, Seq(
              UnderpaymentDetail("B00", 0.0, 1.0),
              UnderpaymentDetail("A00", 0.0, 1.0))).success.value
            .set(SplitPaymentPage, true).success.value
            .set(AdditionalDefermentTypePage, "A").success.value
            .set(AdditionalDefermentNumberPage, "1234567").success.value
          )
          private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("B")): _*)
          lazy val result: Future[Result] = controller.onSubmit(request)
          redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.onLoad(Vat, "1234567").url)
        }
      }

      "redirect to CYA and remove VAT proof of authority when user supplies account type C and user answers holds account type A" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(
            UnderpaymentDetail("B00", 0.0, 1.0),
            UnderpaymentDetail("A00", 0.0, 1.0))).success.value
          .set(SplitPaymentPage, true).success.value
          .set(DefermentTypePage, "B").success.value
          .set(DefermentAccountPage, "1234567").success.value
          .set(AdditionalDefermentTypePage, "B").success.value
          .set(AdditionalDefermentNumberPage, "7654321").success.value
          .set(UploadAuthorityPage, Seq(
            UploadAuthority("1234567",
              Duty,
              FileUploadInfo(
                "file-ref-1",
                "DutyDocument.pdf",
                "http://localhost:9570/upscan/download/b1bd66aa-97df-4302-931f-f40a5702a14b",
                LocalDateTime.of(2020, 1, 10, 10, 31),
                "10b53aa59c8a893dc6b8708af3732a90e1c53f902c3656feeb43dba8695054e5",
                "application/pdf"
              )),
            UploadAuthority("7654321",
              Vat,
              FileUploadInfo(
                "file-ref-1",
                "VATDocument.pdf",
                "http://localhost:9570/upscan/download/5e922a0f-d5ad-4aa6-9977-45a83096f71d",
                LocalDateTime.of(2020, 1, 10, 10, 30),
                "10b53aa59c8a893dc6b8708af3732a90e1c53f902c3656feeb43dba8695054e6",
                "application/pdf"
              ))
          )).success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("7654321"), danType = Some("C")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
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
      "point to Representative DAN Duty page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.routes.RepresentativeDanDutyController.onLoad()
      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }
  }

  "sameAccountNumber" when {

    "same value for duty account number and vat account number submitted" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DefermentAccountPage, "1234567").success.value
          .set(DefermentTypePage, "C").success.value
          .set(AdditionalDefermentNumberPage, "1234567").success.value
          .set(AdditionalDefermentTypePage, "A").success.value

      )
      controller.sameAccountNumber("1234567", "1234567", userAnswers.get) mustBe true
      val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("C")): _*)
      controller.onSubmit(request)
    }

    "different value for duty account number and vat account number submitted" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(DefermentAccountPage, "1234567").success.value
          .set(DefermentTypePage, "C").success.value
          .set(AdditionalDefermentNumberPage, "7654321").success.value
          .set(AdditionalDefermentTypePage, "A").success.value

      )
      controller.sameAccountNumber("1234567", "7654321", userAnswers.get) mustBe false
      val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("7654321"), danType = Some("C")): _*)
      controller.onSubmit(request)
    }

  }

}
