/*
 * Copyright 2023 HM Revenue & Customs
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

package services

import config.AppConfig
import connectors.IvdSubmissionConnector
import models.audit.EoriDetailsAuditEvent
import models.requests.OptionalDataRequest
import models.{EoriDetails, ErrorModel}
import play.api.i18n.MessagesApi
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EoriDetailsService @Inject() (
  ivdSubmissionConnector: IvdSubmissionConnector,
  auditService: AuditService,
  implicit val messagesApi: MessagesApi,
  implicit val appConfig: AppConfig
) {

  def retrieveEoriDetails(eori: String)(implicit
    req: OptionalDataRequest[_],
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[ErrorModel, EoriDetails]] = {
    ivdSubmissionConnector.getEoriDetails(eori).map {
      case Left(err) => Left(err)
      case Right(value) =>
        auditService.audit(EoriDetailsAuditEvent(eori, req.credId))
        Right(value)
    }
  }

}
