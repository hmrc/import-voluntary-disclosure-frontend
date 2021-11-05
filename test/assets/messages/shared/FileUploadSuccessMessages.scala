package messages.shared

import messages.BaseMessages

object FileUploadSuccessMessages extends BaseMessages {

  val filename: String = "TestDocument.pdf"

  val title: String    = "The file has been uploaded successfully"
  val bodyText: String = s"You have uploaded $filename"
}
