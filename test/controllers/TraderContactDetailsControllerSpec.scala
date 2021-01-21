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
import forms.TraderContactDetailsFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import views.html.TraderContactDetailsView

import scala.concurrent.Future

class TraderContactDetailsControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    lazy val controller = new TraderContactDetailsController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      traderContactDetailsView
    )
    private lazy val traderContactDetailsView = app.injector.instanceOf[TraderContactDetailsView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: TraderContactDetailsFormProvider = injector.instanceOf[TraderContactDetailsFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: TraderContactDetailsFormProvider = formProvider
  }

}
