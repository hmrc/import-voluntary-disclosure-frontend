package controllers

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}

@Singleton
class BoxNumberController @Inject()(identity: IdentifierAction,
                                    getData: DataRetrievalAction,
                                    mcc: MessagesControllerComponents,
                                    requireData: DataRequiredAction,
                                    view: ???,
                                    implicit val appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {


  def onLoad: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    ???
  }

  def onSubmit: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    ???
  }

}
