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
import pages.{UnderpaymentReasonAmendmentPage, UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage, UnderpaymentReasonsPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ConfirmReasonDetailView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmReasonDetailController @Inject()(identify: IdentifierAction,
                                              getData: DataRetrievalAction,
                                              requireData: DataRequiredAction,
                                              sessionRepository: SessionRepository,
                                              mcc: MessagesControllerComponents,
                                              view: ConfirmReasonDetailView)
  extends FrontendController(mcc) with I18nSupport {


  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val summary = summaryList(request.userAnswers) match {
      case Some(value) => Seq(value)
      case None => Seq(SummaryList())
    }
    val currentBox = request.userAnswers.get(UnderpaymentReasonBoxNumberPage) match {
      case Some(value) => value
      case None => 0
    }
    Future.successful(
      Ok(
        view(
          summary,
          controllers.routes.UnderpaymentReasonAmendmentController.onLoad(currentBox))
      )
    )
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val boxNumber = request.userAnswers.get(UnderpaymentReasonBoxNumberPage)
    val itemNumber = request.userAnswers.get(UnderpaymentReasonItemNumberPage)
    val originalAndAmended = request.userAnswers.get(UnderpaymentReasonAmendmentPage)
    val currentReasons = request.userAnswers.get(UnderpaymentReasonsPage)
    val underpaymentReason = Seq(
      UnderpaymentReason(
        boxNumber = boxNumber.get,
        itemNumber = if (itemNumber.isDefined) itemNumber.get else 0,
        original = originalAndAmended.get.original,
        amended = originalAndAmended.get.amended
      )
    )
    for {
      updatedAnswers <- Future.fromTry(
        request.userAnswers.set(
          UnderpaymentReasonsPage,
          if (currentReasons.isDefined) {
            currentReasons.get ++ underpaymentReason
          } else {
            underpaymentReason
          }))
      _ <- sessionRepository.set(updatedAnswers)
    } yield {
      Redirect(controllers.routes.BoxNumberController.onLoad())
    }
  }


  def summaryList(userAnswers: UserAnswers)(implicit messages: Messages): Option[SummaryList] = {

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

    val itemNumberSummaryListRow: Option[Seq[SummaryListRow]] = userAnswers.get(UnderpaymentReasonItemNumberPage) map { itemNumber =>
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

    val originalAmountSummaryListRow: Option[Seq[SummaryListRow]] = userAnswers.get(UnderpaymentReasonAmendmentPage) map { underPaymentReasonValue =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.original")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-bottom-0"
          ),
          value = Value(
            content = HtmlContent(underPaymentReasonValue.original),
            classes = "govuk-!-padding-bottom-0"
          ),
          actions = Some(Actions(
            items = Seq(
              ActionItem(controllers.routes.UnderpaymentReasonAmendmentController.onLoad(userAnswers.get(UnderpaymentReasonBoxNumberPage).get).url, Text(messages("confirmReason.change")))
            ),
            classes = "govuk-!-padding-bottom-0")
          ),
          classes = "govuk-summary-list__row--no-border"
        )
      )
    }

    val amendedAmountSummaryListRow: Option[Seq[SummaryListRow]] = userAnswers.get(UnderpaymentReasonAmendmentPage) map { underPaymentReasonValue =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.amended")),
            classes = "govuk-!-width-two-thirds govuk-!-padding-top-0"
          ),
          value = Value(
            content = HtmlContent(underPaymentReasonValue.amended),
            classes = "govuk-!-padding-top-0"
          )
        )
      )
    }

    val rows = boxNumberSummaryListRow.getOrElse(Seq.empty) ++
      itemNumberSummaryListRow.getOrElse(Seq.empty) ++
      originalAmountSummaryListRow.getOrElse(Seq.empty) ++
      amendedAmountSummaryListRow.getOrElse(Seq.empty)

    if (rows.nonEmpty) {
      Some(
        SummaryList(
          classes = "govuk-!-margin-bottom-9",
          rows = rows
        )
      )
    } else None

  }

}
