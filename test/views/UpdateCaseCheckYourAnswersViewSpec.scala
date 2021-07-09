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

import base.ViewBaseSpec
import messages.UpdateCaseCYAMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.data.cya.UpdateCaseCheckYourAnswersData._
import views.html.UpdateCaseCheckYourAnswersView

class UpdateCaseCheckYourAnswersViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UpdateCaseCheckYourAnswersView = app.injector.instanceOf[UpdateCaseCheckYourAnswersView]

  "Rendering the Check Your Answers page" when {
    "multiple answers provided" should {
      lazy val view: Html = injectedView(answers)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have ${answers.size} sub-headings" in {
        document.select("main h2").size mustBe answers.size
      }

      s"have ${answers.size} Summary Lists" in {
        document.select(".govuk-summary-list").size mustBe answers.size
      }
    }

  }

  it should {

      lazy val view: Html = injectedView(answers)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UpdateCaseCYAMessages.title)

      s"have the correct h1 of '${UpdateCaseCYAMessages.heading}'" in {
        elementText("h1") mustBe UpdateCaseCYAMessages.heading
      }

      "have Now add your information sub-heading " in {
        document.select("main h2").last.text mustBe UpdateCaseCYAMessages.sendInformation
      }

      "have Now add your information message " in {
        document.select("main p").text mustBe UpdateCaseCYAMessages.disclosureConfirmation
      }

      s"have the correct Accept button" in {
        elementText(".govuk-button") mustBe UpdateCaseCYAMessages.addToTheDisclosure
      }

    }
}
