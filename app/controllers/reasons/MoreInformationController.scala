/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.reasons

import controllers.IVDFrontendController
import controllers.actions._
import forms.reasons.MoreInformationFormProvider
import models.requests.DataRequest
import pages.reasons.MoreInformationPage
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import views.html.reasons.MoreInformationView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MoreInformationController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: MoreInformationFormProvider,
  view: MoreInformationView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val isOneEntry = request.isOneEntry
    val form = request.userAnswers.get(MoreInformationPage).fold(formProvider(isOneEntry)) {
      formProvider(isOneEntry).fill
    }
    Future.successful(Ok(view(form, backLink, isOneEntry)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val isOneEntry = request.isOneEntry
    formProvider(isOneEntry).bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink, isOneEntry))),
      moreInfo => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(MoreInformationPage, moreInfo))
          _              <- sessionRepository.set(updatedAnswers)
        } yield {
          if (request.checkMode) {
            Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
          } else {
            if (request.isOneEntry) {
              Redirect(controllers.docUpload.routes.SupportingDocController.onLoad())
            } else {
              Redirect(controllers.contactDetails.routes.DeclarantContactDetailsController.onLoad())
            }
          }
        }
      }
    )
  }

  private[controllers] def backLink(implicit request: DataRequest[_]): Option[Call] = {
    if (request.checkMode) {
      None
    } else {
      Some(controllers.docUpload.routes.BulkUploadFileController.onLoad())
    }
  }

}
