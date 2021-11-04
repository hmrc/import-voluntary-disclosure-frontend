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

package controllers.updateCase

import controllers.actions._
import forms.shared.UploadAnotherFileFormProvider
import models.requests.DataRequest
import models.{FileUploadInfo, Index}
import pages.updateCase.UploadSupportingDocumentationPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.ActionItemHelper
import views.html.updateCase.UploadSupportingDocumentationSummaryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UploadSupportingDocumentationSummaryController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  mcc: MessagesControllerComponents,
  formProvider: UploadAnotherFileFormProvider,
  view: UploadSupportingDocumentationSummaryView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(UploadSupportingDocumentationPage)
      .fold(Future(Redirect(controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad().url))) {
        files =>
          if (files.isEmpty) {
            Future.successful(Redirect(controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad()))
          } else {
            Future.successful(Ok(view(formProvider(), buildSummaryList(files))))
          }
      }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    formProvider().bindFromRequest().fold(
      formWithErrors => resultWithErrors(formWithErrors),
      addAnotherFile => {
        if (addAnotherFile) {
          Future.successful(Redirect(controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad()))
        } else {
          if (request.checkMode) {
            Future.successful(Redirect(controllers.updateCase.routes.UpdateCaseCheckYourAnswersController.onLoad()))
          } else {
            Future.successful(Redirect(controllers.updateCase.routes.UpdateAdditionalInformationController.onLoad()))
          }
        }
      }
    )
  }

  private def resultWithErrors(
    formWithErrors: Form[Boolean]
  )(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers.get(UploadSupportingDocumentationPage)
      .fold(Future(Redirect(controllers.updateCase.routes.UploadSupportingDocumentationController.onLoad().url))) {
        files =>
          Future.successful(BadRequest(view(formWithErrors, buildSummaryList(files))))
      }

  private def buildSummaryList(files: Seq[FileUploadInfo])(implicit request: DataRequest[AnyContent]) = {
    val summaryListRows = files.zipWithIndex.map { case (file, index) =>
      SummaryListRow(
        key = Key(content = Text(file.fileName), classes = s"govuk-!-width-one-third govuk-!-font-weight-regular".trim),
        actions = Some(
          Actions(
            items = Seq(
              ActionItemHelper.createDeleteActionItem(
                controllers.updateCase.routes.RemoveSupportingDocumentationController.onLoad(Index(index)).url,
                s"Remove ${file.fileName}"
              )
            )
          )
        )
      )
    }
    SummaryList(
      classes = "govuk-!-margin-bottom-9",
      rows = summaryListRows
    )
  }

}
