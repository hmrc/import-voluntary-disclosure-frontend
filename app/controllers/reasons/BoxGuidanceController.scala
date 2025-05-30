/*
 * Copyright 2025 HM Revenue & Customs
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

import config.AppConfig
import controllers.IVDFrontendController
import controllers.actions._
import models.SelectedDutyTypes.{SelectedDutyType, Vat}
import pages.reasons.UnderpaymentReasonsPage
import play.api.mvc._
import views.html.reasons.BoxGuidanceView

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class BoxGuidanceController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  mcc: MessagesControllerComponents,
  requireData: DataRequiredAction,
  view: BoxGuidanceView,
  implicit val appConfig: AppConfig
) extends IVDFrontendController(mcc) {

  private[reasons] def backLink(dutyType: SelectedDutyType): Call = {
    if (dutyType == Vat) {
      controllers.underpayments.routes.PostponedVatAccountingController.onLoad()
    } else {
      controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()
    }
  }

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    if (request.userAnswers.get(UnderpaymentReasonsPage).getOrElse(Seq.empty).nonEmpty) {
      Future.successful(Redirect(controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad()))
    } else {
      Future.successful(Ok(view(backLink(request.dutyType), !request.checkMode)))
    }
  }

}
