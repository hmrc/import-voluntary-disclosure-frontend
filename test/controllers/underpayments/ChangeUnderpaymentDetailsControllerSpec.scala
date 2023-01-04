/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.underpayments.UnderpaymentDetailsFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.underpayments.{UnderpaymentAmount, UnderpaymentDetail}
import pages.underpayments.{UnderpaymentDetailSummaryPage, UnderpaymentDetailsPage}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.underpayments.ChangeUnderpaymentDetailsView

import scala.concurrent.Future

class ChangeUnderpaymentDetailsControllerSpec extends ControllerSpecBase {

  private final val underpaymentType = "A00"

  trait Test extends MockSessionRepository {

    private lazy val changeUnderpaymentDetailsView: ChangeUnderpaymentDetailsView =
      app.injector.instanceOf[ChangeUnderpaymentDetailsView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UnderpaymentDetailsFormProvider = injector.instanceOf[UnderpaymentDetailsFormProvider]
    val form: UnderpaymentDetailsFormProvider         = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new ChangeUnderpaymentDetailsController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      changeUnderpaymentDetailsView,
      ec
    )
  }

  "GET onLoad" when {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(underpaymentType)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML after pre-populating form from underpayment details" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UnderpaymentDetailsPage, UnderpaymentAmount(original = 10, amended = 20)).success.value
      )
      val result: Future[Result] = controller.onLoad(underpaymentType)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "return HTML after pre-populating form from underpayment details Summary" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(
            UnderpaymentDetailSummaryPage,
            Seq(UnderpaymentDetail(underpaymentType, original = 10, amended = 20))
          ).success.value
      )
      val result: Future[Result] = controller.onLoad(underpaymentType)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER response for correct data changed from Summary Page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId")
            .set(
              UnderpaymentDetailSummaryPage,
              Seq(UnderpaymentDetail(underpaymentType, original = 10, amended = 20))
            ).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit(underpaymentType)(
          fakeRequest.withFormUrlEncodedBody("original" -> "40", "amended" -> "50")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.UnderpaymentDetailConfirmController.onLoad(underpaymentType, true).url)
      }

      "return a SEE OTHER response for correct data changed from first addition of underpayment" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
        lazy val result: Future[Result] = controller.onSubmit(underpaymentType)(
          fakeRequest.withFormUrlEncodedBody("original" -> "40", "amended" -> "50")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.UnderpaymentDetailConfirmController.onLoad(underpaymentType, false).url)
      }

      "update the UserAnswers in session" in new Test {
        await(
          controller.onSubmit(underpaymentType)(
            fakeRequest.withFormUrlEncodedBody("original" -> "40", "amended" -> "50")
          )
        )
        verifyCalls()
      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST when no value is sent" in new Test {
        val result: Future[Result] = controller.onSubmit(underpaymentType)(fakeRequest.withFormUrlEncodedBody("" -> ""))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when the original value is greater than amended" in new Test {
        val result: Future[Result] = controller.onSubmit(underpaymentType)(
          fakeRequest.withFormUrlEncodedBody("original" -> "60", "amended" -> "40")
        )
        status(result) mustBe Status.BAD_REQUEST
      }

    }

  }

}
