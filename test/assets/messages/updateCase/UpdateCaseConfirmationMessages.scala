package messages.updateCase

import messages.BaseMessages

object UpdateCaseConfirmationMessages extends BaseMessages {

  val pageTitle = "Confirmation"
  val heading   = "Information added"

  def paragraph(caseId: String): String =
    s"We have received the additional information for disclosure reference number: $caseId."

  val whatHappensNext = "What happens next"
  val whatHappensNextParagraph =
    "We will check the information you have provided and send the C18 Post Clearance Demand Note in the post."
  val whatYouShouldDoNext = "What you should do next"
  val whatYouShouldDoNextParagraph =
    "If you have not received the demand note or we have not contacted you within 14 days then email npcc@hmrc.gov.uk."
  val helpImproveServiceLink   = "What did you think of this service?"
  val startNewUnderpaymentLink = "Start a new, add to, or cancel a disclosure"

}
