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

package controllers.reasons

import controllers.actions._
import models.reasons.BoxNumber
import models.reasons.BoxNumber.BoxNumber
import pages.reasons.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.summary.ConfirmChangeReasonDetailSummaryList
import views.html.reasons.ConfirmChangeReasonDetailView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmChangeReasonDetailController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: ConfirmChangeReasonDetailView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with ConfirmChangeReasonDetailSummaryList
    with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val boxNumber = request.userAnswers.get(ChangeUnderpaymentReasonPage).fold(BoxNumber.Box22) { reason =>
      reason.original.boxNumber
    }
    val summary = buildSummaryList(request.userAnswers.get(ChangeUnderpaymentReasonPage), boxNumber)
    Future.successful(Ok(view(summary, pageTitle(boxNumber))))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(UnderpaymentReasonsPage) match {
      case Some(oldReasonList) =>
        request.userAnswers.get(ChangeUnderpaymentReasonPage) match {
          case Some(reason) =>
            val newReasonList =
              oldReasonList.filterNot(x =>
                x.boxNumber == reason.original.boxNumber && x.itemNumber == reason.original.itemNumber
              ) ++ Seq(reason.changed)
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentReasonsPage, newReasonList))
              updatedAnswers <- Future.fromTry(updatedAnswers.remove(ChangeUnderpaymentReasonPage))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad())
          case _ => Future.successful(InternalServerError("Changed underpayment reason not found"))
        }
      case _ => Future.successful(InternalServerError("Existing underpayment reasons not found"))
    }

  }

  private[controllers] def pageTitle(boxNumber: BoxNumber)(implicit messages: Messages): String =
    boxNumber match {
      case BoxNumber.OtherItem => messages("confirmChangeReason.otherReason.pageTitle")
      case _                   => messages("confirmChangeReason.pageTitle", boxNumber.id)
    }
}
