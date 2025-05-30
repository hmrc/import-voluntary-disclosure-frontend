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

package models.audit

import models.requests.DataRequest
import play.api.libs.json._
import services.JsonAuditModel

case class UpdateCaseAuditEvent(updateCaseData: JsValue)(implicit request: DataRequest[_]) extends JsonAuditModel {
  override val auditType: String       = "UpdateCase"
  override val transactionName: String = "update-case"
  override val detail: JsValue = Json.obj(
    "eori"         -> request.eori,
    "credentialId" -> request.credId
  ) ++ Json.toJson(updateCaseData).asInstanceOf[JsObject]
}
