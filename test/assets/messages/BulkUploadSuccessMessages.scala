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

package messages

object BulkUploadSuccessMessages extends BaseMessages {

  val filename: String = "TestDocument.pdf"

  val title: String = "The file has been uploaded successfully"
  val h1: String = "The file has been uploaded successfully"
  val bodyText: String = s"You have uploaded $filename"
}
