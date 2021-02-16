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
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UnderpaymentReasonSummaryView

import javax.inject.Inject
import scala.concurrent.Future

class UnderpaymentReasonSummaryController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    mcc: MessagesControllerComponents,
                                                    view: UnderpaymentReasonSummaryView
                                                   )
  extends FrontendController(mcc) with I18nSupport {

  private lazy val backLink: Call = Call("GET", controllers.routes.UnderpaymentReasonSummaryController.onLoad().url)


  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    // form
    // page for the sequence of object
    // what to do with the back button
    // does yes or no need to be saved for this page
    // get number of reasons
    // get current reason from user answers for the previous 3 pages
    // write the user answers with the new model

    val form: Form[_] = ???
    Future.successful(Ok(view(form, 0, backLink)))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    ???
  }

}
