package controllers

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.BoxNumberFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import views.html.BoxNumberView

import scala.concurrent.Future

class BoxNumberControllerSpec extends ControllerSpecBase {

  trait Test extends MockSessionRepository {
    lazy val controller = new BoxNumberController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      boxNumberView
    )
    private lazy val boxNumberView = app.injector.instanceOf[BoxNumberView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))
    val formProvider: BoxNumberFormProvider = injector.instanceOf[BoxNumberFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: BoxNumberFormProvider = formProvider
  }


}
