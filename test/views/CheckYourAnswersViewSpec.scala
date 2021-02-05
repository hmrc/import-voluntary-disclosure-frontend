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
import messages.CYAMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.CheckYourAnswersData._
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBaseSpec {

  private lazy val injectedView: CheckYourAnswersView = app.injector.instanceOf[CheckYourAnswersView]

  private val backLink: Call = Call("GET", "url")

  "Rendering the Check Your Answers page" when {
    "answers provided" should {
      lazy val view: Html = injectedView(answers, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have ${answers.size + 1} sub-heading(s)" in {
        document.select("main h2").size mustBe answers.size + 1
      }

      "have correct sub-headings" in {
        val subHeadings = document.select("main h2")
        answers.zipWithIndex.map {
          case (answer, index) => subHeadings.get(index).text mustBe answer.heading
        }
      }

//      "have only 1 Summary List" in {
//        document.select(".govuk-summary-list").size mustBe 1
//      }
//
//      "have 3 Summary List Rows" in {
//        document.select(".govuk-summary-list__row").size mustBe 3
//      }
//
//      "have correct original amount title" in {
//        document.select(".govuk-summary-list__key").eachText.get(0) mustBe UnderpaymentSummaryMessages.originalAmount
//      }
//
//      "have correct original amount value" in {
//        document.select(".govuk-summary-list__value").eachText.get(0) mustBe "£100.00"
//      }
//
//      "have correct amended amount title" in {
//        document.select(".govuk-summary-list__key").eachText.get(1) mustBe UnderpaymentSummaryMessages.amendedAmount
//      }
//
//      "have correct amended amount value" in {
//        document.select(".govuk-summary-list__value").eachText.get(1) mustBe "£1,000.00"
//      }
//
//      "have correct due amount title" in {
//        document.select(".govuk-summary-list__key").eachText.get(2) mustBe
//          UnderpaymentSummaryMessages.importVatTitle + UnderpaymentSummaryMessages.dueToHmrc
//      }
//
//      "have correct due amount value" in {
//        document.select(".govuk-summary-list__value").eachText.get(2) mustBe "£900.00"
//      }
//
//      "have correct Change link " in {
//        document.select(".govuk-summary-list__actions").text.trim mustBe
//          (UnderpaymentSummaryMessages.change + " " + UnderpaymentSummaryMessages.importVatTitle).trim
//
//        document.select(".govuk-summary-list__actions > a").attr("href") mustBe
//          controllers.routes.UnderpaymentSummaryController.onLoad().url
//      }
    }
  }

//  it should {
//
//    lazy val view: Html = injectedView(answers, backLink)(fakeRequest, messages)
//    lazy implicit val document: Document = Jsoup.parse(view.body)
//
//    s"have the correct page title" in {
//      document.title mustBe CYAMessages.title
//    }
//
//    s"have the correct h1 of '${CYAMessages.heading}'" in {
//      elementText("h1") mustBe CYAMessages.heading
//    }
//
//    "render a back link with the correct URL" in {
//      elementAttributes("#back-link") must contain("href" -> "url")
//    }
//
//    s"have the correct Accept button" in {
//      elementText(".govuk-button") mustBe CYAMessages.acceptAndSend
//    }
//
//  }
}
