package messages.updateCase

import messages.BaseMessages

object UpdateCaseCYAMessages extends BaseMessages {

  val title: String           = "Check your information before adding it to the disclosure"
  val sendInformation: String = "Now add your information"
  val disclosureConfirmation: String =
    "By adding this information you are confirming that, to the best of your knowledge, the details you are providing are correct."
  val addToTheDisclosure: String          = "Add to the disclosure"
  val referenceNumber: String             = "Reference number"
  val moreDocumentation: String           = "Add more documentation?"
  val fileUpload: String                  = "Add more documentation?"
  val additionalInformation: String       = "Additional information"
  val changeReferenceNumber: String       = "Change reference number"
  val changeMoreDocumentation: String     = "Change add more documentation?"
  val changeAdditionalInformation: String = "Change additional information"
  val changeUploadedFiles: String         = "Change uploaded files"

  def filesUploaded(numberOfFiles: Int): String =
    if (numberOfFiles == 1) s"$numberOfFiles file uploaded" else s"$numberOfFiles files uploaded"

}
