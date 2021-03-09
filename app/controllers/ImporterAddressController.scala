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
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.ImporterAddressFormProvider
import javax.inject.Singleton
import models.ContactAddress
import pages.{ImporterAddressFinalPage, KnownEoriDetails, ReuseKnowAddressPage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ImporterAddressView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ImporterAddressController @Inject()(identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          sessionRepository: SessionRepository,
                                          mcc: MessagesControllerComponents,
                                          formProvider: ImporterAddressFormProvider,
                                          view: ImporterAddressView
                                         )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val eoriDetails = request.userAnswers.get(KnownEoriDetails).get
    val form = request.userAnswers.get(ReuseKnowAddressPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, eoriDetails.address)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val traderAddress: ContactAddress = request.userAnswers.get(KnownEoriDetails).get.address
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, traderAddress))),
      value => {
        if (value) {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ReuseKnowAddressPage, value))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(ImporterAddressFinalPage, traderAddress))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.DefermentController.onLoad())
          }
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ReuseKnowAddressPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            Redirect(controllers.routes.AddressLookupController.initialiseJourney())
          }
        }
      }
    )
  }

}
