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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.WhatDoYouWantToDoFormProvider
import models.UserAnswers
import pages.{KnownEoriDetailsPage, WhatDoYouWantToDoPage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.WhatDoYouWantToDoView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhatDoYouWantToDoController @Inject()(identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            sessionRepository: SessionRepository,
                                            mcc: MessagesControllerComponents,
                                            formProvider: WhatDoYouWantToDoFormProvider,
                                            view: WhatDoYouWantToDoView) extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(WhatDoYouWantToDoPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      isCreateCase => {
        val currentValue = request.userAnswers.get(WhatDoYouWantToDoPage).getOrElse(false)
        if (currentValue == isCreateCase) {
          for {
            userAnswers <- Future.fromTry(request.userAnswers.set(WhatDoYouWantToDoPage, isCreateCase))
            _ <- sessionRepository.set(userAnswers)
          } yield {
            submitRedirect(isCreateCase)
          }
        } else {
          val cleanedUserAnswers: UserAnswers = request.userAnswers.preserve(Seq(KnownEoriDetailsPage))
          for {
            userAnswers <- Future.fromTry(cleanedUserAnswers.set(WhatDoYouWantToDoPage, isCreateCase))
            _ <- sessionRepository.set(userAnswers)
          } yield {
            submitRedirect(isCreateCase)
          }
        }
      }
    )
  }

  private[controllers] def submitRedirect(submittedValue: Boolean): Result = {
    if (submittedValue) {
      Redirect(controllers.routes.UserTypeController.onLoad())
    } else {
      Redirect(controllers.routes.DisclosureReferenceNumberController.onLoad())
    }
  }

  private[controllers] def backLink(): Call = {
    controllers.routes.ConfirmEORIDetailsController.onLoad()
  }

}
