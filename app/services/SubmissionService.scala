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
import models.OptionalDocument._
import models.SelectedDutyTypes.{Duty, Vat}
import models._
import models.audit.CreateCaseAuditEvent
import models.importDetails.{NumberOfEntries, UserType}
import models.requests.DataRequest
import play.api.Logger
import play.api.libs.json._
import services.ServiceJsonUtils._
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionService @Inject()(ivdSubmissionConnector: IvdSubmissionConnector,
                                  auditService: AuditService) {

  private val logger = Logger("application." + getClass.getCanonicalName)
  private val buildSubmissionErrorPrefix = "buildSubmission error - "

  def createCase()(implicit request: DataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, SubmissionResponse]] = {

    buildSubmission(request.userAnswers) match {
      case Right(submission) => {
        ivdSubmissionConnector.createCase(submission).map {
          case Right(confirmationResponse) =>
            auditService.audit(CreateCaseAuditEvent(confirmationResponse, submission))
            Right(confirmationResponse)
          case Left(errorResponse) => Left(errorResponse)
        }
      }
      case Left(err) => Future.successful(Left(err))
    }

  }

  private[services] def buildSubmission(answers: UserAnswers): Either[ErrorModel, JsValue] = {
    Json.fromJson[SubmissionData](answers.data) match {
      case JsSuccess(data, _) =>
        try {
          val json = buildEntryDetails(data) ++
            buildUnderpaymentDetails(data) ++
            buildReasonsDetails(data) ++
            buildSupportingDocumentation(data) ++
            buildDefermentDetails(data) ++
            buildDeclarantDetails(data) ++
            buildImporterDetails(data) ++
            buildRepresentativeDetails(data)
          Right(json.dropNullValues)
        } catch {
          case err: Exception => {
            logger.error(s"Failed to build SubmissionData Json. Error: ${err.getMessage}")
            Left(ErrorModel(-1, err.getMessage))
          }
        }
      case JsError(err) => {
        logger.error(s"Invalid User Answers data. Failed to parse into SubmissionData model. Error: ${err}")
        Left(ErrorModel(-1, err.toString()))
      }
    }
  }

  private[services] def buildEntryDetails(data: SubmissionData): JsObject = {
    val isBulkEntry = data.numEntries == NumberOfEntries.MoreThanOneEntry
    val customsProcessingCode = (data.oneCpc, data.originalCpc) match {
      case (Some(true), Some(cpc)) => cpc
      case (Some(true), _) => throw new RuntimeException(buildSubmissionErrorPrefix + "CPC missing from user answers")
      case _ => "VARIOUS"
    }

    val json = Json.obj(
      "userType" -> data.userType,
      "isBulkEntry" -> isBulkEntry,
      "isEuropeanUnionDuty" -> data.acceptedBeforeBrexit,
      "customsProcessingCode" -> customsProcessingCode
    )
    if (isBulkEntry) json else Json.obj("entryDetails" -> data.entryDetails) ++ json
  }

  private[services] def buildUnderpaymentDetails(data: SubmissionData): JsObject = {
    Json.obj(
      "underpaymentDetails" -> data.underpaymentDetails
    )
  }

  private[services] def buildReasonsDetails(data: SubmissionData): JsObject = {
    val isBulk = data.numEntries == NumberOfEntries.MoreThanOneEntry
    val filteredItems = data.amendedItems.map(_.filterNot(_.boxNumber == 99))
    val amendedItems = if (isBulk || filteredItems.forall(_.isEmpty)) Json.obj() else Json.obj("amendedItems" -> filteredItems)
    val otherReason = data.amendedItems.flatMap(_.find(_.boxNumber == 99)).map(_.original)
    val additionInfo = otherReason.orElse(data.additionalInfo).getOrElse("Not Applicable")

    Json.obj("additionalInfo" -> additionInfo) ++ amendedItems
  }

  private[services] def buildSupportingDocumentation(data: SubmissionData): JsObject = {
    val isBulkEntry = data.numEntries == NumberOfEntries.MoreThanOneEntry
    val authorityDocuments = data.authorityDocuments.getOrElse(Seq.empty)
    val mandatoryDocumentsList: Seq[DocumentType] = if (isBulkEntry) {
      Seq(DocumentTypes.Other)
    } else {
      Seq(DocumentTypes.OriginalC88, DocumentTypes.OriginalC2, DocumentTypes.AmendedSubstituteEntryWorksheet)
    }

    val optionalDocumentsList: Seq[DocumentType] = data.optionalDocumentsSupplied.getOrElse(Seq.empty).flatMap {
      case ImportAndEntry => Seq(DocumentTypes.AmendedC88, DocumentTypes.AmendedC2)
      case AirwayBill => Seq(DocumentTypes.InvoiceAirwayBillPreferenceCertificate)
      case OriginProof => Seq(DocumentTypes.InvoiceAirwayBillPreferenceCertificate)
      case Other => Seq(DocumentTypes.Other)
      case _ => Seq.empty
    }

    val documentsSupplied = mandatoryDocumentsList ++ optionalDocumentsList.distinct

    val supportingDocuments = if (data.paymentByDeferment) {
      (data.splitDeferment, data.defermentType, data.additionalDefermentType) match {
        case (Some(true), Some("B"), Some("B")) =>
          authorityDocuments.filter(x => Seq(Duty, Vat).contains(x.dutyType)).map(_.file) ++ data.supportingDocuments
        case (Some(true), Some("B"), _) =>
          authorityDocuments.filter(_.dutyType == Duty).map(_.file) ++ data.supportingDocuments
        case (Some(true), _, Some("B")) =>
          authorityDocuments.filter(_.dutyType == Vat).map(_.file) ++ data.supportingDocuments
        case (_, Some("B"), _) =>
          authorityDocuments.map(_.file) ++ data.supportingDocuments
        case _ => data.supportingDocuments
      }
    } else {
      data.supportingDocuments
    }

    val supportingDocumentTypes = if (data.paymentByDeferment) {
      (data.splitDeferment, data.defermentType, data.additionalDefermentType) match {
        case (Some(true), Some(dt), Some(addDt)) if dt != "B" && addDt != "B" => documentsSupplied
        case (Some(true), _, _) => documentsSupplied ++ Seq(DefermentAuthorisation)
        case (_, Some("B"), _) => documentsSupplied ++ Seq(DefermentAuthorisation)
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
      (data.defermentType, data.defermentAccountNumber, data.additionalDefermentAccountNumber, data.additionalDefermentType, data.splitDeferment) match {
        case (Some(dt), Some(dan), Some(addDan), Some(addDT), Some(true)) if data.userType == UserType.Representative =>
          Json.obj(
            "defermentType" -> dt,
            "defermentAccountNumber" -> s"$dt$dan",
            "additionalDefermentAccountNumber" -> s"$addDT$addDan"
          )
        case (Some(dt), Some(dan), _, _, _) if data.userType == UserType.Representative =>
          Json.obj(
            "defermentType" -> dt,
            "defermentAccountNumber" -> s"$dt$dan"
          )
        case (_, Some(dan), _, _, _) if data.userType == UserType.Importer =>
          Json.obj(
            "defermentType" -> "D",
            "defermentAccountNumber" -> s"D$dan"
          )
        case _ => throw new RuntimeException(buildSubmissionErrorPrefix + "Deferment information missing")
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
      val vatNumber = data.knownDetails.vatNumber match {
        case Some(value) => Json.obj("vatNumber" -> value)
        case None => Json.obj()
      }
      Json.obj(
        "importer" -> (
          Json.obj(
            "eori" -> data.knownDetails.eori,
            "contactDetails" -> data.declarantContactDetails.copy(fullName = data.knownDetails.name),
            "address" -> data.traderAddress
          ) ++ vatNumber)
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
      details.getOrElse(
        throw new RuntimeException(buildSubmissionErrorPrefix + "Importer details not captured in representative flow")
      )
    }
  }

  private[services] def buildRepresentativeDetails(data: SubmissionData): JsObject = {
    if (data.userType == UserType.Representative) {
      val vatNumber = data.knownDetails.vatNumber match {
        case Some(value) => Json.obj("vatNumber" -> value)
        case None => Json.obj()
      }
      Json.obj(
        "representative" -> (
          Json.obj(
            "eori" -> data.knownDetails.eori,
            "contactDetails" -> data.declarantContactDetails,
            "address" -> data.traderAddress
          ) ++ vatNumber)
      )
    } else {
      Json.obj()
    }
  }

}
