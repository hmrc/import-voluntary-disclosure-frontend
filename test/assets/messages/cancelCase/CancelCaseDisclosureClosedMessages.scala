package messages.cancelCase

import messages.BaseMessages

object CancelCaseDisclosureClosedMessages extends BaseMessages {

  val pageTitle = "This disclosure cannot be cancelled"
  val p1 =
    "This is because we may have already issued the C18 Post Clearance Demand Note."
  val p2 =
    "Instead, you need to email npcc@hmrc.gov.uk, and include:"

  def li1(caseId: String) = s"the disclosure reference number $caseId"
  val li2 =
    "the entry details (or where there are multiple entry details, the total amount of tax or duty that is owed)"
  val li3 = "the EORI number and name of the importer"
  val li4 = "the reason for requesting the cancellation of this disclosure"
}
