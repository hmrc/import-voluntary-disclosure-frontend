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
import forms.MoreInformationFormProvider
import models.requests.DataRequest
import pages.MoreInformationPage
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.MoreInformationView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class MoreInformationController @Inject()(identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          sessionRepository: SessionRepository,
                                          mcc: MessagesControllerComponents,
                                          formProvider: MoreInformationFormProvider,
                                          view: MoreInformationView)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val isOneEntry = request.isOneEntry
    val form = request.userAnswers.get(MoreInformationPage).fold(formProvider(isOneEntry)) {
      formProvider(isOneEntry).fill
    }
    Future.successful(Ok(view(form, backLink, isOneEntry)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val isOneEntry = request.isOneEntry
    formProvider(isOneEntry).bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink, isOneEntry))),
      moreInfo => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(MoreInformationPage, moreInfo))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          if (request.checkMode) {
            Redirect(controllers.routes.CheckYourAnswersController.onLoad())
          } else {
            if (request.isOneEntry){
              Redirect(controllers.routes.SupportingDocController.onLoad())
            } else {
              Redirect(controllers.routes.DeclarantContactDetailsController.onLoad())
            }
          }
        }
      }
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Option[Call] = {
    if (request.checkMode) {
      None
    } else {
      if(request.isOneEntry) {
        Some(controllers.routes.HasFurtherInformationController.onLoad())
      } else {
        Some(controllers.routes.BulkUploadFileController.onSuccess())
      }
    }
  }

}
