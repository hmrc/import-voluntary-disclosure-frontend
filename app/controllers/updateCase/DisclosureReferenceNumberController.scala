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

package controllers.updateCase

import controllers.IVDFrontendController
import controllers.actions._
import forms.updateCase.DisclosureReferenceNumberFormProvider
import models.requests.DataRequest
import pages.updateCase.DisclosureReferenceNumberPage
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import views.html.updateCase.DisclosureReferenceNumberView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DisclosureReferenceNumberController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: DisclosureReferenceNumberFormProvider,
  view: DisclosureReferenceNumberView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(DisclosureReferenceNumberPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      reference =>
        for {
          updatedAnswers <- Future.fromTry(
            request.userAnswers.set(DisclosureReferenceNumberPage, reference.toUpperCase)
          )
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          if (request.checkMode) {
            Redirect(controllers.updateCase.routes.UpdateCaseCheckYourAnswersController.onLoad())
          } else {
            Redirect(controllers.updateCase.routes.MoreDocumentationController.onLoad())
          }
        }
    )
  }

  private[controllers] def backLink(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.updateCase.routes.UpdateCaseCheckYourAnswersController.onLoad()
    } else {
      controllers.serviceEntry.routes.WhatDoYouWantToDoController.onLoad()
    }
  }

}
