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

package controllers

import config.AppConfig
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import forms.UserTypeFormProvider
import models.requests.OptionalDataRequest
import models.{UserAnswers, UserType}
import pages.{CheckModePage, KnownEoriDetailsPage, UserTypePage}
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UserTypeView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class UserTypeController @Inject()(identify: IdentifierAction,
                                   getData: DataRetrievalAction,
                                   sessionRepository: SessionRepository,
                                   mcc: MessagesControllerComponents,
                                   formProvider: UserTypeFormProvider,
                                   view: UserTypeView,
                                   appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  private[controllers] def backLink()(implicit request: OptionalDataRequest[AnyContent]): Call = {
    val cyaMode = {
      for {
        answers <- request.userAnswers
        mode <- answers.get(CheckModePage)
      } yield mode
    }.getOrElse(false)

    if (cyaMode) {
      controllers.routes.CheckYourAnswersController.onLoad()
    } else {
      if (appConfig.updateCaseEnabled) {
        controllers.routes.WhatDoYouWantToDoController.onLoad()
      } else {
        controllers.routes.ConfirmEORIDetailsController.onLoad()
      }
    }
  }

  val onLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>

    val form = for {
      userAnswers <- request.userAnswers
      data <- userAnswers.get(UserTypePage)
    } yield {
      formProvider().fill(data)
    }

    Future.successful(Ok(view(form.getOrElse(formProvider()), backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      newUserType => {
        val prevUserType = userAnswers.get(UserTypePage).getOrElse(newUserType)

        val cleanedUserAnswers = if (prevUserType != newUserType) {
          userAnswers.preserve(Seq(KnownEoriDetailsPage))
        } else {
          userAnswers
        }

        for {
          updatedAnswers <- Future.fromTry(cleanedUserAnswers.set(UserTypePage, newUserType))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          val checkMode = updatedAnswers.get(CheckModePage).getOrElse(false)
          if (prevUserType == newUserType && checkMode) {
            Redirect(controllers.routes.CheckYourAnswersController.onLoad())
          } else {
            newUserType match {
              case UserType.Importer => Redirect(controllers.routes.NumberOfEntriesController.onLoad())
              case UserType.Representative => Redirect(controllers.routes.ImporterNameController.onLoad())
            }
          }
        }
      }
    )
  }

}
