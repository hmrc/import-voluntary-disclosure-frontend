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

package controllers.reasons

import controllers.IVDFrontendController
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.reasons.UnderpaymentReasonAmendmentFormProvider
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, UnderpaymentReasonValue}
import pages.reasons.ChangeUnderpaymentReasonPage
import play.api.data.{Form, FormError}
import play.api.mvc._
import repositories.SessionRepository
import views.html.reasons._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChangeUnderpaymentReasonDetailsController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  formProvider: UnderpaymentReasonAmendmentFormProvider,
  textAmendmentView: TextAmendmentView,
  weightAmendmentView: WeightAmendmentView,
  currencyAmendmentView: CurrencyAmendmentView,
  otherReasonAmendmentView: OtherReasonAmendmentView,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  private def formAction(boxNumber: BoxNumber): Call =
    controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onSubmit(boxNumber.id)

  def onLoad(boxNumberId: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val boxNumber = BoxNumber.fromInt(boxNumberId)
      val itemNumber = request.userAnswers.get(ChangeUnderpaymentReasonPage).fold(0) { reason =>
        reason.changed.itemNumber
      }
      val form = request.userAnswers.get(ChangeUnderpaymentReasonPage).fold(formProvider(boxNumber)) { reason =>
        formProvider(boxNumber).fill(UnderpaymentReasonValue(reason.changed.original, reason.changed.amended))
      }
      Future.successful(Ok(routeToView(boxNumber, itemNumber, form)))
  }

  def onSubmit(boxNumberId: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val boxNumber = BoxNumber.fromInt(boxNumberId)
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
              val reason  = data.copy(changed = changed)
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ChangeUnderpaymentReasonPage, reason))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(controllers.reasons.routes.ConfirmChangeReasonDetailController.onLoad())
            case _ => Future.successful(InternalServerError("Changed item number not found"))
          }
        }
      )
  }

  private[controllers] def backLink(boxNumber: BoxNumber): Option[Call] = {
    boxNumber match {
      case BoxNumber.Box33 | BoxNumber.Box34 | BoxNumber.Box35 | BoxNumber.Box36 | BoxNumber.Box37 | BoxNumber.Box38 |
          BoxNumber.Box39 | BoxNumber.Box41 | BoxNumber.Box42 | BoxNumber.Box43 | BoxNumber.Box45 | BoxNumber.Box46 =>
        None
      case _ => Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad())
    }
  }

  private[controllers] def routeToView(boxNumber: BoxNumber, itemNumber: Int, form: Form[_])(implicit
    request: Request[_]
  ) = {
    boxNumber match {
      case BoxNumber.Box22 | BoxNumber.Box37 | BoxNumber.Box39 | BoxNumber.Box41 | BoxNumber.Box42 | BoxNumber.Box62 |
          BoxNumber.Box63 | BoxNumber.Box66 | BoxNumber.Box67 | BoxNumber.Box68 =>
        textAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber))
      case BoxNumber.Box33 =>
        textAmendmentView(
          form,
          formAction(boxNumber),
          boxNumber,
          itemNumber,
          backLink(boxNumber),
          inputClass = Some("govuk-input--width-20")
        )
      case BoxNumber.Box34 | BoxNumber.Box36 | BoxNumber.Box43 =>
        textAmendmentView(
          form,
          formAction(boxNumber),
          boxNumber,
          itemNumber,
          backLink(boxNumber),
          inputClass = Some("govuk-input--width-3")
        )
      case BoxNumber.Box35 | BoxNumber.Box38 =>
        weightAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber))
      case BoxNumber.Box45 =>
        textAmendmentView(
          form,
          formAction(boxNumber),
          boxNumber,
          itemNumber,
          backLink(boxNumber),
          inputClass = Some("govuk-input--width-4")
        )
      case BoxNumber.Box46 =>
        currencyAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber))
      case BoxNumber.OtherItem =>
        otherReasonAmendmentView(form, formAction(boxNumber), boxNumber, itemNumber, backLink(boxNumber))
      case _ => throw new RuntimeException(s"Invalid Box Number: ${boxNumber.id}")
    }
  }

}
