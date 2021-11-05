package messages.importDetails

import messages.BaseMessages

object AcceptanceDateBulkMessages extends BaseMessages {

  val title: String = "When were the entry acceptance dates for all the entries?"
  val info: String =
    "You must not include entries where some have an entry acceptance date before and some after 1 January 2021. If the entries cross this date you will need to separate them and make two disclosures."
  val requiredError: String = "Select when the entry acceptance dates were for all the entries"
  val beforeRadio: String   = "On or before 31 December 2020"
  val afterRadio: String    = "From 1 January 2021"

}
