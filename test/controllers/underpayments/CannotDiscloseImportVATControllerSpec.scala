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

package controllers.underpayments

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.{ContactAddress, EoriDetails, UserAnswers}
import models.importDetails.UserType.{Importer, Representative}
import pages.importDetails.{ImporterNamePage, UserTypePage}
import pages.serviceEntry.KnownEoriDetailsPage
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.reasons.CannotDiscloseImportVATView

import scala.concurrent.Future

class CannotDiscloseImportVATControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {

    MockedSessionRepository.remove(Future.successful("OK"))

    val view = injector.instanceOf[CannotDiscloseImportVATView]

    lazy val controller = new CannotDiscloseImportVATController(
      authenticatedAction,
      dataRetrievalAction,
      messagesControllerComponents,
      dataRequiredAction,
      mockSessionRepository,
      errorHandler,
      view,
      ec
    )

    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("some-cred-id").set(DisclosureReferenceNumberPage, "C182107152124AQYVM6E34").success.value
    )

  }

  "GET onLoad" should {
    "return 200 for representative" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UserTypePage, Representative).success.value
          .set(ImporterNamePage, "Some Name").success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return 200 for importer" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(UserTypePage, Importer).success.value
          .set(
            KnownEoriDetailsPage,
            EoriDetails("1234567890", "name", ContactAddress("line1", None, "City", Some("CC"), ""), Some(""))
          ).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

  }

}
