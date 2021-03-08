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
import mocks.repositories.MockSessionRepository
import mocks.services.MockEoriDetailsService
import models.{ErrorModel, UserAnswers}
import pages._
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, status}
import utils.ReusableValues
import views.data.ConfirmEORIDetailsData
import views.html.ConfirmEORIDetailsView

import scala.concurrent.Future


class ConfirmEORIDetailsControllerSpec extends ControllerSpecBase with MockEoriDetailsService with ReusableValues {

  trait Test extends MockSessionRepository {
    private lazy val view: ConfirmEORIDetailsView = app.injector.instanceOf[ConfirmEORIDetailsView]

    MockedSessionRepository.set(Future.successful(true))

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    lazy val controller = new ConfirmEORIDetailsController(authenticatedAction, dataRetrievalAction,
      messagesControllerComponents, mockSessionRepository, mockEoriDetailsService, view)

    val importerAddressYes: Boolean = true


  }

  "GET /" should {
    "return OK" in new Test {
      setupMockRetrieveAddress(Right(eoriDetails))
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return error model" in new Test {
      setupMockRetrieveAddress(Left(ErrorModel(404, "")))
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.NOT_FOUND
    }

    "return HTML" in new Test {
      setupMockRetrieveAddress(Right(eoriDetails))
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id").set(ReuseKnowAddressPage, importerAddressYes).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "produce correct summary list" in new Test {
      val result = controller.summaryList(eoriDetails)
      val expectedResult = Some(ConfirmEORIDetailsData.details("GB987654321000", "Fast Food ltd"))
      result mustBe expectedResult
    }
  }


}
