/*
 * Copyright 2023 HM Revenue & Customs
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

import config.{AppConfig, ErrorHandler}
import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.importDetails.NumberOfEntriesFormProvider
import models.UserAnswers
import models.importDetails.NumberOfEntries
import models.importDetails.NumberOfEntries.{MoreThanOneEntry, OneEntry}
import models.requests.DataRequest
import pages._
import pages.contactDetails.ImporterAddressPage
import pages.importDetails._
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.mvc._
import repositories.SessionRepository
import views.html.importDetails.NumberOfEntriesView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NumberOfEntriesController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  appConfig: AppConfig,
  mcc: MessagesControllerComponents,
  errorHandler: ErrorHandler,
  formProvider: NumberOfEntriesFormProvider,
  view: NumberOfEntriesView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val form = request.userAnswers.get(NumberOfEntriesPage).fold(formProvider()) {
      formProvider().fill
    }

    request.getImporterName.fold(errorHandler.showInternalServerError) { name =>
      Ok(view(form, name, request.isRepFlow, backLink()))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        val res = request.getImporterName.fold(errorHandler.showInternalServerError) { name =>
          BadRequest(view(formWithErrors, name, request.isRepFlow, backLink()))
        }
        Future.successful(res)
      },
      newNumberOfEntries => {
        val prevNumberOfEntries: Option[NumberOfEntries] = request.userAnswers.get(NumberOfEntriesPage)
        val cleanedUserAnswers: UserAnswers = prevNumberOfEntries match {
          case Some(oldNumberOfEntries) =>
            if (newNumberOfEntries == oldNumberOfEntries) {
              request.userAnswers
            } else {
              request.userAnswers.preserve(
                Seq(
                  ImporterAddressPage,
                  UserTypePage,
                  ImporterNamePage,
                  KnownEoriDetailsPage,
                  ImporterVatRegisteredPage,
                  ImporterEORINumberPage,
                  ImporterEORIExistsPage
                )
              )
            }
          case _ => request.userAnswers
        }
        for {
          updatedAnswers <- Future.fromTry(cleanedUserAnswers.set(NumberOfEntriesPage, newNumberOfEntries))
          _              <- sessionRepository.set(updatedAnswers)
        } yield redirect(newNumberOfEntries, cleanedUserAnswers)
      }
    )
  }

  private def redirect(entries: NumberOfEntries, userAnswers: UserAnswers): Result = {
    val isCheckMode = userAnswers.get(CheckModePage).getOrElse(false)
    if (isCheckMode) {
      Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
    } else {
      entries match {
        case OneEntry         => Redirect(controllers.importDetails.routes.EntryDetailsController.onLoad())
        case MoreThanOneEntry => Redirect(controllers.importDetails.routes.AcceptanceDateController.onLoad())
      }
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      (request.isRepFlow, request.doesImporterEORIExist) match {
        case (true, true)  => controllers.importDetails.routes.ImporterVatRegisteredController.onLoad()
        case (true, false) => controllers.importDetails.routes.ImporterEORIExistsController.onLoad()
        case _             => controllers.importDetails.routes.UserTypeController.onLoad()
      }
    }
  }
}
