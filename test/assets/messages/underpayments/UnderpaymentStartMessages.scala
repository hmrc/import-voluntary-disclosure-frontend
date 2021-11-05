package messages.underpayments

import messages.BaseMessages

object UnderpaymentStartMessages extends BaseMessages {

  val pageTitle: String       = "Tell us what was underpaid"
  val p1: String              = "For each type of tax or duty, you will need to provide:"
  val oneEntryBullet1: String = "the amount that was originally paid"
  val oneEntryBullet2: String = "the amount that should have been paid"
  val bulkBullet1: String     = "the total amount that was originally paid across all the entries"
  val bulkBullet2: String     = "the total amount that should have been paid across all the entries"
  val representativeParagraph =
    "Do not use this service to tell us about underpaid import VAT if ABC ltd uses postponed VAT accounting. Instead, they must account for the underpaid import VAT on their next VAT return."
  val importerParagraph =
    "Do not use this service to tell us about underpaid import VAT if ABC ltd uses postponed VAT accounting. Instead you must account for the underpaid import VAT on your next VAT return."

}
