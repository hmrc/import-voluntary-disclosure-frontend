/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.importDetails

import controllers.IVDFrontendController
import controllers.actions._
import forms.importDetails.OneCustomsProcedureCodeFormProvider
import models.requests.DataRequest
import pages.importDetails.{EnterCustomsProcedureCodePage, OneCustomsProcedureCodePage}
import play.api.mvc._
import repositories.SessionRepository
import views.html.importDetails.OneCustomsProcedureCodeView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OneCustomsProcedureCodeController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: OneCustomsProcedureCodeFormProvider,
  view: OneCustomsProcedureCodeView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(OneCustomsProcedureCodePage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))

  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      oneCPCExists => {
        if (oneCPCExists) {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(OneCustomsProcedureCodePage, oneCPCExists))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad())
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(OneCustomsProcedureCodePage, oneCPCExists))
            updatedAnswers <- Future.fromTry(updatedAnswers.remove(EnterCustomsProcedureCodePage))
            _              <- sessionRepository.set(updatedAnswers)
          } yield {
            if (request.checkMode) {
              Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
            } else {
              Redirect(controllers.underpayments.routes.UnderpaymentStartController.onLoad())
            }
          }
        }
      }
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.importDetails.routes.AcceptanceDateController.onLoad()
    }
  }
}
