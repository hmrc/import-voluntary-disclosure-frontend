package messages.importDetails

import messages.BaseMessages

object NumberOfEntriesMessages extends BaseMessages {

  val title: String          = "How many entries are you disclosing an underpayment for?"
  val radioButtonOne: String = "One entry"
  val radioButtonTwo: String = "More than one entry"
  val hint: String           = "All the entries must be for the same importer."
  val requiredError: String  = "Select if you are disclosing an underpayment for one entry or more than one entry"

}
