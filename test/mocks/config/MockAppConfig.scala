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

package mocks.config

import config.AppConfig
import play.api.i18n.Lang
import play.api.mvc.RequestHeader

object MockAppConfig
    extends MockAppConfig(
      List.empty,
      privateBetaAllowListEnabled = false,
    )

class MockAppConfig(
  override val privateBetaAllowList: List[String] = List.empty,
  override val privateBetaAllowListEnabled: Boolean = false,
) extends AppConfig {
  override val footerLinkItems: Seq[String]         = Seq("TBC")
  override val contactFormServiceIdentifier: String = "TBC"
  override val contactUrl: String                   = "TBC"
  override val surveyUrl: String                    = "TBC"
  override val host: String                         = "TBC"

  override def feedbackUrl(implicit request: RequestHeader): String =
    "/contact/beta-feedback?service=import-voluntary-disclosure"

  override val appName: String                       = "import-voluntary-disclosure-frontend"
  override val loginUrl: String                      = "TBC"
  override val signOutUrl: String                    = "TBC"
  override val loginContinueUrl: String              = "TBC"
  override val addressLookupFrontend: String         = "TBC"
  override val addressLookupCallback: String         = "/disclose-import-taxes-underpayment/address-callback"
  override val importerAddressLookupCallback: String = "/disclose-import-taxes-underpayment/importer-address-callback"
  override val addressLookupInitialise: String       = "TBC"
  override val addressLookupFeedbackUrl: String      = "TBC"
  override val addressLookupCallbackUrl: String      = "TBC"
  override val importerAddressLookupCallbackUrl: String                 = "TBC"
  override val timeoutPeriod: Int                                       = 900
  override val countdown: Int                                           = 120
  override val cacheTtl: Int                                            = 500
  override val upScanCallbackUrlForSuccessOrFailureOfFileUpload: String = "TBC"
  override val upScanSuccessRedirectForUser: String                     = "TBC"
  override val upScanSuccessRedirectForBulk: String                     = "TBC"
  override val upScanErrorRedirectForUser: String                       = "TBC"
  override val upScanErrorRedirectForBulk: String                       = "TBC"
  override val upScanMinFileSize: Int                                   = 1
  override val upScanMaxFileSize: Int                                   = 10485760
  override val upScanPollingDelayMilliSeconds: Int                      = 10
  override val upScanInitiateBaseUrl: String                            = "TBC"
  override val upScanAcceptedFileTypes: String                          = "TBC"
  override val fileRepositoryTtl: Int                                   = 86400
  override val upScanAuthoritySuccessRedirectForUser: String            = "TBC"
  override val upScanAuthorityErrorRedirectForUser: String              = "TBC"
  override val upScanSupportingDocSuccessRedirectForUser: String        = "TBC"
  override val upScanSupportingDocErrorRedirectForUser: String          = "TBC"

  override val upScanCancelCaseRedirectForUser: String         = "TBC"
  override val upScanCancelCaseDocErrorRedirectForUser: String = "TBC"

  override val importVoluntaryDisclosureSubmission: String = "TBC"

  override val eccSubscribeUrl: String = "TBC"
  override val c2001Url: String        = "TBC"
  override val en: Lang                = Lang("en")
  override val cy: Lang                = Lang("cy")
  override val defaultLanguage: Lang   = en

  override val vatReturnAdjustmentsUrl: String = "url"
  override val pvaHandoffUrl: String           = "url"
  override val c18EmailAddress: String         = "npcc@hmrc.gov.uk"

}
