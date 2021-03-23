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
import forms.RepresentativeDanOneFormProvider
import mocks.repositories.MockSessionRepository
import models.{RepresentativeDanOne, UserAnswers}
import pages.{DefermentPage, RepresentativeDanOnePage, SplitPaymentPage}
import play.api.http.Status
import play.api.mvc.{Call, Result}
import play.api.test.Helpers._
import views.html.RepresentativeDanOneView

import scala.concurrent.Future

class RepresentativeDanOneControllerSpec extends ControllerSpecBase {

  def buildForm(accountNumber: Option[String] = Some("1234567"),
                danType: Option[String] = Some("A")): Seq[(String, String)] =
    (
      (accountNumber.map(_ => "accountNumber" -> accountNumber.get) ++
        danType.map(_ => "value" -> danType.get)).toSeq
      )

  trait Test extends MockSessionRepository {
    private lazy val representativeDanOneView: RepresentativeDanOneView = app.injector.instanceOf[RepresentativeDanOneView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: RepresentativeDanOneFormProvider = injector.instanceOf[RepresentativeDanOneFormProvider]
    val form: RepresentativeDanOneFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new RepresentativeDanOneController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, representativeDanOneView, form)
  }

  "GET Representative Dan One page" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] =
        Some(UserAnswers("some-cred-id").set(RepresentativeDanOnePage, RepresentativeDanOne("1234567", "A")).success.value)
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "the backLink functionality is called" should {
      "redirect to the deferment page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(DefermentPage, false).success.value
        )
        controller.backLink(userAnswers.get) mustBe Call("GET", controllers.routes.DefermentController.onLoad().url)
      }

      "redirect to the split payments page" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(
          UserAnswers("some-cred-id").set(SplitPaymentPage, true).success.value
        )
        controller.backLink(userAnswers.get) mustBe Call("GET", controllers.routes.SplitPaymentController.onLoad().url)
      }
    }
  }

  "POST Representative Dan One" when {
    "payload contains valid data" should {

      "return a SEE OTHER response and redirect to correct location when dan type is A" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("A")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.CheckYourAnswersController.onLoad().url)
      }

      "return a SEE OTHER response and redirect to correct location when dan type is B" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("B")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.RepresentativeDanOneController.onLoad().url)
      }

      "return a SEE OTHER response and redirect to correct location when dan type is C" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(accountNumber = Some("1234567"), danType = Some("C")): _*)
        lazy val result: Future[Result] = controller.onSubmit(request)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.CheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        private val request = fakeRequest.withFormUrlEncodedBody(buildForm(): _*)
        await(controller.onSubmit(request))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return a BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }
}
