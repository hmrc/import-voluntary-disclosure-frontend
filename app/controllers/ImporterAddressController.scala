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

import com.google.inject.Inject
import config.ErrorHandler
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.ImporterAddressFormProvider
import models.{ErrorModel, TraderAddress}
import pages.ImporterAddressPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.ImporterAddressService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ImporterAddressView

import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ImporterAddressController @Inject()(identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          sessionRepository: SessionRepository,
                                          importerAddressService: ImporterAddressService,
                                          val errorHandler: ErrorHandler,
                                          mcc: MessagesControllerComponents,
                                          formProvider: ImporterAddressFormProvider,
                                          view: ImporterAddressView
                                         )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    // TODO -- get the importerAddress and store it under temporary-address within user answers
    // once we get the yes it's correct address transfer it over to confirmed-address or similar
    val form = request.userAnswers.get(ImporterAddressPage).fold(formProvider()) {
      formProvider().fill
    }
    importerAddressService.retrieveAddress("1").map {
      case Right(traderAddress) => Ok(view(form, traderAddress))
      case Left(_) => Ok("")
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, TraderAddress("", "", Some(""), "")))),
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(ImporterAddressPage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          if (value) {
            Redirect(controllers.routes.DefermentController.onLoad())
          } else {
            Redirect(controllers.routes.ImporterAddressController.onLoad()) // address lookup
          }
        }
      }
    )
  }

}
