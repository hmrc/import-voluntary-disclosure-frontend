/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package models.upscan

import play.api.libs.json._

case class UpScanInitiateResponse(reference: Reference, uploadFormTemplate: UploadFormTemplate)

case class Reference(value: String)
case class UploadFormTemplate(href: String, fields: Map[String, String])
object UpScanInitiateResponse {

  implicit val referenceFormat: Format[Reference] = new Format[Reference] {

    override def writes(reference: Reference): JsValue = JsString(reference.value)

    override def reads(json: JsValue): JsResult[Reference] = json.validate[String] match {
      case JsSuccess(reference, _) => JsSuccess(Reference(reference))
      case error: JsError => error
    }
  }
  implicit val jsonFormatUploadForm: Format[UploadFormTemplate] = Json.format[UploadFormTemplate]

  implicit val jsonReadsForUpScanInitiateResponse: Reads[UpScanInitiateResponse] = new Reads[UpScanInitiateResponse] {
    override def reads(json: JsValue): JsResult[UpScanInitiateResponse] = {
      for {
        reference <- (json \ "reference").validate[Reference](referenceFormat)
        fieldsAndHref <- (json \ "uploadRequest").validate[UploadFormTemplate](jsonFormatUploadForm)
      } yield {
        UpScanInitiateResponse(reference, fieldsAndHref)
      }
    }
  }
}
