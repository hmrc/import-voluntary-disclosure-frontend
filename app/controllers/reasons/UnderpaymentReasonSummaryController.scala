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
import forms.reasons.UnderpaymentReasonSummaryFormProvider
import javax.inject.Inject
import models.reasons.UnderpaymentReason
import pages.reasons.UnderpaymentReasonsPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.ActionItemHelper
import views.html.reasons.UnderpaymentReasonSummaryView

import scala.concurrent.Future

class UnderpaymentReasonSummaryController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    mcc: MessagesControllerComponents,
                                                    view: UnderpaymentReasonSummaryView,
                                                    formProvider: UnderpaymentReasonSummaryFormProvider)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    summaryList(request.userAnswers.get(UnderpaymentReasonsPage)) match {
      case Some(value) => Future.successful(Ok(view(formProvider.apply(), Some(value))))
      case None => Future.successful(InternalServerError("Couldn't find Underpayment reasons"))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            summaryList(request.userAnswers.get(UnderpaymentReasonsPage))
          )
        )
      ),
      anotherReason => {
        val reasons = request.userAnswers.get(UnderpaymentReasonsPage)
        if (anotherReason) {
          Future.successful(Redirect(controllers.reasons.routes.BoxNumberController.onLoad()))
        } else {
          if (request.checkMode) {
            Future.successful(Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad()))
          } else if (reasons.exists(_.exists(_.boxNumber == 99))) {
            Future.successful(Redirect(controllers.routes.SupportingDocController.onLoad()))
          } else {
            Future.successful(Redirect(controllers.reasons.routes.HasFurtherInformationController.onLoad()))
          }
        }
      }
    )
  }

  private[controllers] def summaryList(underpaymentReason: Option[Seq[UnderpaymentReason]]
                                      )(implicit messages: Messages): Option[SummaryList] = {
    def changeAction(boxNumber: Int, itemNumber: Int): Call = controllers.reasons.routes.ChangeUnderpaymentReasonController.change(boxNumber, itemNumber)

    underpaymentReason.map { reasons =>
      val sortedReasons = reasons.sortBy(item => item.boxNumber)
      SummaryList(
        rows = for (underpayment <- sortedReasons) yield {
          val label = underpayment.boxNumber match {
            case 99 => Text(messages("underpaymentReasonSummary.otherReason"))
            case _ => Text(s"${messages("underpaymentReasonSummary.box")} ${underpayment.boxNumber}")
          }
          val hiddenLabel = underpayment.boxNumber match {
            case 99 => messages("changeUnderpaymentReason.otherReason.change")
            case 33 | 34 | 35 | 36 | 37 | 38 | 39 | 41 | 42 | 43 | 45 | 46 => messages(
              "changeUnderpaymentReason.itemLevel.change",
              underpayment.boxNumber,
              underpayment.itemNumber
            )
            case _ => messages("changeUnderpaymentReason.entryLevel.change", underpayment.boxNumber)
          }
          SummaryListRow(
            key = Key(
              content = label
            ),
            value = Value(
              content =
                if (underpayment.boxNumber == 99) {
                  HtmlContent(messages("underpaymentReasonSummary.entryOrItem"))
                }
                else if (underpayment.itemNumber == 0) {
                  HtmlContent(messages("underpaymentReasonSummary.entryLevel"))
                } else {
                  HtmlContent(s"${messages("underpaymentReasonSummary.item")} ${underpayment.itemNumber}")
                }
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
