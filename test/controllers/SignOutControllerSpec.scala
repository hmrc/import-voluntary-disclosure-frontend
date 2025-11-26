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

package controllers

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import models.UserAnswers
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import repositories.SessionRepository

import java.net.URLEncoder
import scala.concurrent.Future

class SignOutControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

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

  val controller = new SignOutController(
    authenticatedAction,
    dataRetrievalAction,
    messagesControllerComponents,
    dataRequiredAction,
    mockSessionRepository,
    ec,
    appConfig
  )

  override def beforeEach(): Unit =
    when(mockSessionRepository.remove(any())(any())).thenReturn(Future.successful("OK"))

  "GET signOut" should {
    "return 303" in {
      val result: Future[Result] = controller.signOut()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "redirect to the correct sign out URL" in {
      val result: Future[Result]     = controller.signOut()(fakeRequest)
      val feedbackURLEncoded: String = URLEncoder.encode(s"${appConfig.surveyUrl}", "UTF-8")
      redirectLocation(result) mustBe Some(s"${appConfig.signOutUrl}?continue=$feedbackURLEncoded")
    }

  }

  "GET sign out unidentified" should {
    "return 303 & redirect to correct sign out URL" in {
      val result: Future[Result] = controller.signOutUnidentified(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      val feedbackURLEncoded: String = URLEncoder.encode(s"${appConfig.surveyUrl}", "UTF-8")
      redirectLocation(result) mustBe Some(s"${appConfig.signOutUrl}?continue=$feedbackURLEncoded")
    }
  }

}
