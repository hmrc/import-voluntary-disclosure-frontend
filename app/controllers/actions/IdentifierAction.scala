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

package controllers.actions

import com.google.inject.Inject
import config.AppConfig
import models.requests.IdentifierRequest
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.{Redirect, Unauthorized}
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.annotation.nowarn
import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction
    extends ActionBuilder[IdentifierRequest, AnyContent]
    with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  unauthorisedView: views.html.errors.UnauthorisedView,
  config: AppConfig,
  val parser: BodyParsers.Default,
  val messagesApi: MessagesApi,
  val http: HttpClient
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions
    with I18nSupport {

  private val logger = Logger("application." + getClass.getCanonicalName)

  private def isValidUser(enrolments: Enrolments) = enrolments.getEnrolment("HMRC-CTS-ORG") match {
    case Some(enrolment) => enrolment.isActivated
    case None            => false
  }

  @nowarn("msg=match may not be exhaustive")
  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    authorised().retrieve(externalId and allEnrolments and affinityGroup) {
      case _ ~ _ ~ Some(AffinityGroup.Agent) =>
        Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorisedAgentAccess()))
      case Some(userId) ~ enrolments ~ _ if isValidUser(enrolments) =>
        val Some(eori) =
          for {
            enrolment  <- enrolments.getEnrolment("HMRC-CTS-ORG")
            identifier <- enrolment.getIdentifier("EORINumber")
          } yield identifier.value
        val req = IdentifierRequest(request, userId, eori)
        block(req)
      case Some(userId) ~ enrolments ~ Some(AffinityGroup.Individual) if !isValidUser(enrolments) =>
        val updatedSession = request.session + ("credId" -> userId)
        Future.successful(
          Redirect(controllers.serviceEntry.routes.CustomsDeclarationController.onLoad()).withSession(updatedSession)
        )
      case Some(userId) ~ enrolments ~ _ if !isValidUser(enrolments) =>
        Future.successful(Redirect(config.eccSubscribeUrl))
      case _ =>
        logger.warn("Unable to retrieve the external ID for the user")
        Future.successful(Unauthorized(unauthorisedView()(request, request2Messages(request))))
    } recover {
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case x: AuthorisationException =>
        logger.warn(s"Authorisation Exception ${x.reason}")
        Unauthorized(unauthorisedView()(request, request2Messages(request)))
    }
  }

}
