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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.underpayments.UnderpaymentDetailSummaryFormProvider
import models.SelectedDutyTypes._
import models.requests.DataRequest
import models.underpayments.UnderpaymentDetail
import pages._
import pages.underpayments.{TempUnderpaymentTypePage, UnderpaymentDetailSummaryPage}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.ActionItemHelper
import views.ViewUtils.displayMoney
import views.html.underpayments.UnderpaymentDetailSummaryView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnderpaymentDetailSummaryController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    sessionRepository: SessionRepository,
                                                    mcc: MessagesControllerComponents,
                                                    view: UnderpaymentDetailSummaryView,
                                                    formProvider: UnderpaymentDetailSummaryFormProvider
                                                   )
  extends FrontendController(mcc) with I18nSupport {

  def cya(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(TempUnderpaymentTypePage, request.dutyType))
      _ <- sessionRepository.set(updatedAnswers)
    } yield {
      Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad())
    }
  }

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val fallbackResponse = Redirect(controllers.underpayments.routes.UnderpaymentStartController.onLoad())

    val result = request.userAnswers.get(UnderpaymentDetailSummaryPage).map {
      case Nil => fallbackResponse
      case underpayments => Ok(
        view(formProvider(), summaryList(underpayments), amountOwedSummaryList(underpayments), underpayments.length)
      )
    }.getOrElse(fallbackResponse)

    Future.successful(result)
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        val underpayments = request.userAnswers.get(UnderpaymentDetailSummaryPage).getOrElse(Seq.empty)
        Future.successful(BadRequest(view(formWithErrors, summaryList(underpayments), amountOwedSummaryList(underpayments), underpayments.length)))
      },
      addAnother => {
        if (addAnother) {
          Future.successful(Redirect(controllers.underpayments.routes.UnderpaymentTypeController.onLoad()))
        } else {
          if (request.isRepFlow) {
            redirectForRepFlow()
          } else {
            if (request.checkMode) {
              Future.successful(Redirect(controllers.routes.CheckYourAnswersController.onLoad()))
            } else {
              Future.successful(Redirect(controllers.routes.BoxGuidanceController.onLoad()))
            }
          }
        }
      }
    )
  }

  def redirectForRepFlow()(implicit request: DataRequest[_]): Future[Result] = {
    val newUnderpaymentType: SelectedDutyType = request.dutyType
    val oldUnderpaymentType = request.userAnswers.get(TempUnderpaymentTypePage)
    val splitThePayment = request.userAnswers.get(SplitPaymentPage)
    val dutyOrVatOnly = Seq(Duty, Vat)

    (oldUnderpaymentType, newUnderpaymentType, splitThePayment) match {
      case (Some(oldType), Both, _) if dutyOrVatOnly.contains(oldType) =>
        removePaymentDataAndRedirect()
      case (Some(Both), newType, Some(true)) if dutyOrVatOnly.contains(newType) =>
        removePaymentDataAndRedirect()
      case (None, _, _) => Future.successful(Redirect(controllers.routes.BoxGuidanceController.onLoad()))
      case _ => Future.successful(Redirect(controllers.routes.CheckYourAnswersController.onLoad()))
    }
  }

  def removePaymentDataAndRedirect()(implicit request: DataRequest[_]): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.remove(DefermentPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(SplitPaymentPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(DefermentTypePage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(DefermentAccountPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(UploadAuthorityPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(AdditionalDefermentTypePage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(AdditionalDefermentNumberPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(TempUnderpaymentTypePage))
      _ <- sessionRepository.set(updatedAnswers)
    } yield {
      Redirect(controllers.routes.DefermentController.onLoad())
    }
  }

  private[controllers] def summaryList(underpaymentDetail: Seq[UnderpaymentDetail])
                                      (implicit messages: Messages): SummaryList = {
    SummaryList(
      rows = for (underpayment <- underpaymentDetail.reverse) yield
        SummaryListRow(
          key = Key(
            content = Text(messages(s"underpaymentDetailsSummary.${underpayment.duty}")),
            classes = "govuk-summary-list__key govuk-!-width-two-thirds govuk-!-font-weight-regular"
          ),
          value = Value(
            content = HtmlContent(displayMoney(underpayment.amended - underpayment.original)),
            classes = "govuk-summary-list__value"
          ),
          actions = Some(
            Actions(
              items = Seq(
                ActionItemHelper.createChangeActionItem(
                  controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpayment.duty).url,
                  messages(s"underpaymentDetailsSummary.${underpayment.duty}.change")
                )
              )
            )
          )
        )
    )
  }

  def amountOwedSummaryList(underpaymentDetail: Seq[UnderpaymentDetail])(implicit messages: Messages): SummaryList = {
    val amountOwed = underpaymentDetail.map(underpayment => underpayment.amended - underpayment.original).sum
    SummaryList(
      rows = Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages(s"underpaymentDetailsSummary.owedToHMRC")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(content = HtmlContent(displayMoney(amountOwed))),
          classes = "govuk-summary-list__row--no-border govuk-!-font-weight-bold"
        )
      )
    )
  }

}
