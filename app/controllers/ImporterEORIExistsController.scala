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
import forms.ImporterEORIExistsFormProvider
import models.requests.DataRequest
import pages.{ImporterEORIExistsPage, ImporterEORINumberPage, ImporterVatRegisteredPage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ImporterEORIExistsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ImporterEORIExistsController @Inject()(identify: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             sessionRepository: SessionRepository,
                                             mcc: MessagesControllerComponents,
                                             formProvider: ImporterEORIExistsFormProvider,
                                             view: ImporterEORIExistsView,
                                             implicit val ec: ExecutionContext
                                            )
  extends FrontendController(mcc) with I18nSupport {


  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(ImporterEORIExistsPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))

  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      eoriExists => {
        if (eoriExists) {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ImporterEORIExistsPage, eoriExists))
            _ <- sessionRepository.set(updatedAnswers)
          }
          yield {
            Redirect(controllers.routes.ImporterEORINumberController.onLoad())
          }
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ImporterEORIExistsPage, eoriExists))
            updatedAnswers <- Future.fromTry(updatedAnswers.remove(ImporterEORINumberPage))
            updatedAnswers <- Future.fromTry(updatedAnswers.remove(ImporterVatRegisteredPage))
            _ <- sessionRepository.set(updatedAnswers)
          }
          yield {
            if (request.checkMode) {
              Redirect(controllers.routes.CheckYourAnswersController.onLoad())
            } else {
              Redirect(controllers.routes.NumberOfEntriesController.onLoad())

            }
          }
        }
      }
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.routes.ImporterNameController.onLoad()
    }
  }

}
