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
import messages.{ConfirmReasonDetailMessages, UnderpaymentSummaryMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.ConfirmReasonData.{answers, boxNumber, itemNumber}
import views.html.ConfirmReasonDetailView

class ConfirmReasonDetailViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ConfirmReasonDetailView = app.injector.instanceOf[ConfirmReasonDetailView]

  private val backLink: Call = Call("GET", "url")


  "Rendering the UnderpaymentSummary page" when {
    "only 1 underpayment made" should {

      lazy val view: Html = injectedView(answers, backLink)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have 2 Summary List Rows" in {
        document.select(".govuk-summary-list__row").size mustBe 2
      }

      "have correct original box number title" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dt") mustBe ConfirmReasonDetailMessages.boxNumber
      }

      "have correct original item number title" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dt") mustBe ConfirmReasonDetailMessages.itemNumber
      }

      "have correct box number value" in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__value") mustBe "33"
      }

      "have correct item number value" in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__value") mustBe "1"
      }

//            "have correct original amount value" in {
//              document.select(".govuk-summary-list__value").eachText.get(0) mustBe "£100.00"
//            }
//
//            "have correct amended amount title" in {
//              document.select(".govuk-summary-list__key").eachText.get(1) mustBe UnderpaymentSummaryMessages.amendedAmount
//            }
//
//            "have correct amended amount value" in {
//              document.select(".govuk-summary-list__value").eachText.get(1) mustBe "£1,000.00"
//            }
//
//            "have correct due amount title" in {
//              document.select(".govuk-summary-list__key").eachText.get(2) mustBe
//                UnderpaymentSummaryMessages.importVatTitle + UnderpaymentSummaryMessages.dueToHmrc
//            }
//
//            "have correct due amount value" in {
//              document.select(".govuk-summary-list__value").eachText.get(2) mustBe "£900.00"
//            }

      "have correct Change link for Box Number " in {
        elementText("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.BoxNumberController.onLoad().url
            }
      "have correct Change link for Item Number " in {
        elementText("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a") mustBe
          (ConfirmReasonDetailMessages.change).trim

        document.select("#main-content > div > div > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a").attr("href") mustBe
          controllers.routes.ItemNumberController.onLoad().url
      }

          }
        }

//        it should {
//
//          lazy val view: Html = injectedView(customsDuty, importVat, exciseDuty, backLink)(fakeRequest, messages)
//          lazy implicit val document: Document = Jsoup.parse(view.body)
//
//          s"have the correct page title" in {
//            document.title mustBe UnderpaymentSummaryMessages.title
//          }
//
//          s"have the correct h1 of '${UnderpaymentSummaryMessages.h1}'" in {
//            elementText("h1") mustBe UnderpaymentSummaryMessages.h1
//          }
//
//          "render a back link with the correct URL" in {
//            elementAttributes("#back-link") must contain("href" -> "url")
//          }
//
//          s"have the correct Continue button" in {
//            elementText(".govuk-button") mustBe UnderpaymentSummaryMessages.continue
//          }
//
//        }

}
