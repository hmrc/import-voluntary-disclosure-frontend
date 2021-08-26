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

package services.updateCaseService

import models.SubmissionType.{CancelCase, UpdateCase}
import models.{UpdateCaseData, UserAnswers}
import pages._
import pages.serviceEntry.WhatDoYouWantToDoPage
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
    answers <- answers.set(MoreDocumentationPage, updateData.anyOtherSupportingDocs)
    answers <- answers.set(UpdateAdditionalInformationPage, updateData.additionalInfo)
  } yield answers).get

  val additionalInfo: String = "Additional Information"

  val additionalInfoWithPrependedText: String =
    "Cancellation request:\n" + "Additional Information"

  val updateCaseJson: JsObject =
    Json.obj(
      "caseId" -> "C18",
      "additionalInfo" -> additionalInfo,
      "supportingDocuments" -> Json.arr(
        Json.obj(
          "reference" -> "file-ref-1",
          "fileName" -> "TestDocument.pdf",
          "downloadUrl" -> "http://some/location",
          "uploadTimestamp" -> "2021-05-14T20:15:13.807",
          "checksum" -> "the file checksum",
          "fileMimeType" -> "application/pdf"
        )
      )
    )

  val cancelCaseJson: JsObject =
    Json.obj(
      "caseId" -> "C18",
      "additionalInfo" -> additionalInfoWithPrependedText,
      "supportingDocuments" -> Json.arr(
        Json.obj(
          "reference" -> "file-ref-1",
          "fileName" -> "TestDocument.pdf",
          "downloadUrl" -> "http://some/location",
          "uploadTimestamp" -> "2021-05-14T20:15:13.807",
          "checksum" -> "the file checksum",
          "fileMimeType" -> "application/pdf"
        )
      )
    )

  val updateCaseJsonWithoutDocs: JsObject =
    Json.obj(
      "caseId" -> "C18",
      "additionalInfo" -> additionalInfoWithPrependedText
    )
}
