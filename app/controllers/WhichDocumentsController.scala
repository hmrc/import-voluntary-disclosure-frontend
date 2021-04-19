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
import forms.WhichDocumentsFormProvider
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.govukfrontend.views.Aliases.{Content, Label, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.WhichDocumentsView

import javax.inject.Inject
import scala.concurrent.Future

class WhichDocumentsController @Inject()(identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         sessionRepository: SessionRepository,
                                         mcc: MessagesControllerComponents,
                                         view: WhichDocumentsView,
                                         formProvider: WhichDocumentsFormProvider)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    Future.successful(Ok(view(
      formProvider.apply(),
      controllers.routes.WhichDocumentsController.onLoad(),
      Seq(
        CheckboxItem(
          value = "Blah",
          label = Some(Label(Some("Blah"))),
          name = Some("blah"),
          content = Text("blah")
        )
      )
    )))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    ???
  }

}
