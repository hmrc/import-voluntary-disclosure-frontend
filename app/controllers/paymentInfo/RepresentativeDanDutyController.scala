/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.paymentInfo

import com.google.inject.Inject
import config.ErrorHandler
import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.paymentInfo.RepresentativeDanFormProvider
import models.RepresentativeDan
import models.SelectedDutyTypes.Duty
import models.requests.DataRequest
import pages._
import pages.paymentInfo._
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.mvc._
import repositories.SessionRepository
import views.html.paymentInfo.RepresentativeDanDutyView

import scala.concurrent.{ExecutionContext, Future}

class RepresentativeDanDutyController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  errorHandler: ErrorHandler,
  view: RepresentativeDanDutyView,
  formProvider: RepresentativeDanFormProvider,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val repName = request.userAnswers.get(KnownEoriDetailsPage).get.name
    val form = (for {
      danType       <- request.userAnswers.get(DefermentTypePage)
      accountNumber <- request.userAnswers.get(DefermentAccountPage)
    } yield formProvider().fill(RepresentativeDan(accountNumber, danType))).getOrElse(formProvider())

    request.getImporterName.fold(errorHandler.showInternalServerError) { name =>
      Ok(view(form, name, repName, backLink))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val repName = request.userAnswers.get(KnownEoriDetailsPage).get.name
    formProvider().bindFromRequest().fold(
      formWithErrors => {
        val res = request.getImporterName.fold(errorHandler.showInternalServerError) { name =>
          BadRequest(view(formWithErrors, name, repName, backLink))
        }
        Future.successful(res)
      },
      dan => {
        val previousDutyAccountNumber = request.userAnswers.get(DefermentAccountPage).getOrElse(dan.accountNumber)
        val previousDutyAccountType   = request.userAnswers.get(DefermentTypePage).getOrElse(dan.danType)
        if (dan.accountNumber != previousDutyAccountNumber || dan.danType != previousDutyAccountType) {

          val userAnswers = request.userAnswers.removeMany(
            Seq(
              AdditionalDefermentNumberPage,
              AdditionalDefermentTypePage,
              UploadAuthorityPage
            )
          )
          for {
            updatedAnswers <- Future.successful(userAnswers)
            updatedAnswers <- Future.fromTry(updatedAnswers.set(CheckModePage, false))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(DefermentTypePage, dan.danType))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(DefermentAccountPage, dan.accountNumber))
            _              <- sessionRepository.set(updatedAnswers)
          } yield dan.danType match {
            case "A" | "C" => Redirect(controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad())
            case _         => Redirect(controllers.paymentInfo.routes.UploadAuthorityController.onLoad(Duty))
          }
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DefermentTypePage, dan.danType))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(DefermentAccountPage, dan.accountNumber))
            _              <- sessionRepository.set(updatedAnswers)
          } yield {
            if (request.checkMode) {
              Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
            } else {
              dan.danType match {
                case "A" | "C" => Redirect(controllers.paymentInfo.routes.RepresentativeDanImportVATController.onLoad())
                case _ =>
                  Redirect(controllers.paymentInfo.routes.UploadAuthorityController.onLoad(Duty))
              }
            }
          }
        }
      }
    )
  }

  private[controllers] def backLink(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.paymentInfo.routes.SplitPaymentController.onLoad()
    }
  }

}
