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

package services

import connectors.IvdSubmissionConnector
import models._
import models.audit.UpdateCaseAuditEvent
import models.requests.DataRequest
import play.api.Logger
import play.api.libs.json._
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateCaseService @Inject()(ivdSubmissionConnector: IvdSubmissionConnector,
                                  auditService: AuditService) {
  private val logger = Logger("application." + getClass.getCanonicalName)

  def updateCase()(implicit request: DataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[UpdateCaseError, UpdateCaseResponse]] = {
    Json.fromJson[UpdateCaseData](request.userAnswers.data) match {
      case JsSuccess(data, _) =>
        ivdSubmissionConnector.updateCase(buildUpdate(data)).map {
          case Right(confirmationResponse) =>
            auditService.audit(UpdateCaseAuditEvent(data, request.credId, request.eori))
            Right(confirmationResponse)
          case Left(errorResponse) => Left(errorResponse)
        }
      case JsError(err) =>
        logger.error(s"Invalid User Answers data. Failed to parse into UpdateCase model. Error: ${err}")
        Future.successful(Left(UpdateCaseError.UnexpectedError(-1, Some(err.toString()))))
    }
  }

  private[services] def buildUpdate(update: UpdateCaseData): JsValue = {
    Json.obj(
      "caseId" -> update.caseId,
      "additionalInfo" -> update.additionalInfo,
      "supportingDocuments" -> update.supportingDocuments
    )
  }
}
