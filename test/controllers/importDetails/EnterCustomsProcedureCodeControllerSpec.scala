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

package controllers.importDetails

import java.time.LocalDate

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.importDetails.EnterCustomsProcedureCodeFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.importDetails.EntryDetails
import models.requests._
import pages.CheckModePage
import pages.importDetails.{EnterCustomsProcedureCodePage, EntryDetailsPage}
import play.api.http.Status
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.importDetails.EnterCustomsProcedureCodeView

import scala.concurrent.Future

class EnterCustomsProcedureCodeControllerSpec extends ControllerSpecBase {

  val userAnswersWithEntryDetails: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
    .set(
      EntryDetailsPage,
      EntryDetails("123", "123456Q", LocalDate of(2020, 1, 1))
    ).success.value
    .set(CheckModePage, false).success.value
  )

  private def fakeRequestGenerator(cpc: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "cpc" -> cpc
    )

  trait Test extends MockSessionRepository {
    lazy val controller = new EnterCustomsProcedureCodeController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      EnterCustomsProcedureCodeView,
      form,
      ec
    )
    private lazy val EnterCustomsProcedureCodeView = app.injector.instanceOf[EnterCustomsProcedureCodeView]
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
    val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
    val formProvider: EnterCustomsProcedureCodeFormProvider = injector.instanceOf[EnterCustomsProcedureCodeFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: EnterCustomsProcedureCodeFormProvider = formProvider
  }

  "GET onLoad" when {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId")
        .set(EnterCustomsProcedureCodePage, "1234A12").success.value
      )
      val result: Future[Result] = controller.onLoad(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data when check mode is false" should {

      "return a SEE OTHER response when correct data with numeric only values" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234567"))
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.underpayments.routes.UnderpaymentStartController.onLoad().url)
      }
      "return a SEE OTHER response when correct data with an alphanumeric value" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234A12"))
        status(result) mustBe Status.SEE_OTHER
      }
      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        await(controller.onSubmit(fakeRequestGenerator("1234567")))
        verifyCalls()
      }

    }

    "payload contains valid data when check mode is true" should {

      "return a SEE OTHER response when correct data with numeric only values" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate of(2020, 1, 1))).success.value
          .set(CheckModePage, true).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234567"))
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.CheckYourAnswersController.onLoad().url)
      }
      "return a SEE OTHER response when correct data with an alphanumeric value" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate of(2020, 1, 1))).success.value
          .set(CheckModePage, true).success.value
        )
        lazy val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234A12"))
        status(result) mustBe Status.SEE_OTHER
      }
      "update the UserAnswers in session" in new Test {
        override val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id")
          .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate of(2020, 1, 1))).success.value
          .set(CheckModePage, true).success.value
        )
        await(controller.onSubmit(fakeRequestGenerator("1234567")))
        verifyCalls()
      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST when invalid data is sent" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("123456!"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data is more than 7 in length" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("12345678"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data there is an alpha character at the beginning" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("A2345678"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST when data there is an alpha character at the end" in new Test {
        override val userAnswers: Option[UserAnswers] = userAnswersWithEntryDetails
        val result: Future[Result] = controller.onSubmit(fakeRequestGenerator("1234567A"))
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST" in new Test {
        val result: Future[Result] = controller.onSubmit(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }
    }
  }

  "backLink" when {

    "not in change mode" should {
      "point to acceptance date page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, false).success.value
          )
        lazy val result: Option[Call] = controller.backLink()
        result mustBe Some(controllers.importDetails.routes.OneCustomsProcedureCodeController.onLoad())
      }
    }

    "in change mode" should {
      "point to Check Your Answers page" in new Test {
        override val userAnswers: Option[UserAnswers] =
          Some(UserAnswers("some-cred-id")
            .set(CheckModePage, true).success.value
          )
        lazy val result: Option[Call] = controller.backLink()
        result mustBe None
      }
    }
  }

}
