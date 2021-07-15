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

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.importDetails.NumberOfEntriesFormProvider
import javax.inject.{Inject, Singleton}
import models.importDetails.NumberOfEntries.{MoreThanOneEntry, OneEntry}
import models.requests.DataRequest
import models.UserAnswers
import models.importDetails.NumberOfEntries
import pages._
import pages.importDetails._
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.importDetails.NumberOfEntriesView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NumberOfEntriesController @Inject()(identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          sessionRepository: SessionRepository,
                                          appConfig: AppConfig,
                                          mcc: MessagesControllerComponents,
                                          formProvider: NumberOfEntriesFormProvider,
                                          view: NumberOfEntriesView,
                                          implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  implicit val config: AppConfig = appConfig

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val form = request.userAnswers.get(NumberOfEntriesPage).fold(formProvider()) {
      formProvider().fill
    }

    Future.successful(Ok(view(form, request.isRepFlow, backLink())))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, request.isRepFlow, backLink()))),
      newNumberOfEntries => {
        val prevNumberOfEntries: Option[NumberOfEntries] = request.userAnswers.get(NumberOfEntriesPage)
        val cleanedUserAnswers: UserAnswers = prevNumberOfEntries match {
          case Some(oldNumberOfEntries) => if (newNumberOfEntries == oldNumberOfEntries) {
            request.userAnswers
          } else {
            request.userAnswers.preserve(
              Seq(
                ImporterAddressPage, UserTypePage, ImporterNamePage, KnownEoriDetailsPage, ImporterVatRegisteredPage,
                ImporterEORINumberPage, ImporterEORIExistsPage
              )
            )
          }
          case _ => request.userAnswers
        }
        for {
          updatedAnswers <- Future.fromTry(cleanedUserAnswers.set(NumberOfEntriesPage, newNumberOfEntries))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          redirect(newNumberOfEntries, cleanedUserAnswers)
        }
      }
    )
  }

  private def redirect(entries: NumberOfEntries, userAnswers: UserAnswers): Result = {
    val isCheckMode = userAnswers.get(CheckModePage).getOrElse(false)
    if (isCheckMode) {
      Redirect(controllers.routes.CheckYourAnswersController.onLoad())
    } else {
      entries match {
        case OneEntry => Redirect(controllers.importDetails.routes.EntryDetailsController.onLoad())
        case MoreThanOneEntry => Redirect(controllers.importDetails.routes.AcceptanceDateController.onLoad())
      }
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.routes.CheckYourAnswersController.onLoad()
    } else {
      (request.isRepFlow, request.doesImporterEORIExist) match {
        case (true, true) => controllers.importDetails.routes.ImporterVatRegisteredController.onLoad()
        case (true, false) => controllers.importDetails.routes.ImporterEORIExistsController.onLoad()
        case _ => controllers.importDetails.routes.UserTypeController.onLoad()
      }
    }
  }
}
