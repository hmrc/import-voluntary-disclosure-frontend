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
import models.DocumentTypes.{DefermentAuthorisation, DocumentType}
import models.SelectedDutyTypes.{Duty, Vat}
import models.requests.DataRequest
import models._
import play.api.Logger
import play.api.libs.json.{JsError, JsObject, JsSuccess, JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionService @Inject()(ivdSubmissionConnector: IvdSubmissionConnector) {

  private val logger = Logger("application." + getClass.getCanonicalName)

  def createCase()(implicit request: DataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, SubmissionResponse]] = {

    buildSubmission(request.userAnswers) match {
      case Right(submission) => {
        ivdSubmissionConnector.postSubmission(submission).map {
          case Right(confirmationResponse) => Right(confirmationResponse)
          case Left(errorResponse) => {
            logger.error("Error in response from EIS/Pega: " + errorResponse.message)
            Left(errorResponse)
          }
        }
      }
      case Left(err) => Future.successful(Left(err))
    }

  }

  private[services] def buildSubmission(answers: UserAnswers): Either[ErrorModel, JsValue] = {
    Json.fromJson[SubmissionData](answers.data) match {
      case JsSuccess(data, _) =>
        Right(
          buildEntryDetails(data) ++
          buildUnderpaymentDetails(data) ++
          buildReasonsDetails(data) ++
          buildSupportingDocumentation(data) ++
          buildDefermentDetails(data) ++
          buildDeclarantDetails(data) ++
          buildImporterDetails(data) ++
          buildRepresentativeDetails(data)
        )
      case JsError(_) => {
        logger.error("Invalid User Answers data. Failed to read into SubmissionData model")
        Left(ErrorModel(-1, "Invalid User Answers data. Failed to read into SubmissionData model"))
      }
    }
  }

  private[services] def buildEntryDetails(data: SubmissionData): JsObject = {
    val isBulkEntry = data.numEntries == NumberOfEntries.MoreThanOneEntry

    Json.obj(
      "userType" -> data.userType,
      "isBulkEntry" -> isBulkEntry,
      "isEuropeanUnionDuty" -> data.acceptedBeforeBrexit,
      "entryDetails" -> data.entryDetails,
      "customsProcessingCode" -> data.originalCpc
    )
  }

  private[services] def buildUnderpaymentDetails(data: SubmissionData): JsObject = {
    Json.obj(
      "underpaymentDetails" -> data.underpaymentDetails
    )
  }

  private[services] def buildReasonsDetails(data: SubmissionData): JsObject = {
    Json.obj(
      "additionalInfo" -> data.additionalInfo,
      "amendedItems" -> data.amendedItems
    )
  }

  private[services] def buildSupportingDocumentation(data: SubmissionData): JsObject = {
    val mandatoryDocumentsList: Seq[DocumentType] = Seq(
      DocumentTypes.OriginalC88, DocumentTypes.OriginalC2, DocumentTypes.AmendedSubstituteEntryWorksheet
    )

    val optionalDocumentsList: Option[Seq[DocumentType]] = Some(data.optionalDocumentsSupplied.getOrElse(Seq.empty).flatMap {
      case "importAndEntry" => Seq(DocumentTypes.AmendedC88, DocumentTypes.AmendedC2)
      case "airwayBill" => Seq(DocumentTypes.InvoiceAirwayBillPreferenceCertificate)
      case "originProof" => Seq(DocumentTypes.InvoiceAirwayBillPreferenceCertificate)
      case "other" => Seq(DocumentTypes.Other)
      case _ => Seq.empty
    })

    val documentsSupplied = mandatoryDocumentsList ++ optionalDocumentsList.getOrElse(Seq.empty)

    val supportingDocuments = if (data.paymentByDeferment) {
      (data.splitDeferment, data.defermentType, data.additionalDefermentType) match {
        case (true, Some("B"), Some("B")) =>
          data.authorityDocuments.filter(x => Seq(Duty, Vat).contains(x.dutyType)).map(_.file) ++ data.supportingDocuments
        case (true, Some("B"), _) =>
          data.authorityDocuments.filter(_.dutyType == Duty).map(_.file) ++ data.supportingDocuments
        case (true, _, Some("B")) =>
          data.authorityDocuments.filter(_.dutyType == Vat).map(_.file) ++ data.supportingDocuments
        case (false, Some("B"), _) =>
          data.authorityDocuments.map(_.file) ++ data.supportingDocuments
        case _ => data.supportingDocuments
      }
    } else {
      data.supportingDocuments
    }

    val supportingDocumentTypes = if (data.paymentByDeferment) {
      (data.splitDeferment, data.defermentType, data.additionalDefermentType) match {
        case (true, Some(dt), Some(addDt)) if dt != "B" && addDt != "B" => documentsSupplied
        case (true, _, _) => documentsSupplied ++ Seq(DefermentAuthorisation)
        case (false, Some("B"), _) => documentsSupplied ++ Seq(DefermentAuthorisation)
        case _ => documentsSupplied
      }
    } else {
      documentsSupplied
    }

    Json.obj(
      "supportingDocumentTypes" -> supportingDocumentTypes,
      "supportingDocuments" -> supportingDocuments
    )
  }

  private[services] def buildDefermentDetails(data: SubmissionData): JsObject = {
    if (data.paymentByDeferment) {
      (data.defermentType, data.defermentAccountNumber, data.additionalDefermentAccountNumber, data.additionalDefermentType) match {
        case (Some(dt), Some(dan), Some(addDan), Some(addDT)) if data.userType == UserType.Representative && data.splitDeferment =>
          Json.obj(
            "defermentType" -> dt,
            "defermentAccountNumber" -> s"$dt$dan",
            "additionalDefermentAccountNumber" -> s"$addDT$addDan"
          )
        case (Some(dt), Some(dan), _, _) if data.userType == UserType.Representative =>
          Json.obj(
            "defermentType" -> dt,
            "defermentAccountNumber" -> s"$dt$dan"
          )
        case (_, Some(dan), _, _) if data.userType == UserType.Importer =>
          Json.obj(
            "defermentType" -> "D",
            "defermentAccountNumber" -> s"D$dan"
          )
        case _ => Json.obj()
      }
    } else {
      Json.obj()
    }
  }

  private[services] def buildDeclarantDetails(data: SubmissionData): JsObject = {
    Json.obj(
      "declarantContactDetails" -> data.declarantContactDetails
    )
  }

  private[services] def buildImporterDetails(data: SubmissionData): JsObject = {
    val DEFAULT_EORI: String = "GBPR"
    if (data.userType == UserType.Importer) {
      Json.obj(
        "importer" -> Json.obj(
          "eori" -> data.knownDetails.eori,
          "contactDetails" -> data.declarantContactDetails.copy(fullName = data.knownDetails.name),
          "address" -> data.traderAddress
        )
      )
    } else {
      val details = for {
        eori <- data.importerEori.orElse(Some(DEFAULT_EORI))
        name <- data.importerName
        address <- data.importerAddress
        isVatRegistered <- data.isImporterVatRegistered.orElse(Some(false))
      } yield {
        val mandatoryDetails = Json.obj(
          "eori" -> eori,
          "contactDetails" -> ContactDetails(name),
          "address" -> address
        )
        val vatNumber = if (isVatRegistered) {
          Json.obj(
            "vatNumber" -> eori.substring(2, 11)
          )
        } else {
          Json.obj()
        }

        val importerDetails = mandatoryDetails ++ vatNumber
        Json.obj("importer" -> importerDetails)
      }
      details.getOrElse(throw new RuntimeException("Importer details not captured in representative flow"))
    }
  }

  private[services] def buildRepresentativeDetails(data: SubmissionData): JsObject = {
    if (data.userType == UserType.Representative) {
      Json.obj(
        "representative" -> Json.obj(
          "eori" -> data.knownDetails.eori,
          "contactDetails" -> data.declarantContactDetails,
          "address" -> data.traderAddress
        )
      )
    } else {
      Json.obj()
    }
  }

}
