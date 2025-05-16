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

package messages.importDetails

import messages.BaseMessages

object EnterCustomsProcedureCodeMessages extends BaseMessages {

  val title: String = "What is the customs procedure code?"
  val hint: String =
    "This can be 7 numbers, or 6 numbers and a letter, for example 4000000 or 4000C10. It is box 37 on the C88 and it may be called ‘procedure’."
  val requiredError: String = "Enter the customs procedure code"
  val formatError: String   = "Enter the customs procedure code in the correct format"

}
