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
import models.requests.DataRequest
import models.{RepresentativeDan, UserAnswers}
import pages._
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.RepresentativeDanView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RepresentativeDanController @Inject()(identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            sessionRepository: SessionRepository,
                                            mcc: MessagesControllerComponents,
                                            view: RepresentativeDanView,
                                            formProvider: RepresentativeDanFormProvider
                                           )
  extends FrontendController(mcc) with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = (for {
      danType <- request.userAnswers.get(DefermentTypePage)
      accountNumber <- request.userAnswers.get(DefermentAccountPage)
    } yield {
      formProvider().fill(RepresentativeDan(accountNumber, danType))
    }).getOrElse(formProvider())

    Future.successful(Ok(view(form, backLink(request.userAnswers))))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors,
        backLink(request.userAnswers)
      ))),
      dan => {
        val previousAccountNumber = request.userAnswers.get(DefermentAccountPage).getOrElse(dan.accountNumber)
        val previousAccountType = request.userAnswers.get(DefermentTypePage).getOrElse(dan.danType)
        if (dan.accountNumber != previousAccountNumber || dan.danType != previousAccountType) {
          val userAnswers = request.userAnswers.removeMany(Seq(
            DefermentTypePage,
            DefermentAccountPage,
            AdditionalDefermentTypePage,
            AdditionalDefermentNumberPage,
            UploadAuthorityPage))
          for {
            otherUpdatedAnswers <- Future.successful(userAnswers)
            checkMode <- Future.fromTry(otherUpdatedAnswers.set(CheckModePage, false))
            updatedAnswers <- Future.fromTry(checkMode.set(DefermentTypePage, dan.danType))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(DefermentAccountPage, dan.accountNumber))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            dan.danType match {
              case "A" | "C" => Redirect(controllers.routes.CheckYourAnswersController.onLoad())
              case _ => Redirect(controllers.routes.UploadAuthorityController.onLoad(request.dutyType, dan.accountNumber))
            }
          }
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DefermentTypePage, dan.danType))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(DefermentAccountPage, dan.accountNumber))
            _ <- sessionRepository.set(updatedAnswers)
          } yield {
            dan.danType match {
              case "A" | "C" =>
                Redirect(controllers.routes.CheckYourAnswersController.onLoad())
              case _ => if (!request.checkMode) {
                Redirect(controllers.routes.UploadAuthorityController.onLoad(request.dutyType, dan.accountNumber))
              } else {
                Redirect(controllers.routes.CheckYourAnswersController.onLoad())
              }
            }
          }
        }
      }
    )
  }

  private[controllers] def backLink(userAnswers: UserAnswers)(implicit request: DataRequest[_]): Call = {

    if (request.checkMode) {
      controllers.routes.CheckYourAnswersController.onLoad()
    } else {
      if (userAnswers.get(SplitPaymentPage).isDefined) {
        controllers.routes.SplitPaymentController.onLoad()
      } else {
        controllers.routes.DefermentController.onLoad()
      }
    }
  }

}
