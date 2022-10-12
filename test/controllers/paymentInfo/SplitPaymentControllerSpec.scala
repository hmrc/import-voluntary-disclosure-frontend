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

package controllers.paymentInfo

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.paymentInfo.SplitPaymentFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.importDetails.UserType
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.underpayments.UnderpaymentDetail
import pages._
import pages.importDetails.UserTypePage
import pages.paymentInfo.{DefermentPage, SplitPaymentPage}
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.paymentInfo.SplitPaymentView

import scala.concurrent.Future

class SplitPaymentControllerSpec extends ControllerSpecBase {

  val splitPaymentValue: Option[UserAnswers] = Some(
    UserAnswers("some-cred-id")
      .set(
        SplitPaymentPage,
        true
      ).success.value
  )

  private def fakeRequestGenerator(value: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "value" -> value
    )

  trait Test extends MockSessionRepository {
    private lazy val splitPaymentView: SplitPaymentView = app.injector.instanceOf[SplitPaymentView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

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

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: SplitPaymentFormProvider = injector.instanceOf[SplitPaymentFormProvider]
    val form: SplitPaymentFormProvider         = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new SplitPaymentController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      splitPaymentView,
      ec
    )
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(DefermentPage, true).success.value
          .set(SplitPaymentPage, true).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return a SEE OTHER response when true" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
      }

      "return the correct location header" in new Test {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad().url
        )
      }

      "return a SEE OTHER split payment response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = splitPaymentValue
        lazy val result: Future[Result] = controller.onSubmit(
          fakeRequestGenerator("false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.paymentInfo.routes.RepresentativeDanController.onLoad().url)
      }

      "return a SEE OTHER split payment response when correct data is sent and in check mode" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit(
          fakeRequestGenerator("false")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        await(controller.onSubmit(request))
        verifyCalls()
      }

      "return the correct location header when split payment is selected but data held in user answers is no split payment" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(UserTypePage, UserType.Representative).success.value
            .set(
              UnderpaymentDetailSummaryPage,
              Seq(UnderpaymentDetail("B00", 0.0, 1.0), UnderpaymentDetail("A00", 0.0, 1.0))
            ).success.value
            .set(SplitPaymentPage, false).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad().url
        )
      }

      "return the correct location header when no split payment is selected but data held in user answers is split payment" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(UserTypePage, UserType.Representative).success.value
            .set(
              UnderpaymentDetailSummaryPage,
              Seq(UnderpaymentDetail("B00", 0.0, 1.0), UnderpaymentDetail("A00", 0.0, 1.0))
            ).success.value
            .set(SplitPaymentPage, true).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequest.withFormUrlEncodedBody("value" -> "false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.paymentInfo.routes.RepresentativeDanController.onLoad().url)
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
      "point to deferment page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, false).success.value
          )
        lazy val result: Call = controller.backLink
        result mustBe controllers.paymentInfo.routes.DefermentController.onLoad()
      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
          )
        lazy val result: Call = controller.backLink
        result mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }
  }

}
