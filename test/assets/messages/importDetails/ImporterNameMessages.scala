package messages.importDetails

import messages.BaseMessages

object ImporterNameMessages extends BaseMessages {

  val title: String = "What is the name of the importer?"
  val hint: String =
    "This should match the name as it appears on the import declaration (also known as the C88 or SAD). If you are representing an individual then enter their full name."
  val nonEmpty: String        = "Enter the name of the importer"
  val nameMinLength: String   = "Name of the importer must be 2 characters or more"
  val nameMaxLength: String   = "Name of the importer must be 50 characters or fewer"
  val emojiNotAllowed: String = "Name of the importer must not include emojis"

}
