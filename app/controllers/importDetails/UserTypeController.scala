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

package controllers.importDetails

import controllers.IVDFrontendController
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import forms.importDetails.UserTypeFormProvider
import models.UserAnswers
import models.importDetails.UserType
import models.requests.OptionalDataRequest
import pages.CheckModePage
import pages.importDetails.UserTypePage
import pages.serviceEntry.{KnownEoriDetailsPage, SubmissionTypePage}
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import views.html.importDetails.UserTypeView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserTypeController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: UserTypeFormProvider,
  view: UserTypeView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val form = for {
      userAnswers <- request.userAnswers
      data        <- userAnswers.get(UserTypePage)
    } yield formProvider().fill(data)

    Future.successful(Ok(view(form.getOrElse(formProvider()), backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      newUserType => {
        val prevUserType = userAnswers.get(UserTypePage).getOrElse(newUserType)

        val cleanedUserAnswers = if (prevUserType != newUserType) {
          userAnswers.preserve(Seq(KnownEoriDetailsPage, SubmissionTypePage))
        } else {
          userAnswers
        }

        for {
          updatedAnswers <- Future.fromTry(cleanedUserAnswers.set(UserTypePage, newUserType))
          _              <- sessionRepository.set(updatedAnswers)
        } yield {
          val checkMode = updatedAnswers.get(CheckModePage).getOrElse(false)
          if (prevUserType == newUserType && checkMode) {
            Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
          } else {
            newUserType match {
              case UserType.Importer => Redirect(controllers.importDetails.routes.NumberOfEntriesController.onLoad())
              case UserType.Representative => Redirect(controllers.importDetails.routes.ImporterNameController.onLoad())
            }
          }
        }
      }
    )
  }

  private[controllers] def backLink(implicit request: OptionalDataRequest[AnyContent]): Call = {
    val cyaMode = {
      for {
        answers <- request.userAnswers
        mode    <- answers.get(CheckModePage)
      } yield mode
    }.getOrElse(false)

    if (cyaMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.serviceEntry.routes.WhatDoYouWantToDoController.onLoad()
    }
  }

}
