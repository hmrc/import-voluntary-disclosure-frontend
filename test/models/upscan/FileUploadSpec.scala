/*
 * Copyright 2023 HM Revenue & Customs
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

import base.ModelSpecBase
import models.upscan.FileStatusEnum.READY
import play.api.libs.json.{JsSuccess, JsValue, Json}
import play.api.test

import java.time.LocalDateTime

class FileUploadSpec extends ModelSpecBase {

  val fileUpload: FileUpload = FileUpload(
    "11370e18-6e24-453e-b45a-76d3e32ea33d",
    None,
    Some("https://bucketName.s3.eu-west-2.amazonaws.com?1235676"),
    Some(READY),
    Some(
      UploadDetails(
        LocalDateTime.parse("2018-04-24T09:30"),
        "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
        "test.pdf",
        "application/pdf"
      )
    ),
    None,
    None
  )

  "FileUpload" must {
    "read json as FileUpload" in {

      val json: JsValue = Json.parse("""
          | {
          |   "reference" : "11370e18-6e24-453e-b45a-76d3e32ea33d",
          |   "fileStatus" : "READY",
          |   "downloadUrl" : "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
          |   "uploadDetails": {
          |     "uploadTimestamp": "2018-04-24T09:30:00Z",
          |     "checksum": "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
          |     "fileName": "test.pdf",
          |     "fileMimeType": "application/pdf"
          |   }
          | }""".stripMargin)

      Json.fromJson[FileUpload](json) mustBe JsSuccess(fileUpload)
    }

    "read json as FileUpload when uploadTimestamp is of type mongo Date" in {
      val json = Json.parse(
          """
          |{
          | "reference":"11370e18-6e24-453e-b45a-76d3e32ea33d",
          | "downloadUrl":"https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
          | "fileStatus":"READY",
          | "uploadDetails":
          |   {
          |     "uploadTimestamp":{"$date":{"$numberLong":"1524562200000"}},
          |     "checksum":"396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
          |     "fileName":"test.pdf",
          |     "fileMimeType":"application/pdf"
          |   }
          |}
          |""".stripMargin)

      Json.fromJson[FileUpload](json) mustBe JsSuccess(fileUpload)
    }
  }
}
