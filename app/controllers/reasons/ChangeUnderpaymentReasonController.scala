/*
 * Copyright 2023 HM Revenue & Customs
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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, ChangeUnderpaymentReason}
import pages.reasons.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.summary.ChangeUnderpaymentReasonSummaryList
import views.html.reasons.ChangeUnderpaymentReasonView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChangeUnderpaymentReasonController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: ChangeUnderpaymentReasonView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with ChangeUnderpaymentReasonSummaryList
    with I18nSupport {

  private lazy val backLink: Call = controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad()

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(ChangeUnderpaymentReasonPage) match {
      case Some(reason) =>
        val boxNumber = reason.original.boxNumber
        Future.successful(
          Ok(view(backLink, buildSummaryList(reason.original), pageTitle(boxNumber)))
        )
      case _ => Future.successful(InternalServerError("No change underpayment reasons found"))
    }
  }

  def change(boxNumber: Int, itemNumber: Int): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      request.userAnswers.get(UnderpaymentReasonsPage) match {
        case Some(reasons) =>
          val originalReason = reasons.filter(x => x.boxNumber.id == boxNumber && x.itemNumber == itemNumber).head
          for {
            updatedAnswers <- Future.fromTry(
              request.userAnswers.set(
                ChangeUnderpaymentReasonPage,
                ChangeUnderpaymentReason(originalReason, originalReason)
              )
            )
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad())
        case _ => Future.successful(InternalServerError("No underpayment reason list found"))
      }
    }

  private[controllers] def pageTitle(boxNumber: BoxNumber)(implicit messages: Messages): String =
    boxNumber match {
      case BoxNumber.OtherItem => messages("changeUnderpaymentReason.otherReasonTitle")
      case _                   => messages("changeUnderpaymentReason.pageTitle", boxNumber.id)
    }
}
