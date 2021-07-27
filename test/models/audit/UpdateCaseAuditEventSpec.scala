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

package models.audit

import java.time.LocalDateTime

import base.SpecBase
import models.{FileUploadInfo, UpdateCaseData}
import play.api.libs.json.Json
import utils.ReusableValues

class UpdateCaseAuditEventSpec extends SpecBase with AuditTestData with ReusableValues {

  val year = 2021
  val month = 7
  val dayOfMonth = 21
  val hour = 11
  val minute = 45
  val second = 36
  val nanoOfSecond = 286

  val updateCaseData: UpdateCaseData = UpdateCaseData(
    "caseId",
    anyOtherSupportingDocs = true,
    Some(Seq(FileUploadInfo(
      "file-ref-1",
      "Example.pdf",
      "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond),
      "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
      "application/pdf"))),
    "fewfew"
  )

  "A valid UpdateCaseAuditEvent model" should {
    "contain correct details" in {
      val event = UpdateCaseAuditEvent(updateCaseData, "credId", "eori")

      event.detail mustBe Json.parse(updateCaseOutputJson)
    }

  }
}
