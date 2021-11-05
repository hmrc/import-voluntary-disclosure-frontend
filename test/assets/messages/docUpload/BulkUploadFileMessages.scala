package messages.docUpload

import messages.UploadFileCommonMessages

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
