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

package filters

import org.apache.pekko.stream.Materializer
import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.NotFound
import play.api.mvc.{Filter, Request, RequestHeader, Result}
import play.api.{Configuration, Logging}

import scala.concurrent.Future

class AllowListFilter @Inject() (
  configuration: Configuration,
  handler: config.ErrorHandler,
  val messagesApi: MessagesApi
)(implicit val mat: Materializer)
    extends Filter
    with I18nSupport
    with Logging {

  private val allowedPaths = Seq(
    "/disclose-import-taxes-underpayment/assets/", // assets (.css & .js)
    "/ping/ping"                                   // health check route
  )

  override def apply(next: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {

    implicit val request: Request[_] = Request(rh, "")

    val enabled: Boolean = configuration.get[Boolean]("features.allowListActive")
    val allowedList      = allowedPaths.exists(rh.path.startsWith)

    if (enabled && !allowedList) {
      logger.info(s"Restricted access to path: ${rh.path}")
      Future.successful(NotFound(handler.notFoundTemplate(request)))
    } else {
      next(rh)
    }
  }
}
