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
import forms.NumberOfEntriesFormProvider
import javax.inject.{Inject, Singleton}
import models.{NumberOfEntries, UnderpaymentAmount, UnderpaymentSummary}
import models.NumberOfEntries.{MoreThanOneEntry, OneEntry}
import pages.NumberOfEntriesPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UnderpaymentSummaryView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UnderpaymentSummaryController @Inject()(identity: IdentifierAction,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              sessionRepository: SessionRepository,
                                              appConfig: AppConfig,
                                              mcc: MessagesControllerComponents,
                                              formProvider: NumberOfEntriesFormProvider,
                                              view: UnderpaymentSummaryView)
  extends FrontendController(mcc) with I18nSupport {

  implicit val config: AppConfig = appConfig

  val onLoad: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>

    //    val form = request.userAnswers.get(NumberOfEntriesPage).fold(formProvider()) {
    //      formProvider().fill
    //    }

    val underpayments = UnderpaymentSummary(
      customsDuty = Some(UnderpaymentAmount(123, 5432)),
      importVat = Some(UnderpaymentAmount(123, 5432)),
      exciseDuty = Some(UnderpaymentAmount(123, 5432))
    )

    Future.successful(Ok(view(underpayments)))
  }



}
