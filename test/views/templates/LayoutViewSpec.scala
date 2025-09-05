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

package views.templates

import base.ViewBaseSpec
import messages.BaseMessages
import models.UserAnswers
import models.requests.{DataRequest, IdentifierRequest, OptionalDataRequest}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.AnyContentAsEmpty
import play.twirl.api.Html
import views.html.templates.Layout

class LayoutViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val target: Layout = app.injector.instanceOf[Layout]

  val userAnswers: Option[UserAnswers] = Some(UserAnswers("some-cred-id"))

  implicit lazy val dataRequest: DataRequest[AnyContentAsEmpty.type] = DataRequest(
    OptionalDataRequest(
      IdentifierRequest(fakeRequest, "credId", "eori"),
      "credId",
      "eori",
      userAnswers
    ),
    "credId",
    "eori",
    userAnswers.get
  )

  "Rendering the layout" should {
    s"have the sign out url" in {
      lazy val markup: Html                = target("")(Html(""))
      lazy implicit val document: Document = Jsoup.parse(markup.toString)
      element("body > header > div > div > div.govuk-header__content > nav > a")
        .attr("href")
        .contains(controllers.routes.SignOutController.signOut().url) mustBe true
    }

    s"have the sign out url for unauthenticated" in {
      lazy val markup: Html                = target("", isAuthorised = false)(Html(""))
      lazy implicit val document: Document = Jsoup.parse(markup.toString)
      element("body > header > div > div > div.govuk-header__content > nav > a")
        .attr("href")
        .contains(controllers.routes.SignOutController.signOutUnidentified.url) mustBe true
    }

  }

}
