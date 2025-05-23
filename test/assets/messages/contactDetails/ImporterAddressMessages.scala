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

package messages.contactDetails

import messages.BaseMessages

object ImporterAddressMessages extends BaseMessages {

  val pageTitle     = "Is this the correct address for"
  val p             = "We will send the C18 demand note to both"
  val errorRequired = "Select yes if this is the correct address for Traders ltd"

  def getTitle(traderName: String) = s"$pageTitle $traderName?"

  def getParagraph(traderName: String, importerName: Option[String]) = s"$p $traderName and ${importerName.get}"

}
