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

package views.cya

import base.ViewBaseSpec
import messages.cya.SummaryForPrintMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.data.CheckYourAnswersData._
import views.html.cya.SummaryForPrint

class SummaryForPrintSpec extends ViewBaseSpec {

  private lazy val injectedView: SummaryForPrint = app.injector.instanceOf[SummaryForPrint]

  "Rendering the summary for print" when {
    "multiple answers provided" should {
      lazy val view: Html                  = injectedView(answers)(messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct h1 of '${SummaryForPrintMessages.yourDisclosure}'" in {
        elementText("h1") mustBe SummaryForPrintMessages.yourDisclosure
      }

      "have correct sub-headings" in {
        val subHeadings = document.select("h2")
        answers.zipWithIndex.map { case (answer, index) =>
          subHeadings.get(index).text mustBe answer.heading.get
        }
      }

      s"have ${answers.size} Summary Lists" in {
        document.select(".govuk-summary-list").size mustBe answers.size
      }
    }
  }
}
