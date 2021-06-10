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
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.RepresentativeDanImportVATView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RepresentativeDanImportVATController @Inject()(identify: IdentifierAction,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     sessionRepository: SessionRepository,
                                                     mcc: MessagesControllerComponents,
                                                     view: RepresentativeDanImportVATView,
                                                     formProvider: RepresentativeDanFormProvider
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
        val previousVATAccountNumber = request.userAnswers.get(AdditionalDefermentNumberPage).getOrElse(dan.accountNumber)
        val previousVATAccountType = request.userAnswers.get(AdditionalDefermentTypePage).getOrElse(dan.danType)
        if (dan.accountNumber != previousVATAccountNumber || dan.danType != previousVATAccountType) {

          val authorityFiles = request.userAnswers.get(UploadAuthorityPage).getOrElse(Seq.empty).filterNot(_.dutyType == Vat)

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(UploadAuthorityPage, authorityFiles))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(CheckModePage, false))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentTypePage, dan.danType))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentNumberPage, dan.accountNumber))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            dan.danType match {
              case "A" | "C" => Redirect(controllers.routes.CheckYourAnswersController.onLoad())
              case _ => Redirect(controllers.routes.UploadAuthorityController.onLoad(Vat, dan.accountNumber))
            }
          }
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AdditionalDefermentTypePage, dan.danType))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(AdditionalDefermentNumberPage, dan.accountNumber))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            if (request.checkMode) {
              Redirect(controllers.routes.CheckYourAnswersController.onLoad())
            } else {
              dan.danType match {
                case "A" | "C" => Redirect(controllers.routes.CheckYourAnswersController.onLoad())
                case _ => Redirect(controllers.routes.UploadAuthorityController.onLoad(Vat, dan.accountNumber))
              }
            }
          }
        }
      }
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.routes.RepresentativeDanDutyController.onLoad()
    }
  }

}
