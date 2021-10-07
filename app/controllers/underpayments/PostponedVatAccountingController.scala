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

package controllers.underpayments

import config.ErrorHandler
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.underpayments.PostponedVatAccountingFormProvider
import pages.underpayments.PostponedVatAccountingPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.underpayments.PostponedVatAccountingView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PostponedVatAccountingController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: PostponedVatAccountingFormProvider,
  view: PostponedVatAccountingView,
  errorHandler: ErrorHandler,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.getImporterName match {
      case Some(importerName) =>
        val form = request.userAnswers.get(PostponedVatAccountingPage)
          .fold(formProvider(importerName))(formProvider(importerName).fill)
        Future.successful(Ok(view(form, importerName, backLink())))
      case None => Future.successful(errorHandler.showInternalServerError)
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.getImporterName match {
      case Some(importerName) =>
        formProvider(importerName).bindFromRequest().fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, importerName, backLink()))),
          value => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PostponedVatAccountingPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield {
              if (value) {
                Redirect(controllers.underpayments.routes.PVAHandoffController.onLoad())
              } else {
                Redirect(controllers.reasons.routes.BoxGuidanceController.onLoad())
              }
            }
          }
        )

      case None => Future.successful(errorHandler.showInternalServerError)
    }
  }

  private[controllers] def backLink(): Call =
    controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()

}
