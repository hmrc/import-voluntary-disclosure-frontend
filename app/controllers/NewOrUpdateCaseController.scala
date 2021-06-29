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
import forms.NewOrUpdateCaseFormProvider
import models.UserAnswers
import models.requests.DataRequest
import pages.{KnownEoriDetailsPage, NewOrUpdateCasePage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.NewOrUpdateCaseView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NewOrUpdateCaseController @Inject()(identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          sessionRepository: SessionRepository,
                                          mcc: MessagesControllerComponents,
                                          formProvider: NewOrUpdateCaseFormProvider,
                                          view: NewOrUpdateCaseView) extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(NewOrUpdateCasePage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      submittedValue => {
        val currentValue = request.userAnswers.get(NewOrUpdateCasePage).getOrElse(false)
        if (currentValue == submittedValue) {
          for {
            userAnswers <- Future.fromTry(request.userAnswers.set(NewOrUpdateCasePage, submittedValue))
            _ <- sessionRepository.set(userAnswers)
          } yield {
            submitRedirect(submittedValue)
          }
        } else {
          val cleanedUserAnswers: UserAnswers = request.userAnswers.preserve(Seq(KnownEoriDetailsPage, NewOrUpdateCasePage))
          for {
            userAnswers <- Future.fromTry(cleanedUserAnswers.set(NewOrUpdateCasePage, submittedValue))
            _ <- sessionRepository.set(userAnswers)
          } yield {
            submitRedirect(submittedValue)
          }
        }
      }
    )
  }

  private[controllers] def submitRedirect(submittedValue: Boolean): Result = {
    if (submittedValue) {
      Redirect(controllers.routes.UserTypeController.onLoad())
    } else {
      Redirect(controllers.routes.NewOrUpdateCaseController.onLoad()) // TODO - change once next page is in the flow
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    controllers.routes.ConfirmEORIDetailsController.onLoad()
  }

}
