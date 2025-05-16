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

package views.cancelCase

import base.ViewBaseSpec
import messages.cancelCase.CancelCaseCYAMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.data.cya.CancelCaseCheckYourAnswersData._
import views.html.cancelCase.CancelCaseCheckYourAnswersView

class CancelCaseCheckYourAnswersViewSpec extends ViewBaseSpec {

  private lazy val injectedView: CancelCaseCheckYourAnswersView =
    app.injector.instanceOf[CancelCaseCheckYourAnswersView]

  "Rendering the Check Your Answers page" when {
    "multiple answers provided" should {
      lazy val view: Html                  = injectedView(answers)(fakeRequest, messages)
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

    lazy val view: Html                  = injectedView(answers)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(CancelCaseCYAMessages.title)

    s"have the correct h1 of '${CancelCaseCYAMessages.title}'" in {
      elementText("h1") mustBe CancelCaseCYAMessages.title
    }

    "have Now send your cancellation request sub-heading " in {
      document.select("main h2").last.text mustBe CancelCaseCYAMessages.sendInformation
    }

    "have correct disclosure confirmation message " in {
      document.select("main p").text mustBe CancelCaseCYAMessages.disclosureConfirmation
    }

    s"have the correct Accept button" in {
      elementText(".govuk-button") mustBe CancelCaseCYAMessages.accept
    }

  }
}
