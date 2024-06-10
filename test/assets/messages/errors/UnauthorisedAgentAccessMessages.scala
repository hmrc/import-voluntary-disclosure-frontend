/*
 * Copyright 2023 HM Revenue & Customs
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

package messages.errors

import messages.BaseMessages

object UnauthorisedAgentAccessMessages extends BaseMessages {

  val pageTitle = "You cannot access the service with this type of user ID"
  val para1 =
    "You have signed in with an Agent user ID (also known as an Agent Services account). Agent user IDs cannot be used to access this service"
  val signInAgain1    = "Sign in again "
  val signInAgain2    = "with:"
  val bullet1         = "the HMRC sign in details used to apply for your EORI number online"
  val bullet2         = "or, if you do not have this, another user ID you use for your business"
  val detailsLinkText = "I do not have another user ID"
  val details1: String =
    "You can use the link on the sign in page to create a new user ID. You should select organisation or individual " +
      "(not agent) when asked for the type of account."
  val details2: String =
    "The subscription will be unsuccessful if another user ID is already linked to your EORI number. Make sure you check one does not exist before creating a new user ID."

}
