package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.RepresentativeNameFormProvider
import pages.RepresentativeNamePage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RepresentativeNameController @Inject()(identity: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             sessionRepository: SessionRepository,
                                             mcc: MessagesControllerComponents,
                                             formProvider: RepresentativeNameFormProvider
                                            ) extends FrontendController(mcc) with I18nSupport {


  def onLoad(): Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(RepresentativeNamePage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit(): Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    ???
  }

}
