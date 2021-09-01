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

package messages.cancelCase

import messages.BaseMessages

object CancelCaseUploadSupportingDocumentationMessages extends BaseMessages {

  val title: String        = "Upload documentation"
  val h1: String           = "Upload documentation"
  val fileSizeText: String = "Each file must be 6MB or less and you can only upload one file at a time."
  val typesOfFile          = "Types of file you can upload"
  val filePdf              = "PDF (.pdf)"
  val fileCsv              = "CSV (.csv)"
  val fileMs               = "Microsoft Excel, Word or PowerPoint (.xls, .xlsx, .doc, .docx, .ppt or .pptx)"
  val fileOd               = "Open Document Format (.odt, .ods or .odp)"
  val fileImage            = "image (.jpeg, .jpg, .png or .tiff)"
  val uploadFile: String   = "Upload a file"
  val button: String       = "Upload chosen file"
  val fileUploadId: String = "file"
  val fileTooSmall         = "Select a file to upload"
  val fileTooBig           = "The selected file must be smaller than 6MB"
  val fileUnknown          = "The selected file could not be uploaded – try again"
  val fileRejected         =
    "The selected file must be a PDF, XLS, XLSX, DOC, DOCX, PPT, PPTX, ODT, ODS, ODP, JPG, PNG, or TIFF"
  val fileQuarantined      = "The selected file contains a virus"
}
