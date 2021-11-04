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

package controllers

import config.AppConfig
import uk.gov.hmrc.play.language.{LanguageController, LanguageUtils}
import play.api.mvc._
import play.api.i18n.Lang
import com.google.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageSwitchController @Inject() (appConfig: AppConfig, languageUtils: LanguageUtils, cc: ControllerComponents)
    extends LanguageController(languageUtils, cc) {
  import appConfig._

  override def fallbackURL: String = s"$host/disclose-import-taxes-underpayment"

  override protected def languageMap: Map[String, Lang] =
    if (appConfig.welshToggleEnabled) Map(en.code -> en, cy.code -> cy)
    else Map(en.code                              -> en)

}