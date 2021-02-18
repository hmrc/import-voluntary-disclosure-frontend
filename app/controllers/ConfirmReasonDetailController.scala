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
import models.{UnderpaymentReason, UserAnswers}
import pages.{ItemNumberPage, UnderpaymentReasonBoxNumberPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.{CYASummaryList, ConfirmReasonSummaryList}
import views.html.ConfirmReasonDetailView

import javax.inject.Inject
import scala.concurrent.Future

class ConfirmReasonDetailController @Inject()(identify: IdentifierAction,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              mcc: MessagesControllerComponents,
                                              view: ConfirmReasonDetailView)
  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val confirmReasonSummary = summaryList(request.userAnswers).get

    Future.successful(Ok(view(Seq(confirmReasonSummary))))
  }

def summaryList(userAnswers: UserAnswers)(implicit messages: Messages): Option[ConfirmReasonSummaryList] = {

    val boxNumberSummaryListRow: Option[Seq[SummaryListRow]] = userAnswers.get(UnderpaymentReasonBoxNumberPage) map { boxNumber =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.boxNumber")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(boxNumber.toString)
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(controllers.routes.BoxNumberController.onLoad().url, Text(messages("confirmReason.change")))
            )
          )
          )
        )
      )
    }

  val itemNumberSummaryListRow: Option[Seq[SummaryListRow]] = userAnswers.get(ItemNumberPage) map { itemNumber =>
    Seq(
      SummaryListRow(
        key = Key(
          content = Text(messages("confirmReason.itemNumber")),
          classes = "govuk-!-width-two-thirds"
        ),
        value = Value(
          content = HtmlContent(itemNumber.toString)
        ),
        actions = Some(Actions(
          items = Seq(
            ActionItem(controllers.routes.ItemNumberController.onLoad().url, Text(messages("confirmReason.change")))
          )
        )
        )
      )
    )
  }

    val rows = boxNumberSummaryListRow.getOrElse(Seq.empty) ++
      itemNumberSummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        ConfirmReasonSummaryList(
          SummaryList(
            classes = "govuk-!-margin-bottom-9",
            rows = rows
          )
        )
      )
    } else None

  }

}
