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

package controllers.cancelCase

import controllers.actions._
import forms.cancelCase.AnyOtherSupportingCancellationDocsFormProvider
import models.requests.DataRequest
import pages.shared.AnyOtherSupportingDocsPage
import pages.updateCase.UploadSupportingDocumentationPage
import play.api.i18n.I18nSupport
import play.api.libs.json.Format.GenericFormat
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.cancelCase.AnyOtherSupportingCancellationDocsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AnyOtherSupportingCancellationDocsController @Inject()(identify: IdentifierAction,
                                                             getData: DataRetrievalAction,
                                                             requireData: DataRequiredAction,
                                                             sessionRepository: SessionRepository,
                                                             mcc: MessagesControllerComponents,
                                                             formProvider: AnyOtherSupportingCancellationDocsFormProvider,
                                                             view: AnyOtherSupportingCancellationDocsView,
                                                             implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {


  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val form = request.userAnswers.get(AnyOtherSupportingDocsPage).fold(formProvider()) {
      formProvider().fill
    }
    Future.successful(Ok(view(form, backLink)))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(view(formWithErrors, backLink))),
      value =>
        for {
          answersWithSupportingDocumentation <- Future.fromTry(request.userAnswers.set(AnyOtherSupportingDocsPage, value))
          answersWithUpdatedFiles <-
            if (!value) Future.fromTry(answersWithSupportingDocumentation.remove(UploadSupportingDocumentationPage))
            else Future.successful(answersWithSupportingDocumentation)
          existingAnswers = request.userAnswers.get(AnyOtherSupportingDocsPage)
          _ <- sessionRepository.set(answersWithUpdatedFiles)
        } yield {
          val hasNotChanged = existingAnswers.contains(value)
          if (request.checkMode && (!value || hasNotChanged)) {
            Redirect(controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad())
          } else if (value) {
            Redirect(controllers.cancelCase.routes.CancelCaseUploadSupportingDocumentationController.onLoad())
          } else {
            Redirect(controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad())
          }
        }
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cancelCase.routes.CancelCaseCheckYourAnswersController.onLoad()
    } else {
      controllers.cancelCase.routes.CancellationReasonController.onLoad()
    }
  }
}
