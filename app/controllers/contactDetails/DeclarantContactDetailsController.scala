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

package controllers.contactDetails

import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.contactDetails.DeclarantContactDetailsFormProvider
import models.requests.DataRequest
import pages.contactDetails.DeclarantContactDetailsPage
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.contactDetails.DeclarantContactDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarantContactDetailsController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: DeclarantContactDetailsFormProvider,
  view: DeclarantContactDetailsView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(DeclarantContactDetailsPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink())))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink()))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(DeclarantContactDetailsPage, value))
          _              <- sessionRepository.set(updatedAnswers)
        } yield {
          if (request.checkMode) {
            Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
          } else {
            Redirect(controllers.contactDetails.routes.TraderAddressCorrectController.onLoad())
          }
        }
      }
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      if (request.isOneEntry) {
        controllers.docUpload.routes.UploadAnotherFileController.onLoad()
      } else {
        controllers.reasons.routes.MoreInformationController.onLoad()
      }
    }
  }

}
