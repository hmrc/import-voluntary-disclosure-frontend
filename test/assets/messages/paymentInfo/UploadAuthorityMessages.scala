package messages.paymentInfo

import messages.UploadFileCommonMessages

object UploadAuthorityMessages extends UploadFileCommonMessages {

  val title: String = "Upload proof of authority to use this deferment account"

  def para1(dan: String, dutyType: String): String =
    s"You must provide proof that you have one-off authority to use this deferment account $dan to pay for the $dutyType owed."

  val para2: String =
    "The proof needs to be dated and signed by the owner of the deferment account. The date must be after the date of the original import declaration."
  val uploadFile: String = "Upload file"
}
