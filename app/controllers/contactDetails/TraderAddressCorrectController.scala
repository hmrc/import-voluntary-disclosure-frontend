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

package controllers.contactDetails

import com.google.inject.Inject
import config.ErrorHandler
import controllers.actions._
import forms.contactDetails.TraderAddressCorrectFormProvider
import models.ContactAddress
import models.requests.DataRequest
import pages.contactDetails.{TraderAddressCorrectPage, TraderAddressPage}
import pages.importDetails.ImporterNamePage
import pages.serviceEntry.KnownEoriDetailsPage
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.contactDetails.TraderAddressCorrectView

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TraderAddressCorrectController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  sessionRepository: SessionRepository,
  val errorHandler: ErrorHandler,
  mcc: MessagesControllerComponents,
  formProvider: TraderAddressCorrectFormProvider,
  view: TraderAddressCorrectView,
  implicit val ec: ExecutionContext
) extends FrontendController(mcc)
    with I18nSupport {

  private val logger = Logger("application." + getClass.getCanonicalName)

  def onLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val importerName = request.userAnswers.get(ImporterNamePage)
    request.userAnswers.get(KnownEoriDetailsPage) match {
      case Some(eoriDetails) =>
        val form = request.userAnswers.get(TraderAddressCorrectPage).fold(formProvider(eoriDetails.name)) {
          formProvider(eoriDetails.name).fill
        }
        Future.successful(
          Ok(view(form, eoriDetails.address, eoriDetails.name, importerName, request.isRepFlow, backLink()))
        )
      case None =>
        logger.error("Requested the trader address page without EORI details")
        Future.successful(errorHandler.showInternalServerError)
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val importerName                  = request.userAnswers.get(ImporterNamePage)
    val knownEoriDetailsPage          = request.userAnswers.get(KnownEoriDetailsPage).get
    val traderAddress: ContactAddress = knownEoriDetailsPage.address
    formProvider(knownEoriDetailsPage.name).bindFromRequest().fold(
      formWithErrors =>
        Future.successful(
          BadRequest(
            view(formWithErrors, traderAddress, knownEoriDetailsPage.name, importerName, request.isRepFlow, backLink())
          )
        ),
      value => {
        if (value) {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TraderAddressCorrectPage, value))
            updatedAnswers <- Future.fromTry(updatedAnswers.set(TraderAddressPage, traderAddress))
            _              <- sessionRepository.set(updatedAnswers)
          } yield {
            if (request.checkMode) {
              Redirect(controllers.cya.routes.CheckYourAnswersController.onLoad())
            } else {
              Redirect(controllers.paymentInfo.routes.DefermentController.onLoad())
            }
          }
        } else {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TraderAddressCorrectPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(controllers.contactDetails.routes.AddressLookupController.initialiseJourney())
        }
      }
    )
  }

  private[controllers] def backLink()(implicit request: DataRequest[_]): Call = {
    if (request.checkMode) {
      controllers.cya.routes.CheckYourAnswersController.onLoad()
    } else {
      controllers.contactDetails.routes.DeclarantContactDetailsController.onLoad()
    }
  }

}
