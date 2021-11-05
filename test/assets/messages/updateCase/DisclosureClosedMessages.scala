package messages.updateCase

import messages.BaseMessages

object DisclosureClosedMessages extends BaseMessages {

  val pageTitle = "You cannot add information to this disclosure"
  val p1        = "This is because we may have already issued the C18 Post Clearance Demand Note."
  val p2        = "Instead, you need to email npcc@hmrc.gov.uk, and include:"
  val bullet1   = "the disclosure reference number C182107152124AQYVM6E34"
  val bullet2 =
    "the entry details (or where there are multiple entry details, the total amount of tax or duty that is owed)"
  val bullet3 = "the EORI number and name of the importer"
  val bullet4 = "the information you wanted to add to this disclosure"
}
