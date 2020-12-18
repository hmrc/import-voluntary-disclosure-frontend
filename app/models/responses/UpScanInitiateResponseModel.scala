/*
 * Copyright 2020 HM Revenue & Customs
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

package models.responses

import play.api.libs.json._

case class UpScanInitiateResponseModel(reference: Reference, uploadFormTemplate: UploadFormTemplate)

case class Reference(value: String)
case class UploadFormTemplate(href: String, fields: Map[String, String])
object UpScanInitiateResponseModel {

  implicit val referenceFormat: Format[Reference] = new Format[Reference] {

    override def writes(reference: Reference): JsValue = JsString(reference.value)

    override def reads(json: JsValue): JsResult[Reference] = json.validate[String] match {
      case JsSuccess(reference, _) => JsSuccess(Reference(reference))
      case error: JsError => error
    }
  }
  implicit val jsonFormatUploadForm: Format[UploadFormTemplate] = Json.format[UploadFormTemplate]

  implicit val jsonReadsForUpScanInitiateResponseModel: Reads[UpScanInitiateResponseModel] = new Reads[UpScanInitiateResponseModel] {
    override def reads(json: JsValue): JsResult[UpScanInitiateResponseModel] = {
      for {
        reference <- (json \ "reference").validate[Reference](referenceFormat)
        fieldsAndHref <- (json \ "uploadRequest").validate[UploadFormTemplate](jsonFormatUploadForm)
      } yield {
        UpScanInitiateResponseModel(reference, fieldsAndHref)
      }
    }
  }

}
