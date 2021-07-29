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
import forms.RepresentativeDanFormProvider
import models.RepresentativeDan
import models.SelectedDutyTypes.Vat
import models.requests.DataRequest
import pages._
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.RepresentativeDanImportVATView

import scala.concurrent.{ExecutionContext, Future}

class RepresentativeDanImportVATController @Inject()(identify: IdentifierAction,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     sessionRepository: SessionRepository,
                                                     mcc: MessagesControllerComponents,
                                                     view: RepresentativeDanImportVATView,
                                                     formProvider: RepresentativeDanFormProvider,
                                                     implicit val ec: ExecutionContext
                                                    )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = (for {
      danType <- request.userAnswers.get(AdditionalDefermentTypePage)
      accountNumber <- request.userAnswers.get(AdditionalDefermentNumberPage)
    } yield {
      formProvider().fill(RepresentativeDan(accountNumber, danType))
    }).getOrElse(formProvider())

    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,
        backLink
      ))),
      dan => {
        val dutyAccountNumberIsSame = request.userAnswers.get(DefermentAccountPage).contains(dan.accountNumber)
        if (dutyAccountNumberIsSame) {
          val form = formProvider().fill(RepresentativeDan(dan.accountNumber, dan.danType))
            .withError(FormError("accountNumber", "repDan.error.input.sameAccountNumber"))
          Future.successful(Ok(view(form, backLink)))
        } else {

          if (previousVATData(dan.accountNumber, dan.danType)) {

            val authorityFiles = request.userAnswers.get(UploadAuthorityPage).getOrElse(Seq.empty).filterNot(_.dutyType == Vat)

            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(UploadAuthorityPage, authorityFiles))
              updatedAnswers <- Future.fromTry(updatedAnswers.set(CheckModePage, false))
              updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentTypePage, dan.danType))
              updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentNumberPage, dan.accountNumber))
              _ <- sessionRepository.set(updatedAnswers)
            } yield {
              danTypeRedirect(dan.accountNumber, dan.danType)
            }
          } else {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AdditionalDefermentTypePage, dan.danType))
              updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentNumberPage, dan.accountNumber))
              _ <- sessionRepository.set(updatedAnswers)
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

  private[controllers] def previousVATData(accountNumber: String, danType: String)(implicit request: DataRequest[_]): Boolean = {
    val previousVATAccountNumber = request.userAnswers.get(AdditionalDefermentNumberPage).getOrElse(accountNumber)
    val previousVATAccountType = request.userAnswers.get(AdditionalDefermentTypePage).getOrElse(danType)
    if (accountNumber != previousVATAccountNumber || danType != previousVATAccountType) true else false
  }

  private[controllers] def danTypeRedirect(accountNumber: String, accountType: String): Result = {
    accountType match {
      case "A" | "C" => Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
      case _ => Redirect(controllers.routes.UploadAuthorityController.onLoad(Vat, accountNumber))
    }
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.routes.RepresentativeDanDutyController.onLoad()
    }
  }

}
