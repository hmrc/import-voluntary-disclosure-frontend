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

import config.ErrorHandler
import controllers.actions._
import models.requests.DataRequest
import pages.importDetails.EnterCustomsProcedureCodePage
import pages.underpayments.UnderpaymentDetailSummaryPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.underpayments.UnderpaymentStartView

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class UnderpaymentStartController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  mcc: MessagesControllerComponents,
  requireData: DataRequiredAction,
  errorHandler: ErrorHandler,
  view: UnderpaymentStartView
) extends FrontendController(mcc)
    with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    if (request.userAnswers.get(UnderpaymentDetailSummaryPage).getOrElse(Seq.empty).nonEmpty) {
      Future.successful(Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad()))
    } else {
      request.getImporterName match {
        case Some(nameOfImporterOrRep) =>
          Future.successful(
            Ok(view(backLink(), request.isOneEntry, !request.checkMode, request.isRepFlow, nameOfImporterOrRep))
          )
        case None => Future.successful(errorHandler.showInternalServerError)
      }
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.isOneEntry) {
      if (request.userAnswers.get(EnterCustomsProcedureCodePage).isDefined) {
        controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad()
      } else {
        controllers.importDetails.routes.OneCustomsProcedureCodeController.onLoad()
      }
    } else {
      controllers.importDetails.routes.AcceptanceDateController.onLoad()
    }
  }

}
