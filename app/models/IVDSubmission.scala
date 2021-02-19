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

import play.api.libs.json.{Json, Reads, Writes, __}

case class IVDSubmission(userType: UserType,
                         numEntries: NumberOfEntries,
                         acceptanceDate: Option[Boolean],
                         additionalInfo: String = "Not Applicable",
                         entryDetails: EntryDetails,
                         originalCpc: String,
                         declarantContactDetails: ContactDetails,
                         declarantAddress: ContactAddress,
                         defermentType: Option[String] = None,
                         defermentAccountNumber: Option[String] = None,
                         additionalDefermentNumber: Option[String] = None,
                         amendedItems: Seq[UnderpaymentReason] = Seq.empty,
                         underpaymentDetails: Seq[UnderpaymentDetail] = Seq.empty,
                         documentsSupplied: Seq[String] = Seq.empty,
                         supportingDocuments: Seq[FileUploadInfo] = Seq.empty)

object IVDSubmission {
  implicit val writes: Writes[IVDSubmission] = (o: IVDSubmission) => {
    Json.obj(
      "userType" -> o.userType,
      "additionalInfo" -> o.additionalInfo,
      "entryDetails" -> o.entryDetails,
      "customsProcessingCode" -> o.originalCpc,
      "declarantContactDetails" -> o.declarantContactDetails,
      "declarantAddress" -> o.declarantAddress,
      "underpaymentDetails" -> o.underpaymentDetails,
      "supportingDocumentTypes" -> o.documentsSupplied,
      "amendedItems" -> o.amendedItems,
      "supportingDocuments" -> o.supportingDocuments
    )
  }

  implicit val ivdSubmissionReads: Reads[IVDSubmission] =
    for {
      userType <- (__ \ "user-type").read[UserType]
      numEntries <- (__ \ "number-of-entries").read[NumberOfEntries]
      acceptanceDate <- (__ \ "acceptance-date").readNullable[Boolean]
      entryDetails <- (__ \ "entry-details").read[EntryDetails]
      originalCpc <- (__ \ "cpc" \ "original-cpc").read[String]
      traderContactDetails <- (__ \ "trader-contact-details").read[ContactDetails]
      traderAddress <- (__ \ "final-importer-address").read[ContactAddress]
      customsDuty <- (__ \ "customs-duty").readNullable[UnderpaymentAmount]
      importVat <- (__ \ "import-vat").readNullable[UnderpaymentAmount]
      exciseDuty <- (__ \ "excise-duty").readNullable[UnderpaymentAmount]
    } yield {
      val customsDutyUnderpayment = customsDuty.map(x => Seq(UnderpaymentDetail("customsDuty", x.original, x.amended))).getOrElse(Seq.empty)
      val importVatUnderpayment = importVat.map(x => Seq(UnderpaymentDetail("importVat", x.original, x.amended))).getOrElse(Seq.empty)
      val exciseDutyUnderpayment = exciseDuty.map(x => Seq(UnderpaymentDetail("exciseDuty", x.original, x.amended))).getOrElse(Seq.empty)

      IVDSubmission(
        userType = userType,
        numEntries = numEntries,
        acceptanceDate = acceptanceDate,
        entryDetails = entryDetails,
        originalCpc = originalCpc,
        declarantContactDetails = traderContactDetails,
        declarantAddress = traderAddress,
        underpaymentDetails = customsDutyUnderpayment ++ importVatUnderpayment ++ exciseDutyUnderpayment
      )
    }
}
