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

package controllers.importDetails

import controllers.actions._
import forms.importDetails.AcceptanceDateFormProvider
import javax.inject.{Inject, Singleton}
import models.requests.DataRequest
import pages.importDetails.AcceptanceDatePage
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.importDetails.{AcceptanceDateBulkView, AcceptanceDateView}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AcceptanceDateController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: AcceptanceDateFormProvider,
  view: AcceptanceDateView,
  bulkView: AcceptanceDateBulkView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val isOneEntry = request.isOneEntry
    val form = request.userAnswers.get(AcceptanceDatePage).fold(formProvider(isOneEntry)) {
      formProvider(isOneEntry).fill
    }

    Future.successful(
      Ok(
        if (request.isOneEntry) view(form, backLink()) else bulkView(form, backLink())
      )
    )
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider(request.isOneEntry).bindFromRequest().fold(
      formWithErrors =>
        Future.successful(
          BadRequest(
            if (request.isOneEntry) view(formWithErrors, backLink()) else bulkView(formWithErrors, backLink())
          )
        ),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(AcceptanceDatePage, value))
          _              <- sessionRepository.set(updatedAnswers)
        } yield {
          if (request.checkMode) {
            Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
          } else {
            if (request.isOneEntry) {
              Redirect(controllers.importDetails.routes.OneCustomsProcedureCodeController.onLoad())
            } else {
              Redirect(controllers.underpayments.routes.UnderpaymentStartController.onLoad())
            }
          }
        }
      }
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      if (request.isOneEntry) {
        controllers.importDetails.routes.EntryDetailsController.onLoad()
      } else {
        controllers.importDetails.routes.NumberOfEntriesController.onLoad()
      }
    }
  }

}
