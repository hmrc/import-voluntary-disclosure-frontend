package messages.reasons

import messages.BaseMessages

object MoreInformationMessages extends BaseMessages {

  val singleEntryTitle: String          = "Tell us the extra information about the underpayment"
  val singleEntryRequiredError: String  = "Enter the extra information about the underpayment"
  val singleEntryMaxLengthError: String = "The extra information must be 1500 characters or fewer"

  val bulkEntryTitle: String = "What were the reasons for the underpayment of tax or duty?"
  val bulkP1 = "Tell us why the duty or tax was underpaid for all the entries included in this disclosure."
  val bulkP2 = "You do not need to repeat anything that you have already provided in the file you have uploaded."
  val bulkEntryRequiredError: String  = "Enter the reasons for the underpayment of tax or duty"
  val bulkEntryMaxLengthError: String = "The reasons for the underpayment must be 1500 characters or fewer"

}
