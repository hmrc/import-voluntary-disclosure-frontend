/*
 * Copyright 2022 HM Revenue & Customs
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

package messages.serviceEntry

import messages.BaseMessages

object IndividualContinueWithCredentialsMessages extends BaseMessages {

  val title         = "Are you sure you want to use this Government Gateway user ID to set up access to the service?"
  val legend        = "Ensure you have signed in with the Government Gateway user ID you use for your business."
  val yesMessage    = "Yes I want to use this user ID to set up access"
  val noMessage     = "No I want to sign in with a different user ID"
  val errorRequired = "Select yes if you want to use this user ID to set up access"

}
