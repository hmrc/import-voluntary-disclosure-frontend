package messages.importDetails

import messages.BaseMessages

object ImporterEORINumberMessages extends BaseMessages {

  val title: String = "What is the importer’s EORI number?"
  val hint: String =
    "This must start with GB and be followed by either 12 or 15 numbers, for example GB345834921000 or GB123456789123000."
  val nonEmpty: String        = "Enter an EORI number"
  val incorrectFormat: String = "Enter an EORI number in the correct format"

}