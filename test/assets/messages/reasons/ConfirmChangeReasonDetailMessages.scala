package messages.reasons

import messages.BaseMessages

object ConfirmChangeReasonDetailMessages extends BaseMessages {

  def title(boxNumber: Int) = s"Confirm the changes to box $boxNumber reason for underpayment"

  val otherReasonTitle = "Confirm the changes to the reason for underpayment"

  val itemNumber    = "Item number"
  val originalValue = "Original value"
  val amendedValue  = "Amended value"
  val change        = "Change"

}
