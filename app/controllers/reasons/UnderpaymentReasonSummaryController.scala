/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.reasons.UnderpaymentReasonSummaryFormProvider
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, UnderpaymentReason}
import pages.reasons.UnderpaymentReasonsPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.ActionItemHelper
import views.html.reasons.UnderpaymentReasonSummaryView

import javax.inject.Inject
import scala.concurrent.Future

class UnderpaymentReasonSummaryController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  mcc: MessagesControllerComponents,
  view: UnderpaymentReasonSummaryView,
  formProvider: UnderpaymentReasonSummaryFormProvider
) extends FrontendController(mcc)
    with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    summaryList(request.userAnswers.get(UnderpaymentReasonsPage)) match {
      case Some(value) => Future.successful(Ok(view(formProvider.apply(), Some(value))))
      case None        => Future.successful(InternalServerError("Couldn't find Underpayment reasons"))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors =>
        Future.successful(
          BadRequest(
            view(
              formWithErrors,
              summaryList(request.userAnswers.get(UnderpaymentReasonsPage))
            )
          )
        ),
      anotherReason => {
        if (anotherReason) {
          Future.successful(Redirect(controllers.reasons.routes.BoxNumberController.onLoad()))
        } else {
          if (request.checkMode) {
            Future.successful(Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad()))
          } else {
            Future.successful(Redirect(controllers.docUpload.routes.SupportingDocController.onLoad()))
          }
        }
      }
    )
  }

  private[controllers] def summaryList(
    underpaymentReason: Option[Seq[UnderpaymentReason]]
  )(implicit messages: Messages): Option[SummaryList] = {
    def changeAction(boxNumber: BoxNumber, itemNumber: Int): Call =
      controllers.reasons.routes.ChangeUnderpaymentReasonController.change(boxNumber.id, itemNumber)

    underpaymentReason.map { reasons =>
      val sortedReasons = reasons.sortBy(item => item.boxNumber)
      SummaryList(
        rows = for (underpayment <- sortedReasons) yield {
          val label = underpayment.boxNumber match {
            case BoxNumber.OtherItem => Text(messages("underpaymentReasonSummary.otherReason"))
            case _ => Text(s"${messages("underpaymentReasonSummary.box")} ${underpayment.boxNumber.id}")
          }
          val hiddenLabel = underpayment.boxNumber match {
            case BoxNumber.OtherItem => messages("underpaymentReasonSummary.otherReason.change")
            case BoxNumber.Box33 | BoxNumber.Box34 | BoxNumber.Box35 | BoxNumber.Box36 | BoxNumber.Box37 |
                BoxNumber.Box38 | BoxNumber.Box39 | BoxNumber.Box41 | BoxNumber.Box42 | BoxNumber.Box43 |
                BoxNumber.Box45 | BoxNumber.Box46 =>
              messages("underpaymentReasonSummary.itemLevel.change", underpayment.boxNumber.id, underpayment.itemNumber)
            case _ => messages("underpaymentReasonSummary.entryLevel.change", underpayment.boxNumber.id)
          }
          SummaryListRow(
            key = Key(content = label),
            value = Value(
              content = if (underpayment.boxNumber == BoxNumber.OtherItem) {
                HtmlContent(messages("underpaymentReasonSummary.entryOrItem"))
              } else if (underpayment.itemNumber == 0) {
                HtmlContent(messages("underpaymentReasonSummary.entryLevel"))
              } else {
                HtmlContent(s"${messages("underpaymentReasonSummary.item")} ${underpayment.itemNumber}")
              },
              classes = "govuk-!-width-one-half"
            ),
            actions = Some(
              Actions(
                items = Seq(
                  ActionItemHelper.createChangeActionItem(
                    changeAction(underpayment.boxNumber, underpayment.itemNumber).url,
                    hiddenLabel
                  )
                ),
                classes = "govuk-!-width-one-third"
              )
            )
          )
        }
      )
    }
  }

}
