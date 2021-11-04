package messages.contactDetails

import messages.BaseMessages

object TraderContactDetailsMessages extends BaseMessages {

  val title: String = "What are your contact details?"
  val text: String  = "We will only use these details if we have questions about this underpayment disclosure."
  val phoneNumberHint: String = "For international numbers include the country code."

  val errorNameNonEmpty: String            = "Enter a name"
  val errorEmailNonEmpty: String           = "Enter an email address"
  val errorPhoneNumberNonEmpty: String     = "Enter a telephone number"
  val errorNameMinLength: String           = "Name must be 2 characters or more"
  val errorNameMaxLength: String           = "Name must be 50 characters or fewer"
  val errorNameAllowableCharacters: String = "Name must only include letters a to z, hyphens, spaces and apostrophes"
  val errorEmailInvalidFormat: String      = "Enter an email address in the correct format, like name@example.com"
  val errorPhoneNumberInvalidFormat: String =
    "Enter a telephone number, like 01632 960 001, 07700 900 982 or +44 0808 157 0192"

}
