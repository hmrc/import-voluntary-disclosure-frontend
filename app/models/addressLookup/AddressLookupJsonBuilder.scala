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

package models.addressLookup

import config.AppConfig
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.libs.json._

case class AddressLookupJsonBuilder(continueUrl: String, eoriName: String)(implicit
  messagesApi: MessagesApi,
  config: AppConfig
) {

  // general journey overrides
  val showPhaseBanner: Boolean       = true
  val ukMode: Boolean                = false
  val conf: AppConfig                = config
  val deskproServiceName: String     = conf.contactFormServiceIdentifier
  val accessibilityFooterUrl: String = "/accessibility-statement/import-voluntary-disclosure"
  val serviceHref: String            = "/disclose-import-taxes-underpayment"

  object Version2 {

    val eng: Messages = MessagesImpl(Lang("en"), messagesApi)
    val wel: Messages = MessagesImpl(Lang("cy"), messagesApi)

    val version: Int = 2

    val navTitle: Messages => String = message => message("service.name")

    val confirmPageConfig: JsObject = Json.obj(
      "showSubHeadingAndInfo" -> true,
      "showSearchAgainLink"   -> true
    )

    val timeoutConfig: JsObject = Json.obj(
      "timeoutAmount" -> conf.timeoutPeriod,
      "timeoutUrl"    -> "/disclose-import-taxes-underpayment/timeout-signed-out"
    )
    val selectPageLabels: Messages => JsObject = message =>
      Json.obj(
        "title"               -> message("address_lookupPage.selectPage.pageTitle", eoriName),
        "heading"             -> message("address_lookupPage.selectPage.pageTitle", eoriName),
        "submitLabel"         -> message("common.continue"),
        "editAddressLinkText" -> message("address_lookupPage.selectPage.editLink")
      )

    val lookupPageLabels: Messages => JsObject = message =>
      Json.obj(
        "title"         -> message("address_lookupPage.pageTitle", eoriName),
        "heading"       -> message("address_lookupPage.pageTitle", eoriName),
        "filterLabel"   -> message("address_lookupPage.filter"),
        "postcodeLabel" -> message("address_lookupPage.postcode"),
        "submitLabel"   -> message("address_lookupPage.lookupPage.submit")
      )

    val confirmPageLabels: Messages => JsObject = message =>
      Json.obj(
        "title"                 -> message("address_lookupPage.confirmPage.pageTitle", eoriName),
        "heading"               -> message("address_lookupPage.confirmPage.pageTitle", eoriName),
        "infoMessage"           -> message("address_lookupPage.confirmPage.infoMessage"),
        "showConfirmChangeText" -> false
      )

    val editPageLabels: Messages => JsObject = message =>
      Json.obj(
        "heading"     -> message("address_lookupPage.editPage.pageTitle", eoriName),
        "townLabel"   -> message("address_lookupPage.editPage.townOrCity"),
        "submitLabel" -> message("common.continue")
      )

    val phaseBannerHtml: Messages => String = message =>
      s"${message("feedback.before")}" +
        s" <a id='beta-banner-feedback' href='${config.addressLookupFeedbackUrl}'>${message("feedback.link")}</a>" +
        s" ${message("feedback.after")}"
  }

}

object AddressLookupJsonBuilder {

  implicit val writes: Writes[AddressLookupJsonBuilder] = new Writes[AddressLookupJsonBuilder] {
    def writes(data: AddressLookupJsonBuilder): JsObject = {
      Json.obj(
        fields = "version" -> 2,
        "options" -> Json.obj(
          "continueUrl"            -> data.continueUrl,
          "serviceHref"            -> data.serviceHref,
          "accessibilityFooterUrl" -> data.accessibilityFooterUrl,
          "deskProServiceName"     -> data.deskproServiceName,
          "showPhaseBanner"        -> data.showPhaseBanner,
          "ukMode"                 -> data.ukMode,
          "timeoutConfig"          -> data.Version2.timeoutConfig,
          "confirmPageConfig"      -> data.Version2.confirmPageConfig
        ),
        "labels" -> Json.obj(
          "en" -> Json.obj(
            "appLevelLabels" -> Json.obj(
              "navTitle"        -> data.Version2.navTitle(data.Version2.eng),
              "phaseBannerHtml" -> data.Version2.phaseBannerHtml(data.Version2.eng)
            ),
            "selectPageLabels"  -> data.Version2.selectPageLabels(data.Version2.eng),
            "lookupPageLabels"  -> data.Version2.lookupPageLabels(data.Version2.eng),
            "confirmPageLabels" -> data.Version2.confirmPageLabels(data.Version2.eng),
            "editPageLabels"    -> data.Version2.editPageLabels(data.Version2.eng)
          ),
          "cy" -> Json.obj(
            "appLevelLabels" -> Json.obj(
              "navTitle"        -> data.Version2.navTitle(data.Version2.wel),
              "phaseBannerHtml" -> data.Version2.phaseBannerHtml(data.Version2.wel)
            ),
            "selectPageLabels"  -> data.Version2.selectPageLabels(data.Version2.wel),
            "lookupPageLabels"  -> data.Version2.lookupPageLabels(data.Version2.wel),
            "confirmPageLabels" -> data.Version2.confirmPageLabels(data.Version2.wel),
            "editPageLabels"    -> data.Version2.editPageLabels(data.Version2.wel)
          )
        )
      )
    }
  }
}
