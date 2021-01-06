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
import forms.UnderpaymentTypeFormProvider
import models.UserAnswers
import pages.UnderpaymentTypePage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UnderpaymentTypeView

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UnderpaymentTypeController @Inject()(identity: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           sessionRepository: SessionRepository,
                                           mcc: MessagesControllerComponents,
                                           underpaymentTypeView: UnderpaymentTypeView,
                                           formProvider: UnderpaymentTypeFormProvider
                                          )
  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identity andThen getData).async { implicit request =>
    Future.successful(
      Ok(underpaymentTypeView(formProvider.apply(), request.userAnswers.getOrElse(UserAnswers(request.credId))))
    )
  }

  def onSubmit: Action[AnyContent] = (identity andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))

    formProvider().bindFromRequest().fold(
      formWithErrors => {
        println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        formWithErrors.errors.foreach(println)
        Future.successful(BadRequest(underpaymentTypeView(formWithErrors, userAnswers)))
      },
      value => {
        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(UnderpaymentTypePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.routes.UnderpaymentTypeController.onLoad())
        }
      }
    )

  }

}
