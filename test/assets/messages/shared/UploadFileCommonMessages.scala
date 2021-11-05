package messages.shared

import messages.BaseMessages

trait UploadFileCommonMessages extends BaseMessages {

  val uploadAFile      = "Upload a file"
  val uploadChosenFile = "Upload chosen file"
  val typesOfFile      = "Types of file you can upload"
  val filePdf          = "PDF (.pdf)"
  val fileCsv          = "CSV (.csv)"
  val fileMs           = "Microsoft Excel, Word or PowerPoint (.xls, .xlsx, .doc, .docx, .ppt or .pptx)"
  val fileOd           = "Open Document Format (.odt, .ods or .odp)"
  val fileImage        = "image (.jpeg, .jpg, .png or .tiff)"
  val fileSize         = "Each file must be 6MB or less and you can only upload one file at a time."

  val fileUploadId: String = "file"
  val fileTooSmall         = "Select a file to upload"
  val fileTooBig           = "The selected file must be smaller than 6MB"
  val fileUnknown          = "The selected file could not be uploaded – try again"
  val fileRejected =
    "The selected file must be a PDF, XLS, XLSX, DOC, DOCX, PPT, PPTX, ODT, ODS, ODP, JPG, PNG, or TIFF"
  val fileQuarantined = "The selected file contains a virus"
}
