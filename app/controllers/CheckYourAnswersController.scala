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
import models.{UnderpaymentAmount, UserAnswers}
import pages._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.CYASummaryList
import views.ViewUtils.displayMoney
import views.html.CheckYourAnswersView

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class CheckYourAnswersController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           mcc: MessagesControllerComponents,
                                           view: CheckYourAnswersView)
  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val underpaymentDetails = buildUnderpaymentDetailsSummaryList(request.userAnswers).get

    Future.successful(Ok(view(Seq(underpaymentDetails), controllers.routes.CheckYourAnswersController.onLoad)))
  }

  private[controllers] def buildUnderpaymentDetailsSummaryList(answer: UserAnswers)(implicit messages: Messages): Option[CYASummaryList] = {
    CYASummaryList(
      messages("cya.underpaymentDetails"),
      SummaryList(
        classes = "govuk-!-margin-bottom-9",
        rows = Seq(
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.customsDuty")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
            ),
            value = Value(
              content = HtmlContent(displayMoney(BigDecimal(123.22))),
              classes = "govuk-!-padding-bottom-0"
            ),
            actions = Some(Actions(
              items = Seq(
                ActionItem("Url", Text(messages("underpaymentSummary.change")))
              ),
              classes = "govuk-!-padding-bottom-0")
            ),
            classes = "govuk-summary-list__row--no-border"
          ),
          SummaryListRow(
            key = Key(
              content = Text(messages("cya.importVat")),
              classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
            ),
            value = Value(
              content = HtmlContent(displayMoney(BigDecimal(123.22))),
              classes = "govuk-!-padding-top-0"
            )
          )
        )
      )
    )
  }

}
