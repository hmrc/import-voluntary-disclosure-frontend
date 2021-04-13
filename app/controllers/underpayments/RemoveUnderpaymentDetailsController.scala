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

package controllers.underpayments

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.RemoveUnderpaymentReasonFormProvider
import forms.underpayments.RemoveUnderpaymentDetailsFormProvider
import javax.inject.{Inject, Singleton}
import pages.underpayments.{UnderpaymentDetailSummaryPage, UnderpaymentDetailsPage}
import pages.{ChangeUnderpaymentReasonPage, UnderpaymentReasonsPage}
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.underpayments.RemoveUnderpaymentDetailsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class RemoveUnderpaymentDetailsController @Inject()(identify: IdentifierAction,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    sessionRepository: SessionRepository,
                                                    mcc: MessagesControllerComponents,
                                                    formProvider: RemoveUnderpaymentDetailsFormProvider,
                                                    view: RemoveUnderpaymentDetailsView)
  extends FrontendController(mcc) with I18nSupport {



  def onLoad(underpaymentType: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    Future.successful(Ok(view(formProvider(underpaymentType), underpaymentType, backLink(underpaymentType))))
  }

  def onSubmit(underpaymentType: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider(underpaymentType).bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(
          view(
            formWithErrors,
            underpaymentType,
            backLink(underpaymentType))
        )
      ),
      value => {
        if (value) {
          val newReasonsOpt = for {
            allReasons <- request.userAnswers.get(UnderpaymentDetailSummaryPage)
          } yield {
            allReasons.filterNot(x => x.duty == underpaymentType)
          }

          newReasonsOpt match {
            case Some(newReasons) =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(UnderpaymentDetailSummaryPage, newReasons))
                _ <- sessionRepository.set(updatedAnswers)
              } yield {
                if (newReasons.isEmpty) {
                  Redirect(controllers.underpayments.routes.UnderpaymentStartController.onLoad())
                } else {
                  Redirect(controllers.underpayments.routes.UnderpaymentDetailSummaryController.onLoad())
                }
              }
            case _ => Future.successful(InternalServerError("Invalid sequence of reasons"))
          }
        } else {
          Future.successful(Redirect(controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType)))
        }
      }
    )
  }

  def backLink(underpaymentType:String): Call = {
    controllers.underpayments.routes.ChangeUnderpaymentDetailsController.onLoad(underpaymentType)
  }

}


