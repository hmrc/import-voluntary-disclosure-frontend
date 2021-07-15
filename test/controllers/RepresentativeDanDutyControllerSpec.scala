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
import models.SelectedDutyTypes.Duty
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.underpayments.UnderpaymentDetail
import models.UserAnswers
import models.importDetails.UserType
import pages._
import pages.importDetails.UserTypePage
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import views.html.RepresentativeDanDutyView

import scala.concurrent.Future

class RepresentativeDanDutyControllerSpec extends ControllerSpecBase {

  def buildForm(accountNumber: Option[String] = Some("1234567"),
                danType: Option[String] = Some("A")): Seq[(String, String)] =
    (accountNumber.map(_ => "accountNumber" -> accountNumber.get) ++
      danType.map(_ => "value" -> danType.get)).toSeq

  trait Test extends MockSessionRepository {
    private lazy val representativeDanDutyView: RepresentativeDanDutyView = app.injector.instanceOf[RepresentativeDanDutyView]

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

    lazy val controller = new RepresentativeDanDutyController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, representativeDanDutyView, form, ec)
  }

  "GET Representative Dan Duty page" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(
          UserAnswers("some-cred-id")
            .set(DefermentTypePage, "A").success.value
            .set(DefermentAccountPage, "1234567").success.value
        )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST Representative Duty Dan" when {
    "payload contains valid data and check mode is false" should {

      "return a SEE OTHER response and redirect to correct location when dan type is A" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("A")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.RepresentativeDanImportVATController.onLoad().url)
      }

      "return a SEE OTHER response and redirect to correct location when dan type is B" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("B")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.onLoad(Duty, "1234567").url)
      }

      "return a SEE OTHER response and redirect to correct location when dan type is C" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("C")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.RepresentativeDanImportVATController.onLoad().url)
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
        redirectLocation(result) mustBe Some(controllers.routes.CheckYourAnswersController.onLoad().url)
      }

      "payload contains invalid data" should {
        "return a BAD REQUEST" in new Test {
          val result: Future[Result] = controller.onSubmit(fakeRequest)
          status(result) mustBe Status.BAD_REQUEST
        }
      }
    }

    "payload contains valid data and user answers are changed from CYA" should {
      "redirect to DAN Import VAT page when user supplies account number 7654321 and user answers holds account number 1234567" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(
            UnderpaymentDetail("B00", 0.0, 1.0),
            UnderpaymentDetail("A00", 0.0, 1.0))).success.value
          .set(SplitPaymentPage, true).success.value
          .set(DefermentTypePage, "A").success.value
          .set(DefermentAccountPage, "1234567").success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("7654321"), danType = Some("A")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.RepresentativeDanImportVATController.onLoad().url)
      }

      "redirect to DAN Import VAT page when user supplies account type C and user answers holds account type A" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(
            UnderpaymentDetail("B00", 0.0, 1.0),
            UnderpaymentDetail("A00", 0.0, 1.0))).success.value
          .set(SplitPaymentPage, true).success.value
          .set(DefermentTypePage, "A").success.value
          .set(DefermentAccountPage, "1234567").success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("C")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.RepresentativeDanImportVATController.onLoad().url)
      }

      "redirect to UploadAuthority page when user supplies account type B and user answers holds account type C" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(UnderpaymentDetailSummaryPage, Seq(
            UnderpaymentDetail("B00", 0.0, 1.0),
            UnderpaymentDetail("A00", 0.0, 1.0))).success.value
          .set(SplitPaymentPage, true).success.value
          .set(DefermentTypePage, "A").success.value
          .set(DefermentAccountPage, "1234567").success.value
        )
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("B")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.routes.UploadAuthorityController.onLoad(Duty, "1234567").url)
      }
    }
  }

  "backLink" when {

    "not in change mode" should {
      "point to Split Payment page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.routes.SplitPaymentController.onLoad()
      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
          )
        lazy val result: Call = controller.backLink()
        result mustBe controllers.routes.CheckYourAnswersController.onLoad()
      }
    }
  }
}
