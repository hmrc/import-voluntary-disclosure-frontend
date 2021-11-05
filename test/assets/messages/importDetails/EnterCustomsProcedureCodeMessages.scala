package messages.importDetails

import messages.BaseMessages

object EnterCustomsProcedureCodeMessages extends BaseMessages {

  val title: String = "What is the customs procedure code?"
  val hint: String =
    "This can be 7 numbers, or 6 numbers and a letter, for example 4000000 or 4000C10. It is box 37 on the C88 and it may be called ‘procedure’."
  val requiredError: String = "Enter the customs procedure code"
  val formatError: String   = "Enter the customs procedure code in the correct format"

}
