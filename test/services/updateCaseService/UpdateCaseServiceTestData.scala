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

package services.updateCaseService

import models.SubmissionType.{CancelCase, UpdateCase}
import models.{UpdateCaseData, UserAnswers}
import pages.serviceEntry.WhatDoYouWantToDoPage
import pages.shared.MoreDocumentationPage
import pages.updateCase._
import play.api.libs.json.{JsObject, Json}
import utils.ReusableValues

trait UpdateCaseServiceTestData extends ReusableValues {

  val updateData: UpdateCaseData = UpdateCaseData(
    "C18",
    anyOtherSupportingDocs = true,
    Some(supportingDocuments),
    additionalInfo = "Additional Information"
  )

  val completeUserAnswers: UserAnswers = (for {
    answers <- new UserAnswers("some-cred-id").set(DisclosureReferenceNumberPage, updateData.caseId)
    answers <- answers.set(WhatDoYouWantToDoPage, UpdateCase)
    answers <- answers.set(MoreDocumentationPage, updateData.anyOtherSupportingDocs)
    answers <- answers.set(UploadSupportingDocumentationPage, updateData.supportingDocuments.getOrElse(Seq.empty))
    answers <- answers.set(UpdateAdditionalInformationPage, updateData.additionalInfo)
  } yield answers).get

  val cancelCaseCompleteUserAnswers: UserAnswers = (for {
    answers <- new UserAnswers("some-cred-id").set(DisclosureReferenceNumberPage, updateData.caseId)
    answers <- answers.set(WhatDoYouWantToDoPage, CancelCase)
    answers <- answers.set(MoreDocumentationPage, updateData.anyOtherSupportingDocs)
    answers <- answers.set(UploadSupportingDocumentationPage, updateData.supportingDocuments.getOrElse(Seq.empty))
    answers <- answers.set(UpdateAdditionalInformationPage, updateData.additionalInfo)
  } yield answers).get

  val userAnswersWithoutDocs: UserAnswers = (for {
    answers <- new UserAnswers("some-cred-id").set(DisclosureReferenceNumberPage, updateData.caseId)
    answers <- answers.set(WhatDoYouWantToDoPage, UpdateCase)
    answers <- answers.set(MoreDocumentationPage, updateData.anyOtherSupportingDocs)
    answers <- answers.set(UpdateAdditionalInformationPage, updateData.additionalInfo)
  } yield answers).get

  def additionalInfo(eori: String): String =
    s"[EORINumber=$eori]\nAdditional Information"

  def cancelCaseAdditionalInfo(eori: String): String =
    s"[EORINumber=$eori]\nCancellation request:\nAdditional Information"

  val updateCaseJson: JsObject =
    Json.obj(
      "caseId"         -> "C18",
      "additionalInfo" -> additionalInfo("eori"),
      "supportingDocuments" -> Json.arr(
        Json.obj(
          "reference"       -> "file-ref-1",
          "fileName"        -> "TestDocument.pdf",
          "downloadUrl"     -> "http://some/location",
          "uploadTimestamp" -> "2021-05-14T20:15:13.807Z",
          "checksum"        -> "the file checksum",
          "fileMimeType"    -> "application/pdf"
        )
      )
    )

  val cancelCaseJson: JsObject =
    Json.obj(
      "caseId"         -> "C18",
      "additionalInfo" -> cancelCaseAdditionalInfo("eori"),
      "supportingDocuments" -> Json.arr(
        Json.obj(
          "reference"       -> "file-ref-1",
          "fileName"        -> "TestDocument.pdf",
          "downloadUrl"     -> "http://some/location",
          "uploadTimestamp" -> "2021-05-14T20:15:13.807Z",
          "checksum"        -> "the file checksum",
          "fileMimeType"    -> "application/pdf"
        )
      )
    )

  val updateCaseJsonWithoutDocs: JsObject =
    Json.obj(
      "caseId"         -> "C18",
      "additionalInfo" -> additionalInfo("eori")
    )
}
