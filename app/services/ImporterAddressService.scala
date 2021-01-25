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

package services

import config.AppConfig
import connectors.ImporterAddressConnector
import models.{ErrorModel, TraderAddress}
import play.api.i18n.MessagesApi
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ImporterAddressService @Inject()(importerAddressConnector: ImporterAddressConnector,
                                       implicit val messagesApi: MessagesApi,
                                       implicit val appConfig: AppConfig) {

  def retrieveAddress(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, TraderAddress]] = {
    importerAddressConnector.getAddress(id)

    val result: Either[ErrorModel, TraderAddress] = try {
      Right(TraderAddress(
        streetAndNumber = Some("99 Avenue Road"),
        city = Some("Anyold Town"),
        postalCode = Some("99JZ 1AA"),
        countryCode = Some("United Kingdom")
      ))
    } catch {
      case e: Exception => Left(ErrorModel(404, "Failed"))
    }
    Future.successful(result)
  }

}
