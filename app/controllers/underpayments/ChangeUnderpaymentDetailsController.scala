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
import models.underpayments.UnderpaymentAmount
import pages.underpayments.{UnderpaymentDetailSummaryPage, UnderpaymentDetailsPage}
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.underpayments.ChangeUnderpaymentDetailsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChangeUnderpaymentDetailsController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    sessionRepository: SessionRepository,
                                                    mcc: MessagesControllerComponents,
                                                    formProvider: UnderpaymentDetailsFormProvider,
                                                    view: ChangeUnderpaymentDetailsView,
                                                    implicit val ec: ExecutionContext)

  extends FrontendController(mcc) with I18nSupport {

  def onLoad(underpaymentType: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val summaryPageChange = request.userAnswers.get(UnderpaymentDetailSummaryPage) match {
      case Some(underpayments) => !underpayments.filter(underpayment => underpayment.duty == underpaymentType).isEmpty
      case None => false
    }

    val form = request.userAnswers.get(UnderpaymentDetailsPage) match {
      case Some(details) => formProvider().fill(details)
      case None =>
        request.userAnswers.get(UnderpaymentDetailSummaryPage) match {
          case Some(value) => {
            val underpayment = value.filter(underpayment => underpayment.duty == underpaymentType).head
            formProvider().fill(UnderpaymentAmount(underpayment.original, underpayment.amended))
          }
          case None => formProvider()
        }
    }

    Future.successful(Ok(view(form, underpaymentType, backLink(underpaymentType, summaryPageChange), summaryPageChange, request.isOneEntry)))
  }

  def onSubmit(underpaymentType: String): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val summaryPageChange = request.userAnswers.get(UnderpaymentDetailSummaryPage) match {
        case Some(underpayments) => !underpayments.filter(underpayment => underpayment.duty == underpaymentType).isEmpty
        case None => false
      }

      formProvider().bindFromRequest().fold(
        formWithErrors => {
          val newErrors = formWithErrors.errors.map { error =>
            if (error.key.isEmpty) {
              FormError("amended", error.message)
            } else {
              error
            }
          }
          val form = formWithErrors.copy(errors = newErrors)
          Future.successful(BadRequest(view(form, underpaymentType, backLink(underpaymentType, summaryPageChange), summaryPageChange, request.isOneEntry)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(
              UnderpaymentDetailsPage, UnderpaymentAmount(value.original, value.amended))
            )
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.underpayments.routes.UnderpaymentDetailConfirmController.onLoad(
              underpaymentType = underpaymentType,
              change = summaryPageChange)
            )
          }
        }
      )
  }

  private[controllers] def backLink(underpaymentType: String, summaryPageChange: Boolean) = {
    if (summaryPageChange) {
      controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
    } else {
      controllers.underpayments.routes.UnderpaymentDetailConfirmController.onLoad(underpaymentType, summaryPageChange)
    }
  }
}
