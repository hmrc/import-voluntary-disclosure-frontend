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

package controllers.serviceEntry

import com.google.inject.Inject
import config.AppConfig
import controllers.IVDFrontendController
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.{EoriDetails, UserAnswers}
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.Logger
import play.api.i18n.Messages
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.EoriDetailsService
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import views.html.errors.ConfirmEoriDetailsErrorView
import views.html.serviceEntry.ConfirmEORIDetailsView

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmEORIDetailsController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  mcc: MessagesControllerComponents,
  sessionRepository: SessionRepository,
  eoriDetailsService: EoriDetailsService,
  view: ConfirmEORIDetailsView,
  errorView: ConfirmEoriDetailsErrorView,
  implicit val appConfig: AppConfig,
  implicit val ec: ExecutionContext
) extends IVDFrontendController(mcc) {

  private val logger = Logger("application." + getClass.getCanonicalName)

  def onLoad(): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.credId))

    userAnswers.get(KnownEoriDetailsPage) match {
      case Some(eoriDetails) => Future.successful(Ok(view(summaryList(eoriDetails))))
      case _ =>
        eoriDetailsService.retrieveEoriDetails(request.eori).flatMap {
          case Right(eoriDetails) =>
            for {
              updatedAnswers <- Future.fromTry(userAnswers.set(KnownEoriDetailsPage, eoriDetails))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Ok(view(summaryList(eoriDetails)))
          case Left(error) =>
            logger.error(error.message + " " + error.status)
            Future.successful(InternalServerError(errorView()))
        }
    }

  }

  private[serviceEntry] def summaryList(eoriDetails: EoriDetails)(implicit messages: Messages): SummaryList = {

    def rowItem(message: String, value: String) = SummaryListRow(
      key = Key(content = Text(messages(message)), classes = "govuk-summary-list__key govuk-!-width-one-half"),
      value = Value(content = Text(value))
    )

    val eoriNumberSummaryListRow: SummaryListRow = rowItem("confirmEORI.eoriNumber", eoriDetails.eori)
    val nameSummaryListRow: SummaryListRow       = rowItem("confirmEORI.name", eoriDetails.name)
    val vatNumberSummaryListRow: SummaryListRow = eoriDetails.vatNumber match {
      case Some(vatNumber) => rowItem("confirmEORI.vatNumber", vatNumber)
      case None            => rowItem("confirmEORI.vatNumber", messages("confirmEORI.vatNumberNotPresent"))
    }

    SummaryList(Seq(eoriNumberSummaryListRow, nameSummaryListRow, vatNumberSummaryListRow))

  }

}
