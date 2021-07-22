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

object BulkUploadFileMessages extends BaseMessages {

  val title: String = "Upload a file containing the underpayment details for each entry"
  val h1: String = "Upload a file containing the underpayment details for each entry"
  val mustContain: String = "Your file must contain a list of all the entries you are including in this underpayment disclosure. For each entry you must provide:"
  val bullet1: String = "the EPU number, entry number and entry date"
  val bullet2: String = "all item numbers"
  val bullet3: String = "the type of tax or duty that was paid"
  val bullet4: String = "the amount of tax or duty that was paid and the amount that should have been paid"
  val bullet5: String = "the original and amended values of any information that was wrong in the original import declaration (for example, commodity codes or invoice values)"
  val uploadFile: String = "Upload chosen file"
  val typesOfFile: String = "Types of file you can upload"
  val PDF: String = "PDF (.pdf)"
  val CSV: String = "CSV (.csv)"
  val excel: String = "Microsoft Excel, Word or PowerPoint (.xls, .xlsx, .doc, .docx, .ppt or .pptx)"
  val openDocumentFormat: String = "Open Document Format (.odt, .ods or .odp)"
  val image: String = "image (.jpeg, .jpg, .png or .tiff)"
  val fileSize: String = "The file must be 6MB or less."
  val fileUploadId: String = "file"
  val fileTooSmall = "Select a file to upload"
  val fileTooBig = "The selected file must be smaller than 6MB"
  val fileUnknown = "The selected file could not be uploaded – try again"
  val fileRejected = "The selected file must be a PDF, XLS, XLSX, DOC, DOCX, PPT, PPTX, ODT, ODS, ODP, JPG, PNG, or TIFF"
  val fileQuarantined = "The selected file contains a virus"
}
