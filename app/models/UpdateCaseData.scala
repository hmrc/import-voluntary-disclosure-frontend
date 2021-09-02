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

package models

import pages.shared.MoreDocumentationPage
import pages.updateCase._
import play.api.libs.json.Reads

case class UpdateCaseData(caseId: String,
                          anyOtherSupportingDocs: Boolean,
                          supportingDocuments: Option[Seq[FileUploadInfo]],
                          additionalInfo: String)

object UpdateCaseData {
  implicit val reads: Reads[UpdateCaseData] =
    for {
      caseId <- DisclosureReferenceNumberPage.path.read[String]
      anyOtherSupportingDocs <- MoreDocumentationPage.path.read[Boolean]
      supportingDocuments <- UploadSupportingDocumentationPage.path.readNullable[Seq[FileUploadInfo]]
      additionalInfo <- UpdateAdditionalInformationPage.path.read[String]
    } yield
      UpdateCaseData(
        caseId = caseId,
        anyOtherSupportingDocs = anyOtherSupportingDocs,
        supportingDocuments = supportingDocuments,
        additionalInfo = additionalInfo
      )
}
