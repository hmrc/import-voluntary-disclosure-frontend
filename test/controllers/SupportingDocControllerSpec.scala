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

import java.time.LocalDateTime
import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.repositories.MockSessionRepository
import models.{FileUploadInfo, UserAnswers}
import pages.{FileUploadPage, HasFurtherInformationPage}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import views.html.SupportingDocView

import scala.concurrent.Future

class SupportingDocControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {

    lazy val controller = new SupportingDocController(authenticatedAction, dataRetrievalAction,
      messagesControllerComponents, dataRequiredAction, view, appConfig)
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val view = injector.instanceOf[SupportingDocView]
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
  }

  "GET onLoad" should {
    "return 200" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML" in new Test {
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "redirect to the summary page when uploaded file already exists" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(FileUploadPage, Seq(FileUploadInfo("file-ref-1", "test.pdf", "downloadUrl", LocalDateTime.now(), "checksum", "fileMimeType"))).success.value
      )
      val result: Future[Result] = controller.onLoad()(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.UploadAnotherFileController.onLoad().url)

    }
  }

  "Back link" should {
    "return to Has further information page if further information not required" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(HasFurtherInformationPage, false).success.value
      )

      controller.backLink(userAnswers.get) mustBe controllers.routes.HasFurtherInformationController.onLoad()
    }

    "return to More information page if further information is required" in new Test {
      override val userAnswers: Option[UserAnswers] = Some(
        UserAnswers("some-cred-id")
          .set(HasFurtherInformationPage, true).success.value
      )

      controller.backLink(userAnswers.get) mustBe controllers.routes.MoreInformationController.onLoad()
    }
  }
}
