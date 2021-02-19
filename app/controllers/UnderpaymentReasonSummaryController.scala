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
import forms.UnderpaymentReasonSummaryFormProvider
import models.UnderpaymentReason
import pages.UnderpaymentReasonsPage
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UnderpaymentReasonSummaryView

import javax.inject.Inject
import scala.concurrent.Future

class UnderpaymentReasonSummaryController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    mcc: MessagesControllerComponents,
                                                    view: UnderpaymentReasonSummaryView,
                                                    formProvider: UnderpaymentReasonSummaryFormProvider
                                                   )
  extends FrontendController(mcc) with I18nSupport {

  // TODO - route the page flow for adding boxes correctly after the item number page
  // TODO - routing from item number page to the original/amended page
  // TODO - from original/amended page to summary
  // TODO - from summary to reasons summary

  private val backLink: Call = controllers.routes.BoxGuidanceController.onLoad()

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    Future.successful(
      Ok(
        view(
          formProvider.apply(),
          backLink,
          summaryList(request.userAnswers.get(UnderpaymentReasonsPage))
        )
      )
    )
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            backLink,
            summaryList(request.userAnswers.get(UnderpaymentReasonsPage))
          )
        )
      ),
      anotherReason => {
        if (anotherReason) {
          Future.successful(Redirect(controllers.routes.BoxNumberController.onLoad()))
        } else {
          Future.successful(Redirect(controllers.routes.UploadFileController.onLoad()))
        }
      }
    )
  }

  private[controllers] def summaryList(
                                        underpaymentReason: Option[Seq[UnderpaymentReason]]
                                      )(implicit messages: Messages): Option[SummaryList] = {
    // TODO - needs to change when implementing the change and remove buttons
    val changeAction: Call = Call("GET", controllers.routes.UnderpaymentReasonSummaryController.onLoad().url)
    underpaymentReason.map { reasons =>
      val sortedReasons = reasons.sortBy(item => item.boxNumber)
      SummaryList(
        rows = for (underpayment <- sortedReasons) yield
          SummaryListRow(
            key = Key(
              content = Text("Box " + underpayment.boxNumber)
            ),
            value = Value(
              content = if (underpayment.itemNumber == 0) {
                HtmlContent("Entry Level")
              } else {
                HtmlContent("Item " + underpayment.itemNumber)
              }
            ),
            actions = Some(
              Actions(
                items = Seq(
                  ActionItem(
                    changeAction.url,
                    Text(messages("underpaymentSummary.change")),
                    Some("key")
                  ),
                  ActionItem(
                    changeAction.url,
                    Text(messages("underpaymentReasonSummary.remove")),
                    Some("key")
                  )
                ),
                classes = "govuk-!-width-one-third"
              )
            )
          )
      )
    }
  }

}
