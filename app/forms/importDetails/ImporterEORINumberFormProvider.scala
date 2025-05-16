/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms.importDetails

import forms.mappings.Mappings
import forms.utils.FormHelpers
import javax.inject.Inject
import play.api.data.Form

class ImporterEORINumberFormProvider @Inject() extends Mappings with FormHelpers {

  def apply(importerName: String): Form[String] =
    Form(
      "importerEORI" -> text("importerEORINumber.error.nonEmpty", Seq(importerName))
        .transform[String](toUpperNoSpaces(_), toUpperNoSpaces(_))
        .verifying(
          regexp("\\{*^(?i)GB([0-9]{12}|[0-9]{15})$\\}*", "importerEORINumber.error.incorrectFormat", Seq(importerName))
        )
    )

}
