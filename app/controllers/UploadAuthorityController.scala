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

import config.AppConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.SelectedDutyTypes._
import models.upscan._
import models.{UploadAuthority, UserAnswers}
import pages.{SplitPaymentPage, UploadAuthorityPage}
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.{FileUploadRepository, SessionRepository}
import services.UpScanService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{UploadAuthorityProgressView, UploadAuthoritySuccessView, UploadAuthorityView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

@Singleton
class UploadAuthorityController @Inject()(identify: IdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          mcc: MessagesControllerComponents,
                                          val fileUploadRepository: FileUploadRepository,
                                          val sessionRepository: SessionRepository,
                                          upScanService: UpScanService,
                                          view: UploadAuthorityView,
                                          progressView: UploadAuthorityProgressView,
                                          successView: UploadAuthoritySuccessView,
                                          implicit val appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with FileUploadHandler[UploadAuthority] {

  private[controllers] def backLink(currentDutyType: SelectedDutyType, dan: String, selectedDutyTypes: SelectedDutyType, splitPayment: Boolean): Call = {
    selectedDutyTypes match {
      case Both if splitPayment && currentDutyType == Duty => controllers.routes.RepresentativeDanDutyController.onLoad()
      case Both if splitPayment && currentDutyType == Vat => controllers.routes.RepresentativeDanImportVATController.onLoad()
      case _ => controllers.routes.RepresentativeDanController.onLoad()
    }
  }

  def onLoad(dutyType: SelectedDutyType, dan: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val splitPayment = request.userAnswers.get(SplitPaymentPage).getOrElse(false)
    val dutyTypeKey = dutyType match {
      case Vat => "uploadAuthority.vat"
      case Duty => "uploadAuthority.duty"
      case Both => "uploadAuthority.both"
      case _ => "Underpayment Type not found"
    }

    upScanService.initiateAuthorityJourney(dutyType, dan).map { response =>
      Ok(view(response, backLink(dutyType, dan, request.dutyType, splitPayment), dan, dutyTypeKey))
        .removingFromSession("AuthorityUpscanReference")
        .addingToSession("AuthorityUpscanReference" -> response.reference.value)
    }
  }

  def upscanResponseHandler(dutyType: SelectedDutyType,
                            dan: String,
                            key: Option[String] = None,
                            errorCode: Option[String] = None,
                            errorMessage: Option[String] = None,
                            errorResource: Option[String] = None,
                            errorRequestId: Option[String] = None
                           ): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val upscanError = buildUpscanError(errorCode, errorMessage, errorResource, errorRequestId)
    val errorRoute = Redirect(controllers.routes.UploadAuthorityController.onLoad(dutyType, dan))
    val successRoute = Redirect(controllers.routes.UploadAuthorityController.uploadProgress(dutyType, dan, key.getOrElse("this will never be used")))

    handleUpscanResponse(key, upscanError, successRoute, errorRoute)
  }

  def uploadProgress(dutyType: SelectedDutyType,
                     dan: String,
                     key: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val uploadCompleteRoute = Redirect(controllers.routes.UploadAuthorityController.onSuccess(dutyType, dan))
    val uploadFailedRoute = Redirect(controllers.routes.UploadAuthorityController.onLoad(dutyType, dan))
    val uploadInProgressRoute = Ok(
      progressView(
        key = key,
        backLink = controllers.routes.UploadAuthorityController.onLoad(dutyType, dan),
        action = controllers.routes.UploadAuthorityController.uploadProgress(dutyType, dan, key).url
      )
    )
    val updateFilesList: FileUpload => Seq[UploadAuthority] = { file =>
      val upload = extractFileDetails(file, key)
      val newAuthority: UploadAuthority = UploadAuthority(dan, dutyType, upload)
      request.userAnswers.get(UploadAuthorityPage).getOrElse(Seq.empty).filterNot(_.dutyType == dutyType) :+ newAuthority
    }
    val saveFilesList: Seq[UploadAuthority] => Try[UserAnswers] = { list =>
      request.userAnswers.set(UploadAuthorityPage, list)(UploadAuthorityPage.queryWrites)
    }

    handleUpscanFileProcessing(key,
      uploadCompleteRoute,
      uploadInProgressRoute,
      uploadFailedRoute,
      updateFilesList,
      saveFilesList
    )
  }

  def onSuccess(dutyType: SelectedDutyType, dan: String): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val splitPayment = request.userAnswers.get(SplitPaymentPage).getOrElse(false)

    val action = request.dutyType match {
      case Both if splitPayment && dutyType == Duty => controllers.routes.RepresentativeDanImportVATController.onLoad().url
      case _ => controllers.routes.CheckYourAnswersController.onLoad().url
    }

    Future.successful(
      Ok(successView(
        fileName = request.session.get("AuthorityFilename").getOrElse("No filename"),
        action = action)
      ).removingFromSession("AuthorityFilename")
    )
  }

}
