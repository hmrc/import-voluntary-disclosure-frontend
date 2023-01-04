/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.docUpload

import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.docUpload.OptionalSupportingDocsFormProvider
import pages.docUpload.OptionalSupportingDocsPage
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.docUpload.OptionalSupportingDocsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OptionalSupportingDocsController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: OptionalSupportingDocsView,
  formProvider: OptionalSupportingDocsFormProvider,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  private lazy val backLink = controllers.docUpload.routes.AnyOtherSupportingDocsController.onLoad()

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val documentsSelected = request.userAnswers.get(OptionalSupportingDocsPage).getOrElse(Seq.empty)
    Future.successful(Ok(view(formProvider.apply(), backLink, documentsSelected)))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        val form = formWithErrors.discardingErrors.withError(
          "importAndEntry",
          "optionalSupportingDocuments.error.required",
          Seq.empty
        )
        Future.successful(BadRequest(view(form, backLink, Seq.empty)))
      },
      value =>
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(OptionalSupportingDocsPage, value))
          _              <- sessionRepository.set(updatedAnswers)
        } yield Redirect(controllers.docUpload.routes.UploadFileController.onLoad())
    )
  }

}
