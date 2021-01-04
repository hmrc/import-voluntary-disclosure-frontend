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

package views

import java.time.LocalDateTime

import forms.UserTypeFormProvider
import messages.{BaseMessages, UserTypeMessages}
import models.UserAnswers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.json.Json
import play.twirl.api.Html
import views.html.UserTypeView

class UserTypeViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UserTypeView = app.injector.instanceOf[UserTypeView]

  val userAnswers: UserAnswers = UserAnswers(
    "123456",
    Json.obj("value" -> "importer"),
    LocalDateTime.now()
  )
  val formProvider = injector.instanceOf[UserTypeFormProvider]
  val form = formProvider.apply()

  "Rendering the UserType page" when {

    lazy val view: Html = injectedView(form, userAnswers)(fakeRequest, messages)

    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct page heading of '${UserTypeMessages.title}'" in {
      document.title mustBe UserTypeMessages.title
    }

    s"have the correct h1 of '${UserTypeMessages.h1}'" in {
      elementText("h1") mustBe UserTypeMessages.h1
    }

    s"have the correct value for the first radio button of '${UserTypeMessages.radioButtonOne}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe UserTypeMessages.radioButtonOne
    }

    s"have the correct value for the second radio button of '${UserTypeMessages.radioButtonTwo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe
        s"${UserTypeMessages.radioButtonTwo}"
    }
  }
}
