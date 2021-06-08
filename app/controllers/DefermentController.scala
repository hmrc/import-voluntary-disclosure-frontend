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
import forms.DefermentFormProvider
import javax.inject.{Inject, Singleton}
import models.SelectedDutyTypes._
import models.requests.DataRequest
import pages._
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DefermentView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class DefermentController @Inject()(identify: IdentifierAction,
                                    getData: DataRetrievalAction,
                                    requireData: DataRequiredAction,
                                    sessionRepository: SessionRepository,
                                    mcc: MessagesControllerComponents,
                                    formProvider: DefermentFormProvider,
                                    view: DefermentView)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(DefermentPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink, getHeaderMessage())))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            backLink,
            getHeaderMessage()
          )
        )
      ),
      paymentByDeferment => {
        val previousPaymentMethod = request.userAnswers.get(DefermentPage).getOrElse(paymentByDeferment)
        if (paymentByDeferment != previousPaymentMethod) {
          val userAnswers = request.userAnswers.removeMany(Seq(
            SplitPaymentPage,
            DefermentTypePage,
            DefermentAccountPage,
            AdditionalDefermentTypePage,
            AdditionalDefermentNumberPage,
            UploadAuthorityPage))
          for {
            otherUpdatedAnswers <- Future.successful(userAnswers)
            checkMode <- Future.fromTry(otherUpdatedAnswers.set(CheckModePage, false))
            updatedAnswers <- Future.fromTry(checkMode.set(DefermentPage, paymentByDeferment))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            if (paymentByDeferment) {
              redirectToDefermentView()
            } else {
              Redirect(controllers.routes.CheckYourAnswersController.onLoad())
            }
          }
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DefermentPage, paymentByDeferment))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            if (paymentByDeferment && !request.checkMode) {
              redirectToDefermentView()
            } else {
              Redirect(controllers.routes.CheckYourAnswersController.onLoad())
            }
          }
        }
      }
    )
  }

  private[controllers] def redirectToDefermentView()(implicit request: DataRequest[_]): Result = {
    if (request.isRepFlow) {
      request.dutyType match {
        case underpaymentType if underpaymentType == Both => Redirect(controllers.routes.SplitPaymentController.onLoad())
        case underpaymentType if Seq(Vat, Duty).contains(underpaymentType) => Redirect(controllers.routes.RepresentativeDanController.onLoad())
        case _ => InternalServerError("Couldn't find Underpayment types")
      }
    } else {
      Redirect(controllers.routes.ImporterDanController.onLoad())
    }
  }

  private[controllers] def getHeaderMessage()(implicit request: DataRequest[_]): String = {
    request.dutyType match {
      case Vat => "deferment.headingOnlyVAT"
      case Duty => "deferment.headingDutyOnly"
      case _ => "deferment.headingVATandDuty"
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.routes.TraderAddressCorrectController.onLoad()
    }
  }

}
