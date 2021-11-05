package messages.paymentInfo

import messages.BaseMessages

object RepresentativeDanDutyMessages extends BaseMessages {

  val title: String = "Deferment account details for the duty owed"

  val accountNumberLabel: String = "Deferment account number"
  val radioButtonLabel: String   = "Whose deferment account is this?"

  val radio1: String     = "My deferment account (A)"
  val radio2: String     = "The importer’s account and I have authority to use it (B)"
  val radio2Hint: String = "You will be asked to upload proof of authority"
  val radio3: String     = "The importer’s account and I have standing authority to use it (C)"

  val accountNumberRequiredError: String = "Enter the deferment account number"
  val danTypeRequiredError: String       = "Select whose deferment account this is"
  val accountNumberFormatError: String   = "Enter the deferment account number in the correct format, like 1234567"

}
