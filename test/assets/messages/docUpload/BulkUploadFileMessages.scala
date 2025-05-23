/*
 * Copyright 2025 HM Revenue & Customs
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

package messages.docUpload

import messages.shared.UploadFileCommonMessages

object BulkUploadFileMessages extends UploadFileCommonMessages {

  val title: String = "Upload a file containing the underpayment details for each entry"
  val mustContain: String =
    "Your file must contain a list of all the entries you are including in this underpayment disclosure. For each entry you must provide:"
  val bullet1: String = "the EPU number, entry number and entry date"
  val bullet2: String = "all item numbers"
  val bullet3: String = "the type of tax or duty that was paid"
  val bullet4: String = "the amount of tax or duty that was paid and the amount that should have been paid"
  val bullet5: String =
    "the original and amended values of any information that was wrong in the original import declaration (for example, commodity codes or invoice values)"
}
