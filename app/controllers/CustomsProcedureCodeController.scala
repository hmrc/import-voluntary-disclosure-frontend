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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

class CustomsProcedureCodeController @Inject()(identify: IdentifierAction,
                                               getData: DataRetrievalAction,
                                               requireData: DataRequiredAction,
                                               sessionRepository: SessionRepository,
                                               mcc: MessagesControllerComponents)

  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    //    val form = request.userAnswers.get(AcceptanceDatePage).fold(formProvider()) {
    //      formProvider().fill
    //    }
    //
    //    Future.successful(Ok(view(form)))
    ???
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    //    formProvider().bindFromRequest().fold(
    //      formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
    //      value => {
    //        for {
    //          updatedAnswers <- Future.fromTry(request.userAnswers.set(AcceptanceDatePage, value))
    //          _ <- sessionRepository.set(updatedAnswers)
    //        } yield {
    //          Redirect(controllers.routes.UnderpaymentTypeController.onLoad())
    //        }
    //      }
    //    )
    ???
  }

}
