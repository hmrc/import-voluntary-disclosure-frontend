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

object ImporterNameMessages extends BaseMessages {

  val title: String = "What is the name of the importer?"
  val h1: String = "What is the name of the importer?"
  val hint: String = "This should match the name as it appears on the import declaration (also known as the C88 or SAD). If you are representing an individual then enter their full name."
  val nonEmpty: String = "Enter the name of the importer"
  val nameMinLength: String = "Name of the importer must be 2 characters or more"
  val nameMaxLength: String = "Name of the importer must be 50 characters or fewer"
  val emojiNotAllowed: String = "Name of the importer must not include emojis"

}
