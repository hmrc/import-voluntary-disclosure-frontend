/*
 * Copyright 2022 HM Revenue & Customs
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
import models.underpayments.{UnderpaymentAmount, UnderpaymentDetail}
import pages.underpayments.{UnderpaymentDetailSummaryPage, UnderpaymentDetailsPage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.summary.UnderpaymentDetailConfirmSummaryList
import views.html.underpayments.UnderpaymentDetailConfirmView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnderpaymentDetailConfirmController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: UnderpaymentDetailConfirmView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with UnderpaymentDetailConfirmSummaryList
    with I18nSupport {

  def onLoad(underpaymentType: String, change: Boolean): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val underpaymentDetail = request.userAnswers.get(UnderpaymentDetailsPage).getOrElse(UnderpaymentAmount(0, 0))
      Future.successful(
        Ok(
          view(
            underpaymentType,
            buildSummaryList(underpaymentType, underpaymentDetail),
            submitCall(underpaymentType, change)
          )
        )
      )
    }

  def onSubmit(underpaymentType: String, change: Boolean): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      request.userAnswers.get(UnderpaymentDetailsPage) match {
        case Some(value) =>
          val newUnderpaymentDetail    = Seq(UnderpaymentDetail(underpaymentType, value.original, value.amended))
          val currentUnderpaymentTypes = request.userAnswers.get(UnderpaymentDetailSummaryPage).getOrElse(Seq.empty)
          if (change) {
            val updatedUnderpaymentTypes = currentUnderpaymentTypes.map(underpayment =>
              if (underpayment.duty == underpaymentType) {
                underpayment.copy(original = value.original, amended = value.amended)
              } else {
                underpayment
              }
            )
            for {
              updatedAnswers <- Future.fromTry(
                request.userAnswers.set(UnderpaymentDetailSummaryPage, updatedUnderpaymentTypes)
              )
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad())
          } else {
            for {
              updatedAnswers <- Future.fromTry(
                request.userAnswers.set(
                  UnderpaymentDetailSummaryPage,
                  newUnderpaymentDetail ++ currentUnderpaymentTypes
                )
              )
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad())
          }
        case None => Future.successful(InternalServerError("Couldn't find underpayment details"))
      }
    }

  private def submitCall(underpaymentType: String, change: Boolean): Call = {
    if (change) {
      controllers.underpayments.routes.UnderpaymentDetailConfirmController.onSubmit(underpaymentType, change = true)
    } else {
      controllers.underpayments.routes.UnderpaymentDetailConfirmController.onSubmit(underpaymentType, change = false)
    }
  }

}
