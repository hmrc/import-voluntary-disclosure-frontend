package forms

import forms.mappings.Mappings
import play.api.data.Form
import play.api.i18n.Messages

import javax.inject.Inject

class RepresentativeNameFormProvider @Inject() extends Mappings {

  def apply()(implicit messages: Messages): Form[String] =
    Form(
      "value" -> text("representativeName.error.required")
        .verifying(regexp("^[0-9]{4}[A-Za-z0-9][0-9]{2}$", "enterCustomsProcedureCode.cpc.error.format")) // TODO - need to find the regex for this
    )

}
