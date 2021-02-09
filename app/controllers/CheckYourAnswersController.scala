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

import connectors.IVDSubmissionConnector
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{IVDSubmission, UserAnswers}
import pages.{CustomsDutyPage, ExciseDutyPage, ImportVATPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.{CYASummaryList, CYASummaryListHelper}
import views.ViewUtils.displayMoney
import views.html.CheckYourAnswersView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           mcc: MessagesControllerComponents,
                                           ivdSubmissionConnector: IVDSubmissionConnector,
                                           cyaSummaryListHelper: CYASummaryListHelper,
                                           view: CheckYourAnswersView,
                                           implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val underpaymentDetails: CYASummaryList = cyaSummaryListHelper.buildUnderpaymentDetailsSummaryList(request.userAnswers).get
    val underpaymentDetails2: CYASummaryList = cyaSummaryListHelper.buildUnderpaymentDetailsSummaryList2(request.userAnswers).get // add new section here
    val supportingDocuments: CYASummaryList = cyaSummaryListHelper.buildSupportingDocumentsSummaryList(request.userAnswers).get // add new section here
    val yourDetailsDocuments: CYASummaryList = cyaSummaryListHelper.buildYourDetailsSummaryList(request.userAnswers).get // add new section here

    Future.successful(Ok(view(Seq(underpaymentDetails,underpaymentDetails2, supportingDocuments, yourDetailsDocuments), controllers.routes.CheckYourAnswersController.onLoad)))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val submission: IVDSubmission = Json.fromJson[IVDSubmission](request.userAnswers.data).get
    val submissionResponse = ivdSubmissionConnector.postSubmission(submission)

    Future.successful(Redirect(controllers.routes.CheckYourAnswersController.onLoad))
  }
}
