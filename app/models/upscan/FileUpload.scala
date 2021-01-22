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

package models.upscan

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import java.time.Instant

case class FileUpload(reference: String,
                      credId: String,
                      fileStatus: Option[FileStatusEnum] = None,
                      uploadDetails: Option[UploadDetails] = None,
                      lastUpdatedDate: Option[Instant] = None){
  val fileName = uploadDetails.map(_.fileName)
}


object FileUpload {

  val callbackReads: Reads[FileUpload] = (
    (JsPath \ "reference").read[String] and
      (JsPath \ "credId").read[String] and
      (JsPath \ "fileStatus").readNullable[FileStatusEnum] and
      (JsPath \ "uploadDetails").readNullable[UploadDetails] and
      (JsPath \ "lastUpdatedDate").readNullable[Instant]
    )(FileUpload.apply _)

  implicit val format: OFormat[FileUpload] = Json.format[FileUpload]
}

case class UploadDetails(checksum: String,
                         fileName: String,
                         fileMimeType: String)

object UploadDetails {

  implicit val format: OFormat[UploadDetails] = Json.format[UploadDetails]
}