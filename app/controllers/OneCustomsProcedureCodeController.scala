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
import forms.OneCustomsProcedureCodeFormProvider
import pages.OneCustomsProcedureCodePage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.OneCustomsProcedureCodeView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class OneCustomsProcedureCodeController @Inject()(identify: IdentifierAction,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  sessionRepository: SessionRepository,
                                                  mcc: MessagesControllerComponents,
                                                  formProvider: OneCustomsProcedureCodeFormProvider,
                                                  view: OneCustomsProcedureCodeView
                                                 )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(OneCustomsProcedureCodePage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form)))

  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
      oneCPC => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(OneCustomsProcedureCodePage, oneCPC))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          if (oneCPC) {
            Redirect(controllers.routes.EnterCustomsProcedureCodeController.onLoad())
          }
          else {
            Redirect(controllers.underpayments.routes.UnderpaymentStartController.onLoad())
          }
        }
      }
    )

  }

}
