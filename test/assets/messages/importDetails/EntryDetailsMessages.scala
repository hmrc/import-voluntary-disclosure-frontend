package messages.importDetails

import messages.BaseMessages

object EntryDetailsMessages extends BaseMessages {

  val title: String         = "Entry details"
  val paragraph             = "These can be found on the E2."
  val paragraphWhenImporter = "These can be found on the E2 or by asking your shipping agent or courier."

  val epuLabel: String = "Entry Processing Unit (EPU) number"
  val epuHint: String  = "This is 3 numbers, for example 121."

  val entryNumberLabel: String = "Entry number"
  val entryNumberHint: String  = "This is 6 numbers and a letter, for example 123456Q."

  val entryDateLabel: String      = "Entry date"
  val entryDateHint: String       = "For example, 27 3 2020"
  val entryDateDayLabel: String   = "Day"
  val entryDateMonthLabel: String = "Month"
  val entryDateYearLabel: String  = "Year"

  val epuRequiredError: String                = "Enter an EPU number"
  val entryNumberRequiredError: String        = "Enter an entry number"
  val entryDateAllRequiredError: String       = "Enter an entry date and include a day, month and year"
  val entryDateDayRequiredError: String       = "Entry date must include a day"
  val entryDateMonthRequiredError: String     = "Entry date must include a month"
  val entryDateYearRequiredError: String      = "Entry date must include a year"
  val entryDateDayMonthRequiredError: String  = "Entry date must include a day and month"
  val entryDateDayYearRequiredError: String   = "Entry date must include a day and year"
  val entryDateMonthYearRequiredError: String = "Entry date must include a month and year"

  val epuFormatError: String         = "Enter an EPU number in the correct format"
  val entryNumberFormatError: String = "Enter an entry number in the correct format"

  val entryDatePastError: String = "Entry date must be today or in the past"
  val entryDateRealError: String = "Entry date must be a real date"

  val afterDateYearError: String = "Entry date must be after 1 January 1900"

}
