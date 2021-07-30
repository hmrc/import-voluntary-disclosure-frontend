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
import controllers.actions._
import models.UserAnswers
import pages.FileUploadPage
import pages.reasons.HasFurtherInformationPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.SupportingDocView

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class SupportingDocController @Inject()(identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        mcc: MessagesControllerComponents,
                                        requireData: DataRequiredAction,
                                        view: SupportingDocView,
                                        implicit val appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    if (request.userAnswers.get(FileUploadPage).getOrElse(Seq.empty).nonEmpty) {
      Future.successful(Redirect(controllers.routes.UploadAnotherFileController.onLoad()))
    } else {
      Future.successful(Ok(view(backLink(request.userAnswers))))
    }
  }

  private[controllers] def backLink(userAnswers: UserAnswers): Call = {
    userAnswers.get(HasFurtherInformationPage) match {
      case Some(value) if value => controllers.reasons.routes.MoreInformationController.onLoad()
      case _ => controllers.reasons.routes.HasFurtherInformationController.onLoad()
    }
  }
}
