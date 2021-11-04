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

package controllers.paymentInfo

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.paymentInfo.SplitPaymentFormProvider
import models.requests.DataRequest
import pages._
import pages.paymentInfo._
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.paymentInfo.SplitPaymentView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SplitPaymentController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: SplitPaymentFormProvider,
  view: SplitPaymentView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(SplitPaymentPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors =>
        Future.successful(
          BadRequest(
            view(formWithErrors, backLink)
          )
        ),
      splitPayment => {
        val previousSplitPayment = request.userAnswers.get(SplitPaymentPage).getOrElse(splitPayment)
        if (splitPayment != previousSplitPayment) {
          val userAnswers = request.userAnswers.removeMany(
            Seq(
              DefermentTypePage,
              DefermentAccountPage,
              AdditionalDefermentTypePage,
              AdditionalDefermentNumberPage,
              UploadAuthorityPage
            )
          )
          for {
            otherUpdatedAnswers <- Future.successful(userAnswers)
            checkMode           <- Future.fromTry(otherUpdatedAnswers.set(CheckModePage, false))
            updatedAnswers      <- Future.fromTry(checkMode.set(SplitPaymentPage, splitPayment))
            _                   <- sessionRepository.set(updatedAnswers)
          } yield redirectTo(splitPayment)
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SplitPaymentPage, splitPayment))
            _              <- sessionRepository.set(updatedAnswers)
          } yield {
            if (request.checkMode) {
              Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
            } else {
              redirectTo(splitPayment)
            }
          }
        }
      }
    )
  }

  def redirectTo(value: Boolean): Result = {
    if (value) {
      Redirect(controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad())
    } else {
      Redirect(controllers.paymentInfo.routes.RepresentativeDanController.onLoad())
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.paymentInfo.routes.DefermentController.onLoad()
    }
  }

}
