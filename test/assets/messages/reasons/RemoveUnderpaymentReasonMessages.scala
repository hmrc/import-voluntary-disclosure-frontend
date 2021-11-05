package messages.reasons

import messages.BaseMessages

object RemoveUnderpaymentReasonMessages extends BaseMessages {

  val title: String         = "Are you sure you want to remove this reason for underpayment?"
  val radioYes: String      = "Yes"
  val radioNo: String       = "No"
  val requiredError: String = "Select yes to remove this reason for underpayment"

  def box35Paragraph(itemNumber: Int): String = s"Box 35 gross mass amendment for item $itemNumber"

}
