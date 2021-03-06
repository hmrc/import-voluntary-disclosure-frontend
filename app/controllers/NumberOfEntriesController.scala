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

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.NumberOfEntriesFormProvider
import models.NumberOfEntries.{MoreThanOneEntry, OneEntry}
import models.requests.DataRequest
import models.{NumberOfEntries, UserAnswers}
import pages._
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.NumberOfEntriesView

import javax.inject.{Inject, Singleton}
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
        case OneEntry => Redirect(controllers.routes.EntryDetailsController.onLoad())
        case MoreThanOneEntry => Redirect(controllers.routes.AcceptanceDateController.onLoad())
      }
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.routes.CheckYourAnswersController.onLoad()
    } else {
      (request.isRepFlow, request.doesImporterEORIExist) match {
        case (true, true) => controllers.routes.ImporterVatRegisteredController.onLoad()
        case (true, false) => controllers.routes.ImporterEORIExistsController.onLoad()
        case _ => controllers.routes.UserTypeController.onLoad()
      }
    }
  }
}
