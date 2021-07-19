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

import config.ErrorHandler
import controllers.actions._
import javax.inject.{Inject, Singleton}
import pages.DisclosureReferenceNumberPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DisclosureClosedView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DisclosureClosedController @Inject()(identify: IdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           mcc: MessagesControllerComponents,
                                           sessionRepository: SessionRepository,
                                           view: DisclosureClosedView,
                                           errorHandler: ErrorHandler,
                                           implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.get(DisclosureReferenceNumberPage) match {
      case Some(caseId) => sessionRepository.remove(request.credId).map(_ => Ok(view(caseId)))
      case _ => Future.successful(errorHandler.showInternalServerError)
    }
  }

}
