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
import forms.UnderpaymentTypeFormProvider
import models.{EntryDetails, UnderpaymentType}
import pages.{EntryDetailsPage, UnderpaymentTypePage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UnderpaymentTypeView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnderpaymentTypeController @Inject()(identity: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           sessionRepository: SessionRepository,
                                           mcc: MessagesControllerComponents,
                                           underpaymentTypeView: UnderpaymentTypeView,
                                           formProvider: UnderpaymentTypeFormProvider,
                                           appConfig: AppConfig
                                          )
  extends FrontendController(mcc) with I18nSupport {

  val onLoad: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    Future.successful(
      Ok(
        underpaymentTypeView(
          formProvider.apply(),
          request.userAnswers.get(UnderpaymentTypePage).getOrElse(
            UnderpaymentType(customsDuty = false, importVAT = false, exciseDuty = false)
          ),
          backLink(request.userAnswers.get(EntryDetailsPage))
        )
      )
    )
  }

  def onSubmit: Action[AnyContent] = (identity andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        Future.successful(
          BadRequest(
            underpaymentTypeView(
              formWithErrors,
              UnderpaymentType(customsDuty = false, importVAT = false, exciseDuty = false),
              backLink(request.userAnswers.get(EntryDetailsPage))
            )
          )
        )
      },
      value => {
        for {
          updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentTypePage, value))
          _ <- sessionRepository.set(updatedAnswers)
        } yield {
          value match {
            case UnderpaymentType(true, _, _) => Redirect(controllers.routes.UnderpaymentTypeController.onLoad()) // Customs Duty
            case UnderpaymentType(false, true, _) => Redirect(controllers.routes.UnderpaymentTypeController.onLoad()) // Import VAT
            case UnderpaymentType(false, false, true) => Redirect(controllers.routes.UnderpaymentTypeController.onLoad()) // Excise Duty
          }
        }
      }
    )
  }

  private def backLink(entryDetails: Option[EntryDetails]): Option[Call] = {
    val entryDetailsCall = Some(Call("GET", controllers.routes.EntryDetailsController.onLoad().toString))
    entryDetails.fold(entryDetailsCall){ value =>
      if (value.entryDate.isBefore(appConfig.euExitDate)) {
        Some(Call("GET", controllers.routes.AcceptanceDateController.onLoad().toString))
      } else {
        entryDetailsCall
      }
    }
  }

}
