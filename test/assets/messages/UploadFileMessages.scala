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

object UploadFileMessages extends BaseMessages {

  val title: String = "Upload supporting documentation"
  val h1: String = "Upload supporting documentation"
  val mustInclude: String = "You must upload:"
  val mayInclude: String = "You have told us you will upload:"
  val mustIncludeFile1: String = "the import declaration, also known as the C88 or SAD"
  val mustIncludeFile2: String = "the entry acceptance, also known as the E2"
  val mustIncludeFile3: String = "a document or scanned image showing how you calculated the tax or duty that should have been paid"
  val mayIncludeFile1: String = "the amendment import declaration (C88) and entry acceptance (E2)"
  val mayIncludeFile2: String = "the airway bill"
  val mayIncludeFile3: String = "a proof of origin"
  val mayIncludeFile4: String = "other documentation relevant to this underpayment disclosure"
  val fileRequirementsHeader = "File requirements"
  val fileFormats = "PDF or TXT format"
  val oneFileAtTime = "you can only upload one file at a time"
  val uploadAFile: String = "Upload a file"
  val fileUploadId: String = "file"
  val fileTooSmall = "Select a file to upload"
  val fileTooBig = "The selected file must be smaller than 6MB"
  val fileUnknown = "The selected file could not be uploaded – try again"
  val fileRejected = "The selected file must be a PDF, XLS, XLSX, DOC, DOCX, PPT, PPTX, ODT, ODS, ODP, JPG, PNG, or TIFF"
  val fileQuarantined = "The selected file contains a virus"
  val uploadChosenFile = "Upload chosen file"
  val typesOfFile = "Types of file you can upload"
  val PDF = "PDF (.pdf)"
  val CSV = "CSV (.csv)"
  val ms = "Microsoft Excel, Word or PowerPoint (.xls, .xlsx, .doc, .docx, .ppt or .pptx)"
  val openDocumentFormat = "Open Document Format (.odt, .ods or .odp)"
  val image = "image (.jpeg, .jpg, .png or .tiff)"
  val fileSize = "Each file must be 6MB or less and you can only upload one file at a time."
}
