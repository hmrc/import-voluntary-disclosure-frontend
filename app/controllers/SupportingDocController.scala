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

import config.AppConfig
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.SupportingDocView

import scala.concurrent.Future

@Singleton
class SupportingDocController @Inject()(identity: IdentifierAction,
                                     getData: DataRetrievalAction,
                                     mcc: MessagesControllerComponents,
                                     view: SupportingDocView)
  extends FrontendController(mcc) with I18nSupport {


  val onLoad: Action[AnyContent] = (identity andThen getData).async { implicit request =>
    Future.successful(Ok(view()))
  }

  def onSubmit: Action[AnyContent] = (identity andThen getData).async { implicit request =>

    //Will redirct to Upscan page once complete
    Future.successful(Redirect(controllers.routes.SupportingDocController.onLoad()))
  }

}
