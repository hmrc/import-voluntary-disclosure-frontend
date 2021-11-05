package messages.reasons

import messages.BaseMessages

object OtherReasonAmendmentMessages extends BaseMessages {

  val title: String          = "What was the reason for the underpayment?"
  val requiredError: String  = "Enter the reason for the underpayment"
  val maxLengthError: String = "The reason for the underpayment must be 1500 characters or fewer"
  val noEmojiError: String   = "The reason for the underpayment must not include emojis"

}
