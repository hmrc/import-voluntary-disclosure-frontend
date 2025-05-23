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

package assets

import messages.BaseMessages
import models.addressLookup.{AddressLookupJsonBuilder, AddressModel, ImporterAddressLookupJsonBuilder}
import play.api.libs.json.{JsObject, Json}

object AddressLookupTestConstants extends BaseMessages {

  val phaseBannerHtml =
    "This is a new service – your <a id='beta-banner-feedback' href='TBC'>feedback</a> will help us to improve it."

  val addressLine1 = "line 1"
  val addressLine2 = "line 2"
  val addressLine3 = "line 3"
  val addressLine4 = "line 4"
  val postcode     = "aa1 1aa"
  val countryName  = "United Kingdom"
  val countryCode  = "UK"

  val customerAddressMax: AddressModel = AddressModel(
    Some(addressLine1),
    Some(addressLine2),
    Some(addressLine3),
    Some(addressLine4),
    Some(postcode),
    Some(countryCode)
  )

  val customerAddressSome: AddressModel = AddressModel(
    Some(addressLine1),
    Some(addressLine2),
    Some(addressLine3),
    None,
    Some(postcode),
    Some(countryCode)
  )

  val customerAddressMissingLine3: AddressModel = AddressModel(
    Some(addressLine1),
    Some(addressLine2),
    Some(addressLine4),
    None,
    Some(postcode),
    Some(countryCode)
  )

  val customerAddressMin: AddressModel = AddressModel(None, None, None, None, None, None)

  val customerAddressJsonMax: JsObject = Json.obj(
    "address" -> Json.obj(
      "lines"    -> Json.arr(addressLine1, addressLine2, addressLine3, addressLine4),
      "postcode" -> postcode,
      "country" -> Json.obj(
        "name" -> countryName,
        "code" -> countryCode
      )
    )
  )

  val customerAddressJsonSome: JsObject = Json.obj(
    "address" -> Json.obj(
      "lines"    -> Json.arr(addressLine1, addressLine2, addressLine3),
      "postcode" -> postcode,
      "country" -> Json.obj(
        "name" -> countryName,
        "code" -> countryCode
      )
    )
  )

  val customerAddressJsonMin: JsObject = Json.obj()

  val customerAddressToJsonMax: JsObject = Json.obj(
    "line1"       -> addressLine1,
    "line2"       -> addressLine2,
    "line3"       -> addressLine3,
    "line4"       -> addressLine4,
    "postcode"    -> postcode,
    "countryCode" -> countryCode
  )

  val customerAddressToJsonMin: JsObject = Json.obj()

  val customerAddressJsonError: JsObject = Json.obj(
    "address" -> Json.obj(
      "lines" -> 4
    )
  )

  def addressLookupV2Json(builder: AddressLookupJsonBuilder): JsObject = Json.obj(
    fields = "version" -> 2,
    "options" -> Json.obj(
      "continueUrl"            -> "/lookup-address/confirmed",
      "serviceHref"            -> "/disclose-import-taxes-underpayment",
      "accessibilityFooterUrl" -> "/accessibility-statement/import-voluntary-disclosure",
      "deskProServiceName"     -> "TBC",
      "showPhaseBanner"        -> true,
      "ukMode"                 -> false,
      "timeoutConfig" -> Json.obj(
        "timeoutAmount" -> 900,
        "timeoutUrl"    -> "/disclose-import-taxes-underpayment/timeout-signed-out"
      ),
      "confirmPageConfig" -> Json.obj(
        "showSubHeadingAndInfo" -> true,
        "showSearchAgainLink"   -> true
      )
    ),
    "labels" -> Json.obj(
      "en" -> Json.obj(
        "appLevelLabels" -> Json.obj(
          "navTitle"        -> builder.Version2.navTitle(builder.Version2.eng),
          "phaseBannerHtml" -> builder.Version2.phaseBannerHtml(builder.Version2.eng)
        ),
        "selectPageLabels"  -> builder.Version2.selectPageLabels(builder.Version2.eng),
        "lookupPageLabels"  -> builder.Version2.lookupPageLabels(builder.Version2.eng),
        "confirmPageLabels" -> builder.Version2.confirmPageLabels(builder.Version2.eng),
        "editPageLabels"    -> builder.Version2.editPageLabels(builder.Version2.eng)
      ),
      "cy" -> Json.obj(
        "appLevelLabels" -> Json.obj(
          "navTitle"        -> builder.Version2.navTitle(builder.Version2.wel),
          "phaseBannerHtml" -> builder.Version2.phaseBannerHtml(builder.Version2.wel)
        ),
        "selectPageLabels"  -> builder.Version2.selectPageLabels(builder.Version2.wel),
        "lookupPageLabels"  -> builder.Version2.lookupPageLabels(builder.Version2.wel),
        "confirmPageLabels" -> builder.Version2.confirmPageLabels(builder.Version2.wel),
        "editPageLabels"    -> builder.Version2.editPageLabels(builder.Version2.wel)
      )
    )
  )

  def importerAddressLookupV2Json(builder: ImporterAddressLookupJsonBuilder): JsObject = Json.obj(
    fields = "version" -> 2,
    "options" -> Json.obj(
      "continueUrl"            -> "/lookup-address/confirmed",
      "serviceHref"            -> "/disclose-import-taxes-underpayment",
      "accessibilityFooterUrl" -> "/accessibility-statement/import-voluntary-disclosure",
      "deskProServiceName"     -> "TBC",
      "showPhaseBanner"        -> true,
      "ukMode"                 -> false,
      "timeoutConfig" -> Json.obj(
        "timeoutAmount" -> 900,
        "timeoutUrl"    -> "/disclose-import-taxes-underpayment/timeout-signed-out"
      ),
      "confirmPageConfig" -> Json.obj(
        "showSubHeadingAndInfo" -> true,
        "showSearchAgainLink"   -> true
      )
    ),
    "labels" -> Json.obj(
      "en" -> Json.obj(
        "appLevelLabels" -> Json.obj(
          "navTitle"        -> builder.Version2.navTitle(builder.Version2.eng),
          "phaseBannerHtml" -> builder.Version2.phaseBannerHtml(builder.Version2.eng)
        ),
        "selectPageLabels"  -> builder.Version2.selectPageLabels(builder.Version2.eng),
        "lookupPageLabels"  -> builder.Version2.lookupPageLabels(builder.Version2.eng),
        "confirmPageLabels" -> builder.Version2.confirmPageLabels(builder.Version2.eng),
        "editPageLabels"    -> builder.Version2.editPageLabels(builder.Version2.eng)
      ),
      "cy" -> Json.obj(
        "appLevelLabels" -> Json.obj(
          "navTitle"        -> builder.Version2.navTitle(builder.Version2.wel),
          "phaseBannerHtml" -> builder.Version2.phaseBannerHtml(builder.Version2.wel)
        ),
        "selectPageLabels"  -> builder.Version2.selectPageLabels(builder.Version2.wel),
        "lookupPageLabels"  -> builder.Version2.lookupPageLabels(builder.Version2.wel),
        "confirmPageLabels" -> builder.Version2.confirmPageLabels(builder.Version2.wel),
        "editPageLabels"    -> builder.Version2.editPageLabels(builder.Version2.wel)
      )
    )
  )

}
