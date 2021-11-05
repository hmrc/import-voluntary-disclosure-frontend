package messages.importDetails

import messages.BaseMessages

object ImporterEORIExistsMessages extends BaseMessages {

  val title: String = "Does the importer have an EORI number?"
  val hint: String =
    "This must start with GB and be followed by either 12 or 15 numbers, for example GB345834921000 or GB123456789123000."
  val requiredError: String = "Select yes if the importer has an EORI number"

}
