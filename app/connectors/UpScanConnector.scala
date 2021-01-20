/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package connectors

import config.AppConfig
import connectors.httpParsers.upscan.UpScanInitiateHttpParser.{UpScanInitiateResponseReads, UpscanInitiateResponse}
import javax.inject.Inject
import models.upscan.UpScanInitiateRequest
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import scala.concurrent.{ExecutionContext, Future}

class UpScanConnector @Inject()(httpClient: HttpClient, appConfig: AppConfig) {

  lazy val urlForPostInitiate: String = s"${appConfig.upScanInitiateBaseUrl}/upscan/v2/initiate"

    def postToInitiate(body: UpScanInitiateRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[UpscanInitiateResponse] = {
    httpClient.POST(urlForPostInitiate, body)(UpScanInitiateRequest.jsonWrites, UpScanInitiateResponseReads, hc, ec)
  }
}
