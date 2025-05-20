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

package controllers.reasons

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.reasons.RemoveUnderpaymentReasonFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, ChangeUnderpaymentReason, UnderpaymentReason}
import pages.reasons.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.reasons.RemoveUnderpaymentReasonView

import scala.concurrent.Future

class RemoveUnderpaymentReasonControllerSpec extends ControllerSpecBase {

  private def fakeRequestGenerator(value: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody("value" -> value)

  def underpaymentReason(boxNumber: BoxNumber, itemNumber: Int = 0, original: String = "50", amended: String = "60") =
    UnderpaymentReason(boxNumber, itemNumber, original, amended)

  trait Test extends MockSessionRepository {
    private lazy val removeUnderpaymentReasonView: RemoveUnderpaymentReasonView =
      app.injector.instanceOf[RemoveUnderpaymentReasonView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: RemoveUnderpaymentReasonFormProvider = injector.instanceOf[RemoveUnderpaymentReasonFormProvider]
    val form: RemoveUnderpaymentReasonFormProvider         = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new RemoveUnderpaymentReasonController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      removeUnderpaymentReasonView,
      ec
    )
  }

  "GET onLoad" should {
    "return OK" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(
            ChangeUnderpaymentReasonPage,
            ChangeUnderpaymentReason(
              underpaymentReason(boxNumber = BoxNumber.Box22),
              underpaymentReason(boxNumber = BoxNumber.Box22)
            )
          ).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(
            ChangeUnderpaymentReasonPage,
            ChangeUnderpaymentReason(
              underpaymentReason(boxNumber = BoxNumber.Box22),
              underpaymentReason(boxNumber = BoxNumber.Box22)
            )
          ).success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "raise Run Time Exception" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
      val thrown = intercept[RuntimeException] {
        await(controller.onLoad(fakeRequest))
      }
      thrown.getMessage mustBe "No change reason found for remove"
    }

  }

  "POST onSubmit" when {
    "payload contains valid data" should {

      "return a SEE OTHER response when false" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                underpaymentReason(boxNumber = BoxNumber.Box22),
                underpaymentReason(boxNumber = BoxNumber.Box22)
              )
            ).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestGenerator("false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad().url)
      }

      "redirect to Reason Underpayment Summary page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentReasonsPage,
              Seq(
                underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1),
                underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 2)
              )
            ).success.value
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                original = underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1),
                changed = underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1)
              )
            ).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestGenerator("true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad().url
        )
      }

      "redirect to the Change Underpayment Reason page when selecting not to remove underpayment" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentReasonsPage,
              Seq(underpaymentReason(boxNumber = BoxNumber.OtherItem, itemNumber = 1))
            ).success.value
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                original = underpaymentReason(boxNumber = BoxNumber.OtherItem, itemNumber = 1),
                changed = underpaymentReason(boxNumber = BoxNumber.OtherItem, itemNumber = 1)
              )
            ).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestGenerator("false")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(
          controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad().url
        )
      }

      "redirect to Box Guidance page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentReasonsPage,
              Seq(underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1))
            ).success.value
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                original = underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1),
                changed = underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1)
              )
            ).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestGenerator("true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        redirectLocation(result) mustBe Some(controllers.reasons.routes.BoxGuidanceController.onLoad().url)
      }

      "return an Internal Server Error" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id")
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                underpaymentReason(boxNumber = BoxNumber.Box22),
                underpaymentReason(boxNumber = BoxNumber.Box22)
              )
            ).success.value
        )
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestGenerator("true")
        lazy val result: Future[Result]                      = controller.onSubmit(request)
        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }

      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentReasonsPage,
              Seq(underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1))
            ).success.value
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                original = underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1),
                changed = underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1)
              )
            ).success.value
        )
        private val request = fakeRequestGenerator("true")
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentReasonsPage,
              Seq(underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1))
            ).success.value
            .set(
              ChangeUnderpaymentReasonPage,
              ChangeUnderpaymentReason(
                original = underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1),
                changed = underpaymentReason(boxNumber = BoxNumber.Box35, itemNumber = 1)
              )
            ).success.value
        )
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

}
