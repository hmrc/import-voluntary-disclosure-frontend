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
import models.requests.DataRequest
import pages.importDetails.ImporterNamePage
import pages.underpayments.PostponedVatAccountingPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.underpayments.PostponedVatAccountingView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import pages.serviceEntry.KnownEoriDetailsPage

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
    getImporterName match {
      case Some(importerName) => Future.successful(Ok(view(formProvider(importerName), importerName, backLink())))
      case None               => Future.successful(errorHandler.showInternalServerError)
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getImporterName match {
      case Some(importerName) =>
        formProvider(importerName).bindFromRequest().fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, importerName, backLink()))),
          value => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PostponedVatAccountingPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield {
              if (value) {
                // TODO: redirect to Postponed VAT handoff
                Redirect(controllers.underpayments.routes.PostponedVatAccountingController.onLoad())
              } else if (request.isOneEntry) {
                Redirect(controllers.reasons.routes.BoxGuidanceController.onLoad())
              } else {
                Redirect(controllers.docUpload.routes.BulkUploadFileController.onLoad())
              }
            }
          }
        )

      case None => Future.successful(errorHandler.showInternalServerError)
    }
  }

  private[controllers] def getImporterName(implicit request: DataRequest[_]): Option[String] =
    if (request.isRepFlow) {
      request.userAnswers.get(ImporterNamePage)
    } else {
      request.userAnswers.get(KnownEoriDetailsPage).map(_.name)
    }

  private[controllers] def backLink(): Call =
    controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()

}
