package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}

@Singleton
class RepresentativeNameController @Inject()(identity: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             sessionRepository: SessionRepository,
                                             mcc: MessagesControllerComponents
                                            ) extends FrontendController(mcc) with I18nSupport {


  def onLoad(): Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    ???
  }

  def onSubmit(): Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    ???
  }

}
