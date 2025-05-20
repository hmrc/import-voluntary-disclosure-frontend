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
import forms.reasons.UnderpaymentReasonSummaryFormProvider
import pages.reasons.UnderpaymentReasonsPage
import play.api.mvc._
import viewmodels.summary.UnderpaymentReasonSummaryList
import views.html.reasons.UnderpaymentReasonSummaryView

import javax.inject.Inject
import scala.concurrent.Future

class UnderpaymentReasonSummaryController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  mcc: MessagesControllerComponents,
  view: UnderpaymentReasonSummaryView,
  formProvider: UnderpaymentReasonSummaryFormProvider
) extends IVDFrontendController(mcc)
    with UnderpaymentReasonSummaryList {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    buildSummaryList(request.userAnswers.get(UnderpaymentReasonsPage)) match {
      case Some(value) => Future.successful(Ok(view(formProvider.apply(), Some(value))))
      case None        => Future.successful(InternalServerError("Couldn't find Underpayment reasons"))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors =>
        Future.successful(
          BadRequest(
            view(
              formWithErrors,
              buildSummaryList(request.userAnswers.get(UnderpaymentReasonsPage))
            )
          )
        ),
      anotherReason => {
        if (anotherReason) {
          Future.successful(Redirect(controllers.reasons.routes.BoxNumberController.onLoad()))
        } else {
          if (request.checkMode) {
            Future.successful(Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad()))
          } else {
            Future.successful(Redirect(controllers.docUpload.routes.SupportingDocController.onLoad()))
          }
        }
      }
    )
  }

}
