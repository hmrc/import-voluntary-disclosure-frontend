package messages.paymentInfo

import messages.BaseMessages

object SplitPaymentMessages extends BaseMessages {

  val title: String         = "Do you want to split payment between two deferment accounts?"
  val radioYes: String      = "Yes, I want to use two deferment accounts"
  val radioNo: String       = "No, I want to use one deferment account"
  val requiredError: String = "Select yes if you want to split payment between two deferment accounts"

}
