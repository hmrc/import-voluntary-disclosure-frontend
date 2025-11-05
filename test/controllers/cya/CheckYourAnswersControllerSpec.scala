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
import controllers.actions.FakeDataRetrievalAction
import models._
import models.importDetails.{EntryDetails, UserType}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import pages.importDetails._
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import repositories.SessionRepository
import services.SubmissionService
import views.html.cya.{CheckYourAnswersView, ImporterConfirmationView, RepresentativeConfirmationView}

import java.time.LocalDate
import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]
  val mockSubmissionService: SubmissionService = mock[SubmissionService]

  val checkYourAnswersView: CheckYourAnswersView          = app.injector.instanceOf[CheckYourAnswersView]
  val importerConfirmationView: ImporterConfirmationView  = app.injector.instanceOf[ImporterConfirmationView]
  val repConfirmationView: RepresentativeConfirmationView = app.injector.instanceOf[RepresentativeConfirmationView]

  val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

  def cyaController(ua: Option[UserAnswers] = userAnswers): CheckYourAnswersController = {
    new CheckYourAnswersController(
      authenticatedAction,
      new FakeDataRetrievalAction(ua),
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

  override def beforeEach(): Unit = {
    when(mockSessionRepository.remove(any())(any())).thenReturn(Future.successful("OK"))
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
    when(mockSubmissionService.createCase(any(), any(), any())).thenReturn(
      Future.successful(Right(SubmissionResponse("123")))
    )
  }

  "GET onLoad" should {
    "return OK" in {
      val result: Future[Result] = cyaController().onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val result: Future[Result] = cyaController().onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "GET onSubmit" should {

    "return Redirect to the importer confirmation view" in {
      val userAnswers: Option[UserAnswers] = Some(
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
      val result: Future[Result] = cyaController(userAnswers).onSubmit()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return Redirect to the representative confirmation view" in {
      val userAnswers: Option[UserAnswers] = Some(
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
      val result: Future[Result] = cyaController(userAnswers).onSubmit()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return OK even if entryDetails are missing" in {
      val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
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
      val result: Future[Result] = cyaController(userAnswers).onSubmit()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return Internal Server error when user answers incomplete for confirmation view" in {
      val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UserTypePage, UserType.Representative).success.value
          .set(EntryDetailsPage, EntryDetails("123", "123456Q", LocalDate.now())).success.value
          .set(ImporterNamePage, "Test User").success.value
          .set(ImporterEORINumberPage, "GB123456789").success.value
      )
      val result: Future[Result] = cyaController(userAnswers).onSubmit()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

    "return Internal Server error is submission fails" in {
      Mockito.reset(mockSubmissionService)
      when(mockSubmissionService.createCase(any(), any(), any())).thenReturn(
        Future.successful(Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Not Working")))
      )
      val result: Future[Result] = cyaController(userAnswers).onSubmit()(fakeRequest)
      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }

}
