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

package controllers.serviceEntry

import controllers.IVDFrontendController
import controllers.actions._
import forms.serviceEntry.WhatDoYouWantToDoFormProvider
import models.SubmissionType.{CancelCase, CreateCase, UpdateCase}
import models.{SubmissionType, UserAnswers}
import pages.serviceEntry.{KnownEoriDetailsPage, SubmissionTypePage, WhatDoYouWantToDoPage}
import play.api.mvc._
import repositories.SessionRepository
import views.html.serviceEntry.WhatDoYouWantToDoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhatDoYouWantToDoController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: WhatDoYouWantToDoFormProvider,
  view: WhatDoYouWantToDoView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(WhatDoYouWantToDoPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink())))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink()))),
      userCase => {
        val currentValue = request.userAnswers.get(WhatDoYouWantToDoPage).getOrElse(userCase)
        if (currentValue == userCase) {
          for {
            userAnswers <- Future.fromTry(request.userAnswers.set(WhatDoYouWantToDoPage, userCase))
            updatedUserAnswers <- Future.fromTry(userAnswers.remove(SubmissionTypePage))
            _           <- sessionRepository.set(updatedUserAnswers)
          } yield submitRedirect(userCase)
        } else {
          val cleanedUserAnswers: UserAnswers = request.userAnswers.preserve(Seq(KnownEoriDetailsPage))
          for {
            userAnswers <- Future.fromTry(cleanedUserAnswers.set(WhatDoYouWantToDoPage, userCase))
            _           <- sessionRepository.set(userAnswers)
          } yield submitRedirect(userCase)
        }
      }
    )
  }

  private[serviceEntry] def submitRedirect(submittedValue: SubmissionType): Result = {
    submittedValue match {
      case CreateCase => Redirect(controllers.importDetails.routes.UserTypeController.onLoad())
      case UpdateCase => Redirect(controllers.updateCase.routes.DisclosureReferenceNumberController.onLoad())
      case CancelCase => Redirect(controllers.cancelCase.routes.CancelCaseReferenceNumberController.onLoad())
    }
  }

  private[serviceEntry] def backLink(): Call =
    controllers.serviceEntry.routes.ConfirmEORIDetailsController.onLoad()

}
