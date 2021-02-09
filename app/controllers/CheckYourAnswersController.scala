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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.UserAnswers
import pages.{CustomsDutyPage, ExciseDutyPage, ImportVATPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.{CYASummaryList, CYASummaryListHelper}
import views.ViewUtils.displayMoney
import views.html.CheckYourAnswersView
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

@Singleton
class CheckYourAnswersController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           mcc: MessagesControllerComponents,
                                           cyaSummaryListHelper: CYASummaryListHelper,
                                           view: CheckYourAnswersView)
  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val disclosureDetails: CYASummaryList = cyaSummaryListHelper.buildDisclosureDetailsSummaryList(request.userAnswers).get
    val underpaymentDetails: CYASummaryList = cyaSummaryListHelper.buildUnderpaymentDetailsSummaryList(request.userAnswers).get
    val underpaymentDetails2: CYASummaryList = cyaSummaryListHelper.buildUnderpaymentDetailsSummaryList2(request.userAnswers).get // add new section here
    val supportingDocuments: CYASummaryList = cyaSummaryListHelper.buildSupportingDocumentsSummaryList(request.userAnswers).get // add new section here
    val yourDetailsDocuments: CYASummaryList = cyaSummaryListHelper.buildYourDetailsSummaryList(request.userAnswers).get // add new section here
    val defermentDetails: CYASummaryList = cyaSummaryListHelper.buildDefermentSummaryList(request.userAnswers).get // add new section here

    Future.successful(Ok(view(Seq(disclosureDetails,underpaymentDetails,underpaymentDetails2, supportingDocuments, yourDetailsDocuments, defermentDetails), controllers.routes.CheckYourAnswersController.onLoad)))
  }

}
