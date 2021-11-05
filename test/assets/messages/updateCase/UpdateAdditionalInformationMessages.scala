package messages.updateCase

import messages.BaseMessages

object UpdateAdditionalInformationMessages extends BaseMessages {

  val title: String          = "Tell us any additional information"
  val p1: String             = "You may have been asked to provide more information to help us progress the disclosure."
  val requiredError: String  = "Enter the additional information about the underpayment"
  val maxLengthError: String = "More information must be 1500 characters or fewer"
  val emojiError: String     = "The additional information must not include emojis"

}
