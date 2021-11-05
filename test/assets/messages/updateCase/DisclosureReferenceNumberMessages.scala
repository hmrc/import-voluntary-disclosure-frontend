package messages.updateCase

import messages.BaseMessages

object DisclosureReferenceNumberMessages extends BaseMessages {

  val title: String = "What is the disclosure reference number?"
  val hint =
    "This is 22 characters and starts with C18, it was issued by us when the disclosure was made. It may also be included in an email from us requesting more information."
  val details =
    "You will need to email us at npcc@hmrc.gov.uk with the entry details, the EORI number and name of the importer, and the information you want to add to the disclosure."
  val mailLink              = "mailto:npcc@hmrc.gov.uk"
  val requiredError: String = "Enter the disclosure reference number"
  val formatError: String   = "Enter the disclosure reference number in the correct format, like C182107152024AQYVM6E31"

}
