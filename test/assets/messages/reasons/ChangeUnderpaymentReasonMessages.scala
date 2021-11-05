package messages.reasons

import messages.BaseMessages

object ChangeUnderpaymentReasonMessages extends BaseMessages {

  def title(box: Int): String = s"Change box $box of the reason for underpayment"
  val otherItemTitle          = "Change other reason for underpayment"

  val h1: String          = "Underpayment amount summary"
  val itemNumber          = "Item number"
  val itemNumberChange    = "Change item number"
  val change              = "Change"
  val originalValue       = "Original value"
  val originalValueChange = "Change original value"
  val amendedValueChange  = "Change amended value"
  val otherReasonChange   = "Change other reason"
  val amendedValue        = "Amended value"
  val otherItem           = "Other reason"
  val removeLink          = "Remove this reason for underpayment"
  val backToReasons       = "Back to reasons list"

}
