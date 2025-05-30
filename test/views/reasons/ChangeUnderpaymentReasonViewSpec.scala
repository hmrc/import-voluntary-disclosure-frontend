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

package views.reasons

import base.ViewBaseSpec
import messages.reasons.ChangeUnderpaymentReasonMessages
import models.reasons.BoxNumber
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.Html
import views.data.reasons.ChangeUnderpaymentReasonData._
import views.html.reasons.ChangeUnderpaymentReasonView

class ChangeUnderpaymentReasonViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ChangeUnderpaymentReasonView = app.injector.instanceOf[ChangeUnderpaymentReasonView]

  private val backLink: Call = Call("GET", "url")

  "Rendering the ChangeUnderpaymentReasonView page" when {
    "showing underpayment with item number" should {

      val title           = messages("changeUnderpaymentReason.pageTitle", singleItemReason.original.boxNumber.id)
      lazy val view: Html = injectedView(backLink, summaryList(BoxNumber.Box22), title)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ChangeUnderpaymentReasonMessages.title(singleItemReason.original.boxNumber.id))

      "have correct heading" in {
        document.select("h1").text mustBe ChangeUnderpaymentReasonMessages.title(singleItemReason.original.boxNumber.id)
      }

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have correct item number title" in {
        document.select(".govuk-summary-list__key").eachText.get(0) mustBe ChangeUnderpaymentReasonMessages.itemNumber
      }

      "have correct item number value" in {
        document.select(".govuk-summary-list__value").eachText.get(
          0
        ) mustBe singleItemReason.original.itemNumber.toString
      }

      "have correct original amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(
          1
        ) mustBe ChangeUnderpaymentReasonMessages.originalValue
      }

      "have correct original amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(1) mustBe singleItemReason.original.original
      }

      "have correct amended amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(2) mustBe ChangeUnderpaymentReasonMessages.amendedValue
      }

      "have correct amended amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(2) mustBe singleItemReason.original.amended
      }

      "have correct Change links" in {
        document.select(".govuk-summary-list__actions").eachText.get(0).trim mustBe
          ChangeUnderpaymentReasonMessages.change + " " + ChangeUnderpaymentReasonMessages.itemNumberChange

        document.select(".govuk-summary-list__actions").eachText.get(1).trim mustBe
          ChangeUnderpaymentReasonMessages.change + " " + ChangeUnderpaymentReasonMessages.originalValueChange

        document.select(".govuk-summary-list__actions").eachText.get(2).trim mustBe
          ChangeUnderpaymentReasonMessages.change + " " + ChangeUnderpaymentReasonMessages.amendedValueChange

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(0) mustBe
          controllers.reasons.routes.ChangeItemNumberController.onLoad().url

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(1) mustBe
          controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(22).url

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(2) mustBe
          controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(22).url
      }
    }

    "showing underpayment without item number" should {

      val title = messages("changeUnderpaymentReason.pageTitle", singleEntryLevelReason.original.boxNumber.id)
      lazy val view: Html =
        injectedView(backLink, entryLevelSummaryList(BoxNumber.Box35), title)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ChangeUnderpaymentReasonMessages.title(singleEntryLevelReason.original.boxNumber.id))

      "have correct heading" in {
        document.select("h1").text mustBe ChangeUnderpaymentReasonMessages.title(
          singleEntryLevelReason.original.boxNumber.id
        )
      }

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have correct original amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(
          0
        ) mustBe ChangeUnderpaymentReasonMessages.originalValue
      }

      "have correct original amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(0) mustBe singleEntryLevelReason.original.original
      }

      "have correct amended amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(1) mustBe ChangeUnderpaymentReasonMessages.amendedValue
      }

      "have correct amended amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(1) mustBe singleEntryLevelReason.original.amended
      }

      "have correct Change link" in {
        document.select(".govuk-summary-list__actions").eachText.get(0).trim mustBe
          ChangeUnderpaymentReasonMessages.change + " " + ChangeUnderpaymentReasonMessages.change

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(0) mustBe
          controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(35).url
      }
    }

    "showing underpayment for Other Item" should {

      val title           = messages("changeUnderpaymentReason.otherReasonTitle")
      lazy val view: Html = injectedView(backLink, otherItemSummaryList(), title)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ChangeUnderpaymentReasonMessages.otherItemTitle)

      "have correct heading" in {
        document.select("h1").text mustBe ChangeUnderpaymentReasonMessages.otherItemTitle
      }

      "have only 1 Summary List" in {
        document.select(".govuk-summary-list").size mustBe 1
      }

      "have correct original amount title" in {
        document.select(".govuk-summary-list__key").eachText.get(0) mustBe ChangeUnderpaymentReasonMessages.otherItem
      }

      "have correct original amount value" in {
        document.select(".govuk-summary-list__value").eachText.get(0) mustBe otherItemReason.original.original
      }

      "have correct Change link" in {
        document.select(".govuk-summary-list__actions").eachText.get(0).trim mustBe
          ChangeUnderpaymentReasonMessages.change + " " + ChangeUnderpaymentReasonMessages.otherReasonChange

        document.select(".govuk-summary-list__actions > a").eachAttr("href").get(0) mustBe
          controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onLoad(99).url
      }
    }

  }

  it should {

    val title           = messages("changeUnderpaymentReason.pageTitle", BoxNumber.Box22.id)
    lazy val view: Html = injectedView(backLink, summaryList(BoxNumber.Box22), title)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    "have the remove link with the correct URL" in {
      elementAttributes("#remove-link") must contain(
        "href" -> controllers.reasons.routes.RemoveUnderpaymentReasonController.onLoad().url
      )
    }

    "have the remove link with the correct message" in {
      elementText("#remove-link") mustBe ChangeUnderpaymentReasonMessages.removeLink
    }

    "have the correct Continue button link" in {
      elementAttributes(".govuk-button") must contain(
        "href" -> controllers.reasons.routes.UnderpaymentReasonSummaryController.onLoad().url
      )
    }

    "have the correct Continue button message" in {
      elementText(".govuk-button") mustBe ChangeUnderpaymentReasonMessages.backToReasons
    }

  }
}
