package messages.paymentInfo

import messages.BaseMessages

object DefermentMessages extends BaseMessages {

  val headingOnlyVAT: String    = "How will you pay for the import VAT owed?"
  val headingVATandDuty: String = "How will you pay for the import VAT and duty owed?"
  val headingDutyOnly: String   = "How will you pay for the duty owed?"
  val hint: String =
    "This can include BACS, CHAPS, Faster Payments, cheque, or banker’s draft. More information will be sent to you with the C18 Post Clearance Demand Note."
  val payingByDeferment: String = "By duty deferment account"
  val payingByOther: String     = "Another payment method"
  val requiredError: String     = "Select how you will pay for the underpayment"

}
