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

import config.ErrorHandler
import controllers.actions._
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.reasons.CannotDiscloseImportVATView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CannotDiscloseImportVATController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  mcc: MessagesControllerComponents,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  errorHandler: ErrorHandler,
  view: CannotDiscloseImportVATView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  private val logger = Logger("application." + getClass.getCanonicalName)

  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.getImporterName match {
      case Some(nameOfImporter) =>
        sessionRepository.remove(request.credId).map(_ => Ok(view(request.isRepFlow, nameOfImporter)))
      case None =>
        logger.error("Failed to find Importer Name")
        Future.successful(errorHandler.showInternalServerError)
    }

  }

}
