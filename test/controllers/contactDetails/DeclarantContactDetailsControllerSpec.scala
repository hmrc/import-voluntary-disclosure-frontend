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

package controllers.contactDetails

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.contactDetails.DeclarantContactDetailsFormProvider
import models.importDetails.NumberOfEntries.{MoreThanOneEntry, OneEntry}
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import models.{ContactDetails, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import pages.CheckModePage
import pages.contactDetails.DeclarantContactDetailsPage
import pages.importDetails.NumberOfEntriesPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import repositories.SessionRepository
import views.html.contactDetails.DeclarantContactDetailsView

import scala.concurrent.Future

class DeclarantContactDetailsControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  private def fakeRequestGenerator(
    fullName: String,
    email: String,
    phoneNumber: String
  ): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "fullName"    -> fullName,
      "email"       -> email,
      "phoneNumber" -> phoneNumber
    )

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private lazy val declarantContactDetailsView = app.injector.instanceOf[DeclarantContactDetailsView]

  val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

  val formProvider: DeclarantContactDetailsFormProvider = injector.instanceOf[DeclarantContactDetailsFormProvider]

  val form: DeclarantContactDetailsFormProvider = formProvider

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

  private def declarantContactDetailsController(
    optUserAnswers: Option[UserAnswers] = userAnswers
  ): DeclarantContactDetailsController = {
    new DeclarantContactDetailsController(
      authenticatedAction,
      new FakeDataRetrievalAction(optUserAnswers),
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      declarantContactDetailsView,
      ec
    )
  }

  override def beforeEach(): Unit =
    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

  "GET onLoad" when {
    "return OK" in {
      val result: Future[Result] = declarantContactDetailsController().onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in {
      val userAnswers: Option[UserAnswers] = Option(
        UserAnswers("some-cred-id").set(
          DeclarantContactDetailsPage,
          ContactDetails("First Second", "email@email.com", "+1234567890")
        ).success.value
      )
      val result: Future[Result] = declarantContactDetailsController(userAnswers).onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {

      "return a SEE OTHER response when correct data is sent" in {
        lazy val result: Future[Result] = declarantContactDetailsController().onSubmit()(
          fakeRequestGenerator(
            fullName = "First",
            email = "email@email.com",
            phoneNumber = "0123456789"
          )
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(
          controllers.contactDetails.routes.TraderAddressCorrectController.onLoad().url
        )
      }

      "return a SEE OTHER to Check Your Answers page response when correct data is sent and in checkMode" in {
        val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id").set(CheckModePage, true).success.value)
        lazy val result: Future[Result] = declarantContactDetailsController(userAnswers).onSubmit()(
          fakeRequestGenerator(
            fullName = "First",
            email = "email@email.com",
            phoneNumber = "0123456789"
          )
        )
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.cya.routes.CheckYourAnswersController.onLoad().url)
      }

      "update the UserAnswers in session" in {
        await(
          declarantContactDetailsController().onSubmit()(
            fakeRequestGenerator(
              fullName = "First",
              email = "email@email.com",
              phoneNumber = "0123456789"
            )
          )
        )
      }

    }

    "payload contains invalid data" should {

      "return BAD REQUEST when original amount is exceeded" in {
        val result: Future[Result] = declarantContactDetailsController().onSubmit()(
          fakeRequestGenerator(
            fullName = "",
            email = "",
            phoneNumber = ""
          )
        )
        status(result) mustBe Status.BAD_REQUEST
      }

      "return BAD REQUEST" in {
        val result: Future[Result] = declarantContactDetailsController().onSubmit()(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }

    }

  }

  "backLink" when {

    "one entry and not in change mode" should {
      "when loading page back button should take you to Trader address page" in {
        val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, false).success.value
              .set(NumberOfEntriesPage, OneEntry).success.value
          )

        implicit val req: DataRequest[AnyContentAsEmpty.type] = DataRequest(
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

        lazy val result: Call = declarantContactDetailsController(userAnswers).backLink()(req)
        result mustBe controllers.docUpload.routes.UploadAnotherFileController.onLoad()
      }
    }

    "bulk entry and not in change mode" should {
      "when loading page back button should take you to Trader address page" in {
        val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, false).success.value
              .set(NumberOfEntriesPage, MoreThanOneEntry).success.value
          )
        lazy val result: Call = declarantContactDetailsController(userAnswers).backLink()
        result mustBe controllers.reasons.routes.MoreInformationController.onLoad()
      }
    }

    "in change mode" should {
      "when loading page back button should take you to Check your answers page" in {
        val userAnswers: Option[UserAnswers] =
          Some(
            UserAnswers("some-cred-id")
              .set(CheckModePage, true).success.value
          )

        val req = DataRequest(
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

        lazy val result: Call = declarantContactDetailsController(userAnswers).backLink()(req)
        result mustBe controllers.cya.routes.CheckYourAnswersController.onLoad()
      }
    }

  }

}
