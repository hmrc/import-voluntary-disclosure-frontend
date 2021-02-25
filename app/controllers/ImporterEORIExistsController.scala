package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.{BoxNumberFormProvider, ImporterEORIExistsFormProvider}
import javax.inject.Inject
import pages.ImporterEORIExistsPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.BoxNumberView

import scala.concurrent.Future

@Singleton
class ImporterEORIExistsController @Inject()(identity: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             sessionRepository: SessionRepository,
                                             mcc: MessagesControllerComponents,
                                             formProvider: ImporterEORIExistsFormProvider
                                            )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(ImporterEORIExistsPage).fold(formProvider()) {
      formProvider().fill
    }

  }

  def onSubmit: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    ???

  }

  }
