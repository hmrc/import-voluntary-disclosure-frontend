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

object OneCustomsProcedureCodeMessages {

  val title: String = "Is there only one customs procedure code for this entry?"
  val hint: String =
    "The customs procedure code may be called ‘procedure’ and is box 37 on the import declaration (sometimes called the Single Administrative Document (SAD) or C88)."
  val requiredError: String = "Select yes if there is only one customs procedure code for this entry"

}
