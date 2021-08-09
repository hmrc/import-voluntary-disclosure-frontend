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

package controllers.reasons

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.reasons.UnderpaymentReasonAmendmentFormProvider
import models.reasons.UnderpaymentReasonValue
import pages.reasons.ChangeUnderpaymentReasonPage
import play.api.data.{Form, FormError}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.reasons.{CurrencyAmendmentView, TextAmendmentView, WeightAmendmentView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChangeUnderpaymentReasonDetailsController @Inject()(identify: IdentifierAction,
                                                          getData: DataRetrievalAction,
                                                          requireData: DataRequiredAction,
                                                          sessionRepository: SessionRepository,
                                                          mcc: MessagesControllerComponents,
                                                          formProvider: UnderpaymentReasonAmendmentFormProvider,
                                                          textAmendmentView: TextAmendmentView,
                                                          weightAmendmentView: WeightAmendmentView,
                                                          currencyAmendmentView: CurrencyAmendmentView,
                                                          implicit val ec: ExecutionContext
                                                         )
  extends FrontendController(mcc) with I18nSupport {

  private def formAction(boxNumber: Int): Call = controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onSubmit(boxNumber)

  def onLoad(boxNumber: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val itemNumber = request.userAnswers.get(ChangeUnderpaymentReasonPage).fold(0) { reason =>
      reason.changed.itemNumber
    }
    val form = request.userAnswers.get(ChangeUnderpaymentReasonPage).fold(formProvider(boxNumber)) { reason =>
      formProvider(boxNumber).fill(UnderpaymentReasonValue(reason.changed.original, reason.changed.amended))
    }
    Future.successful(Ok(routeToView(boxNumber, itemNumber, form)))
  }

  def onSubmit(boxNumber: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val itemNumber = request.userAnswers.get(ChangeUnderpaymentReasonPage).fold(0) { reason =>
      reason.changed.itemNumber
    }
    formProvider(boxNumber).bindFromRequest().fold(
      formWithErrors => {
        val newErrors = formWithErrors.errors.map { error =>
          if (error.key.isEmpty) {
            FormError("amended", error.message)
          } else {
            error
          }
        }
        Future.successful(BadRequest(routeToView(boxNumber, itemNumber, formWithErrors.copy(errors = newErrors))))
      },
      value => {
        request.userAnswers.get(ChangeUnderpaymentReasonPage) match {
          case Some(data) =>
            val changed = data.changed.copy(original = value.original, amended = value.amended)
            val reason = data.copy(changed = changed)
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ChangeUnderpaymentReasonPage, reason))
              _ <- sessionRepository.set(updatedAnswers)
            } yield {
              Redirect(controllers.reasons.routes.ConfirmChangeReasonDetailController.onLoad())
            }
          case _ => Future.successful(InternalServerError("Changed item number not found"))
        }
      }
    )
  }

  private[controllers] def backLink(boxNumber: Int): Option[Call] = {
    boxNumber match {
      case 33 | 34 | 35 | 36 | 37 | 38 | 39 | 41 | 42 | 43 | 45 | 46 => None
      case _ => Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad())
    }
  }

  private[controllers] def routeToView(boxNumber: Int, itemNumber: Int, form: Form[_])(implicit request: Request[_]) = {
    boxNumber match {
      case 22 | 37 | 39 | 41 | 42 | 62 | 63 | 66 | 67 | 68 => textAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber))
      case 33 => textAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber), inputClass = Some("govuk-input--width-20"))
      case 34 | 36 | 43 => textAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber), inputClass = Some("govuk-input--width-3"))
      case 35 | 38 => weightAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber))
      case 45 => textAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber), inputClass = Some("govuk-input--width-4"))
      case 46 => currencyAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber))
      // TODO: update with an appropriate view for Other Reason
      case 99 => textAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber))
      case _ => throw new RuntimeException("Invalid Box Number")
    }
  }

}
