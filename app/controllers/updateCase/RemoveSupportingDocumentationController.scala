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

package controllers.updateCase

import controllers.IVDFrontendController
import controllers.actions._
import forms.shared.RemoveUploadedFileFormProvider
import models.Index
import pages.updateCase.{RemoveSupportingDocumentationPage, UploadSupportingDocumentationPage}
import play.api.i18n.MessagesApi
import play.api.mvc._
import repositories.SessionRepository
import views.html.shared.RemoveUploadedFileView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveSupportingDocumentationController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: RemoveUploadedFileFormProvider,
  mcc: MessagesControllerComponents,
  view: RemoveUploadedFileView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      request.userAnswers.get(UploadSupportingDocumentationPage) match {
        case Some(files) if files.isDefinedAt(index.position) =>
          Future.successful(
            Ok(view(formProvider(), index, files(index.position).fileName, backlink(), submitLink(index)))
          )
        case _ =>
          Future.successful(Redirect(controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad()))
      }
  }

  def onSubmit(index: Index): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          request.userAnswers.get(UploadSupportingDocumentationPage) match {
            case Some(files) if files.isDefinedAt(index.position) =>
              Future.successful(
                BadRequest(view(formWithErrors, index, files(index.position).fileName, backlink(), submitLink(index)))
              )
            case _ => Future.successful(InternalServerError("File Upload list unavailable."))
          },
        value => {
          if (value) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.remove(RemoveSupportingDocumentationPage(index)))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(controllers.updateCase.routes.UploadSupportingDocumentationSummaryController.onLoad())
          } else {
            Future.successful(
              Redirect(controllers.updateCase.routes.UploadSupportingDocumentationSummaryController.onLoad())
            )
          }
        }
      )
  }

  private[controllers] def backlink() =
    controllers.updateCase.routes.UploadSupportingDocumentationSummaryController.onLoad()

  private[controllers] def submitLink(index: Index) =
    controllers.updateCase.routes.RemoveSupportingDocumentationController.onSubmit(index)
}
