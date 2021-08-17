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

package utils

import messages.underpayments.UnderpaymentTypeMessages
import models.OptionalDocument.{AirwayBill, ImportAndEntry, OriginProof, Other}
import models._
import models.underpayments.UnderpaymentDetail
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.http.HttpResponse

import java.time.{LocalDate, LocalDateTime}
import models.importDetails.{EntryDetails, NumberOfEntries, UserType}
import models.reasons.{BoxNumber, UnderpaymentReason}

trait ReusableValues {

  private val validTimestamp = LocalDateTime.parse("2021-05-14T20:15:13.807")

  val idOne: String = "1"

  val addressDetails: ContactAddress = ContactAddress(
    addressLine1 = "99 Avenue Road",
    addressLine2 = None,
    city = "Anyold Town",
    postalCode = Some("99JZ 1AA"),
    countryCode = "GB"
  )

  val eoriDetails: EoriDetails = EoriDetails(
    "GB987654321000",
    "Fast Food ltd",
    ContactAddress(
      addressLine1 = "99 Avenue Road",
      addressLine2 = None,
      city = "Anyold Town",
      postalCode = Some("99JZ 1AA"),
      countryCode = "GB"
    ),
    Some("987654321000")
  )

  val eoriDetailsWithoutVatNumber: EoriDetails = EoriDetails(
    "GB987654321000",
    "Fast Food ltd",
    ContactAddress(
      addressLine1 = "99 Avenue Road",
      addressLine2 = None,
      city = "Anyold Town",
      postalCode = Some("99JZ 1AA"),
      countryCode = "GB"
    ),
    None
  )

  val errorModel: HttpResponse = HttpResponse(Status.NOT_FOUND, "Error Message")

  val detailsJson: JsObject = Json.obj(
    "responseDetail" -> Json.obj(
      "EORINo" -> "GB987654321000",
      "CDSFullName" -> "Fast Food ltd",
      "CDSEstablishmentAddress" -> Json.obj(
        "streetAndNumber" -> "99 Avenue Road",
        "city" -> "Anyold Town",
        "postalCode" -> "99JZ 1AA",
        "countryCode" -> "GB"
      )
    )
  )

  val cleanedDetailsJson: JsObject = Json.obj(
    "eori" -> "GB987654321000",
    "vatNumber" -> "987654321000",
    "name" -> "Fast Food ltd",
    "streetAndNumber" -> "99 Avenue Road",
    "city" -> "Anyold Town",
    "postalCode" -> "99JZ 1AA",
    "countryCode" -> "GB"
  )

  val underpaymentTypeRadioButtons = Seq(
    createRadioButton("B00", UnderpaymentTypeMessages.importVAT),
    createRadioButton("A00", UnderpaymentTypeMessages.customsDuty),
    createRadioButton("E00", UnderpaymentTypeMessages.exciseDuty),
    createRadioButton("A20", UnderpaymentTypeMessages.additionalDuty),
    createRadioButton("A30", UnderpaymentTypeMessages.definitiveAntiDumpingDuty),
    createRadioButton("A35", UnderpaymentTypeMessages.provisionalAntiDumpingDuty),
    createRadioButton("A40", UnderpaymentTypeMessages.definitiveCountervailingDuty),
    createRadioButton("A45", UnderpaymentTypeMessages.provisionalCountervailingDuty),
    createRadioButton("A10", UnderpaymentTypeMessages.agriculturalDuty),
    createRadioButton("D10", UnderpaymentTypeMessages.compensatoryDuty)
  )

  val importer: UserType = UserType.Importer
  val representative: UserType = UserType.Representative
  val entryDetails: Option[EntryDetails] = Some(EntryDetails("123", "123456Q", LocalDate.parse("2020-12-12")))
  val oneEntry: NumberOfEntries = NumberOfEntries.OneEntry
  val bulkEntry: NumberOfEntries = NumberOfEntries.MoreThanOneEntry
  val contactDetails: ContactDetails = ContactDetails("John Smith", "test@test.com", "0123456789")
  val cpc: String = "4000C09"
  val supportingDocuments: Seq[FileUploadInfo] = Seq(
    FileUploadInfo(
      reference = "file-ref-1",
      fileName = "TestDocument.pdf",
      downloadUrl = "http://some/location",
      uploadTimestamp = validTimestamp,
      checksum = "the file checksum",
      fileMimeType = "application/pdf"
    )
  )
  val authorityDocuments: Seq[UploadAuthority] = Seq(
    UploadAuthority(
      defermentAccountNumber,
      SelectedDutyTypes.Duty,
      FileUploadInfo(
        reference = "file-ref-1",
        fileName = "TestDocument.pdf",
        downloadUrl = "http://some/location",
        uploadTimestamp = validTimestamp,
        checksum = "the file checksum",
        fileMimeType = "application/pdf"
      )
    ))
  val vatAuthorityDocuments: Seq[UploadAuthority] = Seq(
    UploadAuthority(
      defermentAccountNumber,
      SelectedDutyTypes.Vat,
      FileUploadInfo(
        reference = "file-ref-1",
        fileName = "TestDocument.pdf",
        downloadUrl = "http://some/location",
        uploadTimestamp = validTimestamp,
        checksum = "the file checksum",
        fileMimeType = "application/pdf"
      )
    ))
  val defermentAccountNumber: String = "1234567"
  val underpaymentReasons = Some(Seq(
    UnderpaymentReason(BoxNumber.Box22, 0, "GBP100", "GBP200"),
    UnderpaymentReason(BoxNumber.Box33, 1, "2204109400X411", "2204109400X412")
  ))
  val optionalSupportingDocuments: Seq[OptionalDocument] = Seq(
    ImportAndEntry, AirwayBill, OriginProof, Other
  )

  def createRadioButton(value: String, message: String): RadioItem = {
    RadioItem(
      value = Some(value),
      content = Text(message),
      checked = false
    )
  }

  def allUnderpaymentDetailsSelected(): Seq[UnderpaymentDetail] = {
    Seq("B00", "A00", "E00", "A20", "A30", "A35", "A40", "A45", "A10", "D10").map(underpayment =>
      UnderpaymentDetail(underpayment, 0.0, 1.0)
    )
  }

  def someUnderpaymentDetailsSelected(): Seq[UnderpaymentDetail] = {
    Seq("B00", "A00", "E00").map(underpayment =>
      UnderpaymentDetail(underpayment, 0.0, 1.0)
    )
  }

}
