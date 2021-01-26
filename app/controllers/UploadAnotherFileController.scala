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
import forms.UploadAnotherFileFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import pages.UploadAnotherFilePage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import queries.FileUploadQuery
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.AddFileNameRowHelper
import views.html.UploadAnotherFileView

import scala.concurrent.{ExecutionContext, Future}

class UploadAnotherFileController @Inject()(identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            sessionRepository: SessionRepository,
                                            mcc: MessagesControllerComponents,
                                            formProvider: UploadAnotherFileFormProvider,
                                            view: UploadAnotherFileView)(implicit ec: ExecutionContext)

  extends FrontendController(mcc) with I18nSupport {


  val onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
      implicit request =>
        val form = request.userAnswers.get(UploadAnotherFilePage).fold(formProvider()) {
          formProvider().fill
        }
        request.userAnswers.get(FileUploadQuery).fold(Future(Redirect(controllers.routes.SupportingDocController.onLoad().url))) { files =>
            val helper = new AddFileNameRowHelper(files)
            val rows = helper.rows
            if(files.isEmpty) {
              Future.successful(Redirect(controllers.routes.SupportingDocController.onLoad()))
            } else {
              Future.successful(Ok(view(form, backLink, rows)))
            }
      }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => resultWithErrors(formWithErrors),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(UploadAnotherFilePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        }  yield {
          if (value) {
            Redirect(controllers.routes.UploadAnotherFileController.onLoad())
          } else {
            Redirect(controllers.routes.UploadAnotherFileController.onLoad())
          }
        }
      }
    )
  }

  private[controllers] def backLink: Call = Call("GET",controllers.routes.SupportingDocController.onLoad().url)

  private def resultWithErrors(formWithErrors: Form[Boolean])(implicit request: DataRequest[AnyContent]): Future[Result] = {
    request.userAnswers.get(FileUploadQuery).fold(Future(Redirect(controllers.routes.SupportingDocController.onLoad().url))) { files =>
      val helper = new AddFileNameRowHelper(files)
      val rows = helper.rows

      Future.successful(BadRequest(view(formWithErrors, backLink, rows)))
    }
  }
}
