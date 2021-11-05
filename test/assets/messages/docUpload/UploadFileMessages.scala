package messages.docUpload

import messages.UploadFileCommonMessages

object UploadFileMessages extends UploadFileCommonMessages {

  val title: String            = "Upload supporting documentation"
  val mustInclude: String      = "You must upload:"
  val mayInclude: String       = "You have told us you will upload:"
  val mustIncludeFile1: String = "the import declaration, also known as the C88 or SAD"
  val mustIncludeFile2: String = "the entry acceptance, also known as the E2"
  val mustIncludeFile3: String =
    "a document or scanned image showing how you calculated the tax or duty that should have been paid"
  val mayIncludeFile1: String = "the amendment import declaration (C88) and entry acceptance (E2)"
  val mayIncludeFile2: String = "the airway bill"
  val mayIncludeFile3: String = "a proof of origin"
  val mayIncludeFile4: String = "other documentation relevant to this underpayment disclosure"
}
