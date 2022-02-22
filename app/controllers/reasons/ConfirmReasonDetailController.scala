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
import models.reasons._
import pages.reasons._
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.summary.ConfirmReasonDetailSummaryList
import views.html.reasons.ConfirmReasonDetailView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmReasonDetailController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: ConfirmReasonDetailView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with ConfirmReasonDetailSummaryList
    with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val boxNumber = request.userAnswers.get(UnderpaymentReasonBoxNumberPage).getOrElse(BoxNumber.Box22)
    val summary   = buildSummaryList(request.userAnswers, boxNumber)
    Future.successful(
      Ok(view(summary, controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber.id)))
    )
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val underpaymentReason = Seq(
      UnderpaymentReason(
        request.userAnswers.get(UnderpaymentReasonBoxNumberPage).getOrElse(BoxNumber.Box22),
        request.userAnswers.get(UnderpaymentReasonItemNumberPage).getOrElse(0),
        request.userAnswers.get(UnderpaymentReasonAmendmentPage).getOrElse(UnderpaymentReasonValue("", "")).original,
        request.userAnswers.get(UnderpaymentReasonAmendmentPage).getOrElse(UnderpaymentReasonValue("", "")).amended
      )
    )
    val currentReasons = request.userAnswers.get(UnderpaymentReasonsPage).getOrElse(Seq.empty)
    for {
      updatedAnswers <-
        Future.fromTry(request.userAnswers.set(UnderpaymentReasonsPage, currentReasons ++ underpaymentReason))
      _ <- sessionRepository.set(updatedAnswers)
    } yield Redirect(controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad())
  }
}
