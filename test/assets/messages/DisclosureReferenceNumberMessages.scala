/*
 * Copyright 2021 HM Revenue & Customs
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

package messages

object DisclosureReferenceNumberMessages extends BaseMessages {

  val title: String = "What is the disclosure reference number?"
  val label: String = "What is the disclosure reference number?"
  val hint = "This is 22 characters and starts with C18, it was issued by us when the disclosure was made. It may also be included in an email from us requesting more information."
  val requiredError: String = "Enter the disclosure reference number"
  val formatError: String = "Enter the disclosure reference number in the correct format"

}
