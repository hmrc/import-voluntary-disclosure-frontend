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
import forms.UploadFileFormProvider
import pages.UploadFilePage
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.UpScanService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.UploadFileView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class UploadFileController @Inject()(identify: IdentifierAction,
                                     getData: DataRetrievalAction,
                                     requireData: DataRequiredAction,
                                     mcc: MessagesControllerComponents,
                                     formProvider: UploadFileFormProvider,
                                     upScanService: UpScanService,
                                     view: UploadFileView,
                                     implicit val appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(key: Option[String] = None,
             errorCode: Option[String] = None,
             errorMessage: Option[String] = None,
             errorResource: Option[String] = None,
             errorRequestId: Option[String] = None
            ): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val form = request.userAnswers.get(UploadFilePage).fold(formProvider()) {
      formProvider().fill
    }

    val updatedForm =
      if (errorMessage.isDefined)
        form.withError("file", errorMessage.get)
      else
        form

    upScanService.initiateNewJourney().map { response =>
      Ok(view(updatedForm, response))
        .addingToSession("UpscanReference" -> response.reference.value)
//        .removingFromSession(SessionKeys.successRedirectForUser) // Why is this necessary
//        .removingFromSession(SessionKeys.errorRedirectForUser) // Why is this necessary
    }
  }


}
