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

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import javax.inject.{Inject, Singleton}
import models.UnderpaymentSummary
import pages._
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UnderpaymentSummaryView

import scala.concurrent.Future

@Singleton
class UnderpaymentSummaryController @Inject()(identity: IdentifierAction,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              sessionRepository: SessionRepository,
                                              appConfig: AppConfig,
                                              mcc: MessagesControllerComponents,
                                              view: UnderpaymentSummaryView)
  extends FrontendController(mcc) with I18nSupport {

  implicit val config: AppConfig = appConfig

  val onLoad: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>

    val underpayments = UnderpaymentSummary(
        customsDuty = request.userAnswers.get(CustomsDutyPage),
        importVat = request.userAnswers.get(ImportVatPage),
        exciseDuty = request.userAnswers.get(ExciseDutyPage)
      )

    Future.successful(Ok(view(underpayments, controllers.routes.UnderpaymentSummaryController.onLoad)))
  }
}
