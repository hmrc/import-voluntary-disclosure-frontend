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

package mocks.services

import org.scalamock.scalatest.MockFactory
import play.api.mvc.Request
import services.{AuditService, JsonAuditModel}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait MockAuditService extends MockFactory {

  val mockAuditService: AuditService = mock[AuditService]

  def stubAudit(): Unit =
    (mockAuditService.audit(_: JsonAuditModel)(_: HeaderCarrier, _: ExecutionContext, _: Request[_]))
      .stubs(*, *, *, *)

  def verifyAudit(model: JsonAuditModel): Unit =
    (mockAuditService.audit(_: JsonAuditModel)(_: HeaderCarrier, _: ExecutionContext, _: Request[_]))
      .expects(model, *, *, *)
      .once()

  def verifyNoAudit[T <: JsonAuditModel](): Unit =
    (mockAuditService.audit(_: JsonAuditModel)(_: HeaderCarrier, _: ExecutionContext, _: Request[_]))
      .verify(*, *, *, *)
      .never()
}
