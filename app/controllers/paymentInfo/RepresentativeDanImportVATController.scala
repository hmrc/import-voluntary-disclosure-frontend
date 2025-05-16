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
import models.SelectedDutyTypes.Vat
import models.requests.DataRequest
import pages._
import pages.paymentInfo.{AdditionalDefermentNumberPage, AdditionalDefermentTypePage, DefermentAccountPage, UploadAuthorityPage}
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.data.FormError
import play.api.mvc._
import repositories.SessionRepository
import views.html.paymentInfo.RepresentativeDanImportVATView

import scala.concurrent.{ExecutionContext, Future}

class RepresentativeDanImportVATController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  errorHandler: ErrorHandler,
  view: RepresentativeDanImportVATView,
  formProvider: RepresentativeDanFormProvider,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val repName = request.userAnswers.get(KnownEoriDetailsPage).get.name
    val form = (for {
      danType       <- request.userAnswers.get(AdditionalDefermentTypePage)
      accountNumber <- request.userAnswers.get(AdditionalDefermentNumberPage)
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
        val dutyAccountNumberIsSame = request.userAnswers.get(DefermentAccountPage).contains(dan.accountNumber)
        if (dutyAccountNumberIsSame) {
          val form = formProvider().fill(RepresentativeDan(dan.accountNumber, dan.danType))
            .withError(FormError("accountNumber", "repDan.error.input.sameAccountNumber"))
          val res = request.getImporterName.fold(errorHandler.showInternalServerError) { name =>
            Ok(view(form, name, repName, backLink))
          }
          Future.successful(res)
        } else {

          if (previousVATData(dan.accountNumber, dan.danType)) {

            val authorityFiles =
              request.userAnswers.get(UploadAuthorityPage).getOrElse(Seq.empty).filterNot(_.dutyType == Vat)

            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(UploadAuthorityPage, authorityFiles))
              updatedAnswers <- Future.fromTry(updatedAnswers.set(CheckModePage, false))
              updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentTypePage, dan.danType))
              updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentNumberPage, dan.accountNumber))
              _              <- sessionRepository.set(updatedAnswers)
            } yield danTypeRedirect(dan.accountNumber, dan.danType)
          } else {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AdditionalDefermentTypePage, dan.danType))
              updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentNumberPage, dan.accountNumber))
              _              <- sessionRepository.set(updatedAnswers)
            } yield {
              if (request.checkMode) {
                Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
              } else {
                danTypeRedirect(dan.accountNumber, dan.danType)
              }
            }
          }
        }
      }
    )
  }

  private[controllers] def previousVATData(accountNumber: String, danType: String)(implicit
    request: DataRequest[_]
  ): Boolean = {
    val previousVATAccountNumber = request.userAnswers.get(AdditionalDefermentNumberPage).getOrElse(accountNumber)
    val previousVATAccountType   = request.userAnswers.get(AdditionalDefermentTypePage).getOrElse(danType)
    if (accountNumber != previousVATAccountNumber || danType != previousVATAccountType) true else false
  }

  private[controllers] def danTypeRedirect(accountNumber: String, accountType: String): Result =
    accountType match {
      case "A" | "C" => Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
      case _         => Redirect(controllers.paymentInfo.routes.UploadAuthorityController.onLoad(Vat))
    }

  private[controllers] def backLink(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.paymentInfo.routes.RepresentativeDanDutyController.onLoad()
    }
  }

}
