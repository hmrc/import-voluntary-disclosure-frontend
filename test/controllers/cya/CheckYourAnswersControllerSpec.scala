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

package controllers.cya

import base.ControllerSpecBase
import config.ErrorHandler
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import mocks.services.MockSubmissionService
import models._
import models.importDetails.{EntryDetails, UserType}
import pages.importDetails._
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.cya.{CheckYourAnswersView, ImporterConfirmationView, RepresentativeConfirmationView}

import java.time.LocalDate
import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository with MockSubmissionService {

    private def setupConnectorMock(response: Either[ErrorModel, SubmissionResponse]) =
      setupMockCreateCase(response)

    private lazy val checkYourAnswersView: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]
    private lazy val importerConfirmationView: ImporterConfirmationView =
      app.injector.instanceOf[ImporterConfirmationView]
    private lazy val repConfirmationView: RepresentativeConfirmationView =
      app.injector.instanceOf[RepresentativeConfirmationView]

    val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    MockedSessionRepository.set(Future.successful(true))
    MockedSessionRepository.remove(Future.successful("OK"))

    lazy val serviceMock: Either[ErrorModel, SubmissionResponse] = Right(SubmissionResponse("123"))
    lazy val controller = {
      setupConnectorMock(serviceMock)
      new CheckYourAnswersController(
        authenticatedAction,
        dataRetrievalAction,
        dataRequiredAction,
        messagesControllerComponents,
        mockSessionRepository,
        mockSubmissionService,
        checkYourAnswersView,
        importerConfirmationView,
        repConfirmationView,
        errorHandler,
        ec
      )
    }
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "GET onSubmit" should {

    "return Redirect to the importer confirmation view" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Importer).success.value
          .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate.now())).success.value
          .set(
            KnownEoriDetailsPage,
            EoriDetails(
              "GB123456789",
              "Test User",
              ContactAddress(
                addressLine1 = "address one",
                countryCode = "GB",
                city = "Test city",
                postalCode = Some("AA00AA")
              ),
              Some("123456789")
            )
          ).success.value
      )
      val result: Future[Result] = controller.onSubmit()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return Redirect to the representative confirmation view" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate.now())).success.value
          .set(ImporterNamePage, "Test User").success.value
          .set(ImporterEORINumberPage, "GB123456789").success.value
          .set(
            KnownEoriDetailsPage,
            EoriDetails(
              "GB123456789",
              "Test User",
              ContactAddress(
                addressLine1 = "address one",
                countryCode = "GB",
                city = "Test city",
                postalCode = Some("AA00AA")
              ),
              Some("123456789")
            )
          ).success.value
      )
      val result: Future[Result] = controller.onSubmit()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return Internal Server error when user answers incomplete for confirmation view" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate.now())).success.value
          .set(ImporterNamePage, "Test User").success.value
          .set(ImporterEORINumberPage, "GB123456789").success.value
      )
      val result: Future[Result] = controller.onSubmit()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

    "return Internal Server error is submission fails" in new Test {
      override lazy val serviceMock = Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Not Working"))
      val result: Future[Result]    = controller.onSubmit()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }

}
