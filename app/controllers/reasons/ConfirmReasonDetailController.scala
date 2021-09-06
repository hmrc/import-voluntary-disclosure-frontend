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

package controllers.reasons

import controllers.actions._
import models.UserAnswers
import models.reasons.BoxNumber.BoxNumber
import models.reasons._
import pages.reasons._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.ActionItemHelper
import views.html.reasons.ConfirmReasonDetailView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmReasonDetailController @Inject() (
                                                identify: IdentifierAction,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                sessionRepository: SessionRepository,
                                                mcc: MessagesControllerComponents,
                                                view: ConfirmReasonDetailView,
                                                implicit val ec: ExecutionContext
                                              ) extends FrontendController(mcc)
  with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val boxNumber = request.userAnswers.get(UnderpaymentReasonBoxNumberPage).getOrElse(BoxNumber.Box22)
    val summary   = summaryList(request.userAnswers, boxNumber).getOrElse(Seq.empty)
    Future.successful(
      Ok(view(summary, controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber.id)))
    )
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val underpaymentReason = Seq(
      UnderpaymentReason(
        request.userAnswers.get(UnderpaymentReasonBoxNumberPage).getOrElse(BoxNumber.Box22),
        request.userAnswers.get(UnderpaymentReasonItemNumberPage).getOrElse(0),
        request.userAnswers.get(UnderpaymentReasonAmendmentPage).getOrElse(UnderpaymentReasonValue("", "")).original,
        request.userAnswers.get(UnderpaymentReasonAmendmentPage).getOrElse(UnderpaymentReasonValue("", "")).amended
      )
    )
    val currentReasons = request.userAnswers.get(UnderpaymentReasonsPage).getOrElse(Seq.empty)
    for {
      updatedAnswers <-
        Future.fromTry(request.userAnswers.set(UnderpaymentReasonsPage, currentReasons ++ underpaymentReason))
      _ <- sessionRepository.set(updatedAnswers)
    } yield Redirect(controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad())
  }

  def boxNumberSummaryListRow(userAnswers: UserAnswers)(implicit messages: Messages): Option[Seq[SummaryListRow]] =
    userAnswers.get(UnderpaymentReasonBoxNumberPage) map { boxNumber =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.boxNumber")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(boxNumber.id.toString)
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItemHelper.createChangeActionItem(
                  controllers.reasons.routes.BoxNumberController.onLoad().url,
                  messages("confirmReason.box.change")
                )
              )
            )
          )
        )
      )
    }

  def itemNumberSummaryListRow(userAnswers: UserAnswers)(implicit messages: Messages): Option[Seq[SummaryListRow]] =
    userAnswers.get(UnderpaymentReasonItemNumberPage) map { itemNumber =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.itemNumber")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(itemNumber.toString)
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItemHelper.createChangeActionItem(
                  controllers.reasons.routes.ItemNumberController.onLoad().url,
                  messages("confirmReason.item.change")
                )
              )
            )
          )
        )
      )
    }

  def originalAmountSummaryListRow(userAnswers: UserAnswers, boxNumber: BoxNumber)(implicit
                                                                                   messages: Messages
  ): Option[Seq[SummaryListRow]] =
    userAnswers.get(UnderpaymentReasonAmendmentPage) map { underPaymentReasonValue =>
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.original")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(underPaymentReasonValue.original)
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItemHelper.createChangeActionItem(
                  controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber.id).url,
                  messages("confirmReason.values.original.change")
                )
              )
            )
          )
        ),
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmReason.amended")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(underPaymentReasonValue.amended)
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItemHelper.createChangeActionItem(
                  controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(boxNumber.id).url,
                  messages("confirmReason.values.amended.change")
                )
              )
            )
          )
        )
      )
    }

  def summaryList(userAnswers: UserAnswers, boxNumber: BoxNumber)(implicit
                                                                  messages: Messages
  ): Option[Seq[SummaryList]] = {
    val rows =
      if (boxNumber == BoxNumber.OtherItem) {
        userAnswers
          .get(UnderpaymentReasonAmendmentPage)
          .map(value => otherReasonSummaryList(value.original))
          .getOrElse(Seq.empty)
      } else {
        boxNumberSummaryListRow(userAnswers).getOrElse(Seq.empty) ++
          itemNumberSummaryListRow(userAnswers).getOrElse(Seq.empty) ++
          originalAmountSummaryListRow(userAnswers, boxNumber).getOrElse(Seq.empty)
      }

    if (rows.nonEmpty) {
      Some(Seq(SummaryList(rows)))
    } else {
      None
    }
  }

  private def otherReasonSummaryList(value: String)(implicit messages: Messages) =
    Seq(
      SummaryListRow(
        key = Key(content = Text(messages("confirmReason.otherReason"))),
        value = Value(content = HtmlContent(value)),
        actions = Some(
          Actions(
            items = Seq(
              ActionItemHelper.createChangeActionItem(
                controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(BoxNumber.OtherItem.id).url,
                messages("confirmReason.values.otherReason.change")
              )
            )
          )
        )
      )
    )
}
