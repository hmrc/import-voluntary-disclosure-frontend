package messages.shared

import messages.BaseMessages

object FileUploadProgressMessages extends BaseMessages {

  val title: String   = "Upload progress"
  val waiting: String = "Waiting for uploaded file to be scanned."
  val request: String = "Please refresh to view latest progress."
  val refresh: String = "Refresh"
}
