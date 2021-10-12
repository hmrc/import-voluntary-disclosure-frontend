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
import pages.paymentInfo._
import pages.underpayments.{PostponedVatAccountingPage, TempUnderpaymentTypePage, UnderpaymentDetailSummaryPage}
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
import scala.concurrent.{ExecutionContext, Future}

class UnderpaymentDetailSummaryController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: UnderpaymentDetailSummaryView,
  formProvider: UnderpaymentDetailSummaryFormProvider,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  def cya(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(TempUnderpaymentTypePage, request.dutyType))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad())
  }

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val fallbackResponse = Redirect(controllers.underpayments.routes.UnderpaymentStartController.onLoad())

    val result = request.userAnswers.get(UnderpaymentDetailSummaryPage).map {
      case Nil => fallbackResponse
      case underpayments =>
        Ok(
          view(
            formProvider(),
            summaryList(underpayments),
            amountOwedSummaryList(underpayments),
            underpayments.length,
            request.isOneEntry
          )
        )
    }.getOrElse(fallbackResponse)

    Future.successful(result)
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        val underpayments = request.userAnswers.get(UnderpaymentDetailSummaryPage).getOrElse(Seq.empty)
        val content = view(
          formWithErrors,
          summaryList(underpayments),
          amountOwedSummaryList(underpayments),
          underpayments.length,
          request.isOneEntry
        )
        Future.successful(BadRequest(content))
      },
      addAnother => {
        if (addAnother) {
          Future.successful(Redirect(controllers.underpayments.routes.UnderpaymentTypeController.onLoad()))
        } else {
          cleanupVatAccounting(request).flatMap { updatedRequest =>
            redirect(updatedRequest)
          }
        }
      }
    )
  }

  private[underpayments] def cleanupVatAccounting(request: DataRequest[_]): Future[DataRequest[_]] = {
    if (request.dutyType != Vat) {
      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.remove(PostponedVatAccountingPage))
        _              <- sessionRepository.set(updatedAnswers)
      } yield request.copy(userAnswers = updatedAnswers)
    } else {
      Future.successful(request)
    }
  }

  private[underpayments] def redirect(request: DataRequest[_]): Future[Result] = {
    if (request.checkMode) {
      redirectForCheckMode(request)
    } else if (!request.isOneEntry) {
      Future.successful(Redirect(controllers.docUpload.routes.BulkUploadFileController.onLoad()))
    } else if (request.dutyType == Vat) {
      Future.successful(Redirect(controllers.underpayments.routes.PostponedVatAccountingController.onLoad()))
    } else {
      Future.successful(Redirect(controllers.reasons.routes.BoxGuidanceController.onLoad()))
    }
  }

  private[underpayments] def redirectForCheckMode(request: DataRequest[_]): Future[Result] = {
    val newUnderpaymentType: SelectedDutyType = request.dutyType
    val oldUnderpaymentType                   = request.userAnswers.get(TempUnderpaymentTypePage)
    val splitThePayment                       = request.userAnswers.get(SplitPaymentPage)
    val dutyOrVatOnly                         = Seq(Duty, Vat)

    (oldUnderpaymentType, newUnderpaymentType) match {
      case (Some(other), Vat) if other != Vat =>
        val removePaymentDataIfChanged =
          if (request.isRepFlow && oldUnderpaymentType.contains(Both)) {
            removePaymentData(request)
          } else Future.successful(())

        removePaymentDataIfChanged.map { _ =>
          Redirect(controllers.underpayments.routes.PostponedVatAccountingController.onLoad())
        }
      case (Some(oldType), Both) if request.isRepFlow && dutyOrVatOnly.contains(oldType) =>
        redirectForDeferment(request)
      case (Some(Both), newType)
          if request.isRepFlow && dutyOrVatOnly.contains(newType) && splitThePayment.exists(identity) =>
        redirectForDeferment(request)
      case _ =>
        Future.successful(Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad()))
    }
  }

  private def removePaymentData(request: DataRequest[_]): Future[Unit] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.remove(DefermentPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(SplitPaymentPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(DefermentTypePage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(DefermentAccountPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(UploadAuthorityPage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(AdditionalDefermentTypePage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(AdditionalDefermentNumberPage))
      _              <- sessionRepository.set(updatedAnswers)
    } yield ()
  }

  private def redirectForDeferment(request: DataRequest[_]): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.remove(CheckModePage))
      updatedAnswers <- Future.fromTry(updatedAnswers.remove(TempUnderpaymentTypePage))
      _              <- removePaymentData(request.copy(userAnswers = updatedAnswers))
    } yield Redirect(controllers.paymentInfo.routes.DefermentController.onLoad())

  private[controllers] def summaryList(
    underpaymentDetail: Seq[UnderpaymentDetail]
  )(implicit messages: Messages): SummaryList = {
    SummaryList(
      rows =
        for (underpayment <- underpaymentDetail.reverse)
          yield SummaryListRow(
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
