package controllers

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.OptionalSupportingDocsFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import views.html.OptionalSupportingDocsView

import scala.concurrent.Future

class OptionalSupportingDocsControllerSpec extends ControllerSpecBase {


  trait Test extends MockSessionRepository {
    private lazy val optionalSupportingDocsView: OptionalSupportingDocsView = app.injector.instanceOf[OptionalSupportingDocsView]

    val userAnswers: Option[UserAnswers] = Some(UserAnswers("credId"))
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)

    val formProvider: OptionalSupportingDocsFormProvider = injector.instanceOf[OptionalSupportingDocsFormProvider]
    val form: OptionalSupportingDocsFormProvider = formProvider

    MockedSessionRepository.set(Future.successful(true))

    lazy val controller = new OptionalSupportingDocsController(authenticatedAction, dataRetrievalAction, dataRequiredAction,
      mockSessionRepository, messagesControllerComponents, optionalSupportingDocsView, form)
  }



}
