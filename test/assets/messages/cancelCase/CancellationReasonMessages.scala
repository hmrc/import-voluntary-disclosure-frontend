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

package messages.cancelCase

import messages.BaseMessages

object CancellationReasonMessages extends BaseMessages {

  val title: String = "Tell us why you want this disclosure cancelled"
  val h1: String = "Tell us why you want this disclosure cancelled"
  val requiredError: String = "Enter why you want this disclosure cancelled"
  val maxLengthError: String = "Why you want this disclosure cancelled must be 1400 characters or fewer"
  val emojiError: String = "Why you want this disclosure cancelled must not include emojis"
}
