package messages.docUpload

import messages.BaseMessages

object UploadAnotherFileMessages extends BaseMessages {

  def title(size: String, singularFile: String) = s"You have uploaded $size $singularFile"

  val requiredError: String    = "Select yes to upload another file"
  val remove: String           = "Remove fileName"
  val remove2: String          = "Remove fileName2"
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
