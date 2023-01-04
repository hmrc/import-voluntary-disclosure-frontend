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

package views.cya

import base.ViewBaseSpec
import messages.cya.CYAMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.data.CheckYourAnswersData._
import views.html.cya.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBaseSpec {

  private lazy val injectedView: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]

  "Rendering the Check Your Answers page" when {
    "multiple answers provided" should {
      lazy val view: Html                  = injectedView(answers)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have ${answers.size + 1} sub-headings" in {
        document.select("main h2").size mustBe answers.size + 1
      }

      "have correct sub-headings" in {
        val subHeadings = document.select("main h2")
        answers.zipWithIndex.map { case (answer, index) =>
          subHeadings.get(index).text mustBe answer.heading.get
        }
      }

      s"have ${answers.size} Summary Lists" in {
        document.select(".govuk-summary-list").size mustBe answers.size
      }
    }

  }

  it should {

    lazy val view: Html                  = injectedView(answers)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(CYAMessages.title)

    s"have the correct h1 of '${CYAMessages.title}'" in {
      elementText("h1") mustBe CYAMessages.title
    }

    "have Now Send Disclosure sub-heading " in {
      document.select("main h2").last.text mustBe CYAMessages.sendDisclosure
    }

    "have Now Send Disclosure message " in {
      document.select("main p").text mustBe CYAMessages.disclosureConfirmation
    }

    s"have the correct Accept button" in {
      elementText(".govuk-button") mustBe CYAMessages.acceptAndSend
    }

  }
}
