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
import javax.inject.Inject
import models.EORIDetails
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ConfirmEORIDetailsView

import scala.concurrent.Future

class ConfirmEORIDetailsController @Inject()(identify: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             mcc: MessagesControllerComponents,
                                             view: ConfirmEORIDetailsView)
  extends FrontendController(mcc) with I18nSupport {


  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val eoriDetails = EORIDetails("GB987654321000", "Fast Food ltd.")
    val summary = summaryList(eoriDetails).getOrElse(Seq.empty)
    Future.successful(Ok(view(summary)))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    ???
  }

  def summaryList(eoriDetails: EORIDetails)(implicit messages: Messages): Option[Seq[SummaryList]] = {

    val eoriNumberSummaryListRow: Option[Seq[SummaryListRow]] = Some(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmEORI.eoriNumber")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(eoriDetails.EORINumber)
          )
        )
      ))


    val nameSummaryListRow: Option[Seq[SummaryListRow]] = Some(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmEORI.name")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(eoriDetails.Name)
          )
        )
      ))

    val rows = eoriNumberSummaryListRow.getOrElse(Seq.empty) ++
      nameSummaryListRow.getOrElse(Seq.empty)
    if (rows.nonEmpty) {
      Some(Seq(SummaryList(rows)))
    } else {
      None
    }

  }

}
