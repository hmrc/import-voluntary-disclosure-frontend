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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import javax.inject.Singleton
import models.EoriDetails
import pages.KnownEoriDetails
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.EoriDetailsService
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.ConfirmEORIDetailsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ConfirmEORIDetailsController @Inject()(identify: IdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             mcc: MessagesControllerComponents,
                                             sessionRepository: SessionRepository,
                                             eoriDetailsService: EoriDetailsService,
                                             view: ConfirmEORIDetailsView
                                            )
  extends FrontendController(mcc) with I18nSupport {

  private val logger = Logger("application." + getClass.getCanonicalName)


  def onLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    val getEoriDetails = request.userAnswers.get(KnownEoriDetails)
    if (getEoriDetails.isEmpty) {
      eoriDetailsService.retrieveEoriDetails(request.eori).flatMap {
        case Right(eoriDetails) =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(KnownEoriDetails, eoriDetails))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Ok(view(summaryList(eoriDetails).getOrElse(Seq.empty)))
        case Left(error) =>
          logger.error(error.message + " " + error.status)
          Future.successful(NotFound(error.message + " " + error.status))
      }
    } else {
      Future.successful(Ok(view(summaryList(getEoriDetails.get).getOrElse(Seq.empty))))
    }
  }


  def summaryList(eoriDetails: EoriDetails)(implicit messages: Messages): Option[Seq[SummaryList]] = {

    val eoriNumberSummaryListRow: Option[Seq[SummaryListRow]] = Some(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmEORI.eoriNumber")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(eoriDetails.eori)
          )
        )
      ))


    val nameSummaryListRow: Option[Seq[SummaryListRow]] = Some(
      Seq(
        SummaryListRow(
          key = Key(
            content = Text(messages("confirmEORI.name")),
            classes = "govuk-!-width-two-thirds"
          ),
          value = Value(
            content = HtmlContent(eoriDetails.name)
          )
        )
      ))

    val rows = eoriNumberSummaryListRow.getOrElse(Seq.empty) ++
      nameSummaryListRow.getOrElse(Seq.empty)
    if (rows.nonEmpty) {
      Some(Seq(SummaryList(rows)))
    } else {
      None
    }

  }

}


