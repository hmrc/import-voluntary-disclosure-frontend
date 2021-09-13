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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.reasons.ItemNumberFormProvider
import models.UserAnswers
import models.reasons.BoxNumber.BoxNumber
import models.reasons.UnderpaymentReason
import pages.reasons.{UnderpaymentReasonBoxNumberPage, UnderpaymentReasonItemNumberPage, UnderpaymentReasonsPage}
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.reasons.ItemNumberView

import scala.concurrent.{ExecutionContext, Future}

class ItemNumberController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  mcc: MessagesControllerComponents,
  view: ItemNumberView,
  formProvider: ItemNumberFormProvider,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  private lazy val formAction: Call = controllers.reasons.routes.ItemNumberController.onSubmit()

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(UnderpaymentReasonItemNumberPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, formAction)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, formAction))),
      submittedItemNumber => {
        request.userAnswers.get(UnderpaymentReasonBoxNumberPage) match {
          case Some(currentBoxNumber) =>
            if (existsSameBoxItem(currentBoxNumber, submittedItemNumber, request.userAnswers)) {
              val form = request.userAnswers.get(UnderpaymentReasonItemNumberPage).fold(formProvider()) {
                formProvider().fill
              }
              Future.successful(
                Ok(
                  view(
                    form.copy(errors = Seq(FormError("itemNumber", "itemNo.error.notTheSameNumber"))),
                    formAction
                  )
                )
              )
            } else {
              for {
                updatedAnswers <- Future.fromTry(
                  request.userAnswers.set(UnderpaymentReasonItemNumberPage, submittedItemNumber)
                )
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(
                controllers.reasons.routes.UnderpaymentReasonAmendmentController.onLoad(currentBoxNumber.id)
              )
            }
          case _ =>
            Future.successful(InternalServerError("Couldn't find current box number"))
        }
      }
    )
  }

  private[controllers] def existsSameBoxItem(
    currentBoxNumber: BoxNumber,
    submittedItemNumber: Int,
    userAnswers: UserAnswers
  ) = {
    lazy val currentUnderpayments: Seq[UnderpaymentReason] =
      userAnswers.get(UnderpaymentReasonsPage).getOrElse(Seq.empty)
    currentUnderpayments.exists(box => box.boxNumber == currentBoxNumber && box.itemNumber == submittedItemNumber)
  }

}
