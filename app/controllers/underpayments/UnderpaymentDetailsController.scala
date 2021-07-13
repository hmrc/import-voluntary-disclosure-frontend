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

package controllers.underpayments

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.underpayments.UnderpaymentDetailsFormProvider
import pages.underpayments.UnderpaymentDetailsPage
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.underpayments.UnderpaymentDetailsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UnderpaymentDetailsController @Inject()(identify: IdentifierAction,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              sessionRepository: SessionRepository,
                                              mcc: MessagesControllerComponents,
                                              formProvider: UnderpaymentDetailsFormProvider,
                                              view: UnderpaymentDetailsView,
                                              implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink = controllers.underpayments.routes.UnderpaymentTypeController.onLoad()

  def onLoad(underpaymentType: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(UnderpaymentDetailsPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, underpaymentType, backLink, request.isOneEntry)))
  }

  def onSubmit(underpaymentType: String): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => {
          val newErrors = formWithErrors.errors.map { error =>
            if (error.key.isEmpty) {
              FormError("amended", error.message)
            } else {
              error
            }
          }
          Future.successful(BadRequest(view(formWithErrors.copy(errors = newErrors), underpaymentType, backLink, request.isOneEntry)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentDetailsPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.underpayments.routes.UnderpaymentDetailConfirmController.onLoad(underpaymentType, change = false))
          }
        }
      )
  }

}
