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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.RepresentativeDanOneFormProvider
import models.UserAnswers
import pages.{RepresentativeDanOnePage, SplitPaymentPage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.RepresentativeDanOneView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RepresentativeDanOneController @Inject()(identify: IdentifierAction,
                                               getData: DataRetrievalAction,
                                               requireData: DataRequiredAction,
                                               sessionRepository: SessionRepository,
                                               mcc: MessagesControllerComponents,
                                               view: RepresentativeDanOneView,
                                               formProvider: RepresentativeDanOneFormProvider
                                              )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(RepresentativeDanOnePage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink(request.userAnswers))))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,
        backLink(request.userAnswers)
      ))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(RepresentativeDanOnePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          Redirect(controllers.underpayments.routes.UnderpaymentStartController.onLoad())
        }
      }
    )
  }

  private[controllers] def backLink(userAnswers: UserAnswers): Call =
    if (userAnswers.get(SplitPaymentPage).isDefined) {
      controllers.routes.SplitPaymentController.onLoad()
    } else {
      controllers.routes.DefermentController.onLoad()
    }

}
