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
import forms.underpayments.UnderpaymentTypeFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import pages.underpayments.{UnderpaymentDetailSummaryPage, UnderpaymentTypePage}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import utils.ReusableValues
import views.html.underpayments.UnderpaymentTypeView

import scala.concurrent.Future

class UnderpaymentTypeControllerSpec extends ControllerSpecBase with ReusableValues {

  trait Test extends MockSessionRepository {
    private lazy val underpaymentTypeView: UnderpaymentTypeView = app.injector.instanceOf[UnderpaymentTypeView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: UnderpaymentTypeFormProvider = injector.instanceOf[UnderpaymentTypeFormProvider]
    val form: UnderpaymentTypeFormProvider         = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new UnderpaymentTypeController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      underpaymentTypeView,
      form,
      ec
    )
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return SEE_OTHER" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentDetailSummaryPage, allUnderpaymentDetailsSelected()).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "return OK when some entries are selected" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentDetailSummaryPage, someUnderpaymentDetailsSelected()).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return OK when no entries are selected" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return OK with empty Sequence" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentDetailSummaryPage, Seq.empty).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("credId").set(UnderpaymentTypePage, "A00").success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER entry level response when correct data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
        lazy val result: Future[Result] = controller.onSubmit()(
          fakeRequest.withFormUrlEncodedBody("value" -> "A00")
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.underpayments.routes.UnderpaymentDetailsController.onLoad("A00").url)
      }

      "update the UserAnswers in session" in new Test {
        await(controller.onSubmit()(fakeRequest.withFormUrlEncodedBody("value" -> "A00")))
        verifyCalls()
      }

    }

    "payload contains invalid data" should {
      "return BAD REQUEST when no value is sent" in new Test {
        val result: Future[Result] = controller.onSubmit()(fakeRequest.withFormUrlEncodedBody("" -> ""))
        status(result) mustBe Status.BAD_REQUEST
      }
    }

  }

  "Back Link" when {

    "there are no existing underpayment types back button" should {
      "take you to the underpayments start page" in new Test {
        controller.backLink(
          userAnswers.get
        ) mustBe controllers.underpayments.routes.UnderpaymentStartController.onLoad()
      }
    }

    "there are existing underpayment types back button" should {
      "take you to the underpayments summary page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("credId").set(UnderpaymentDetailSummaryPage, allUnderpaymentDetailsSelected()).success.value
        )
        controller.backLink(
          userAnswers.get
        ) mustBe controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
      }
    }

  }

}
