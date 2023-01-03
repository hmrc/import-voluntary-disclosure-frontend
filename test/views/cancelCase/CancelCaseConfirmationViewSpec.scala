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

package views.cancelCase

import base.ViewBaseSpec
import messages.cancelCase.CancelCaseConfirmationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.cancelCase.CancelCaseConfirmationView

class CancelCaseConfirmationViewSpec extends ViewBaseSpec {

  private lazy val injectedView: CancelCaseConfirmationView = app.injector.instanceOf[CancelCaseConfirmationView]
  private val referenceNumber: String                       = "C181234567890123456789"

  "Rendering the Cancel Case Confirmation page" when {

    "it" should {
      lazy val view: Html                  = injectedView(referenceNumber)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(CancelCaseConfirmationMessages.pageTitle)

      s"have the correct page heading of '${CancelCaseConfirmationMessages.heading}'" in {
        elementText("h1") mustBe CancelCaseConfirmationMessages.heading
      }

      s"have the correct panel text of '${CancelCaseConfirmationMessages.panelText(referenceNumber)}'" in {
        elementText(".govuk-panel > div") mustBe CancelCaseConfirmationMessages.panelText(referenceNumber)
      }

      "The what happens next section" should {
        s"have the h2 message of '${CancelCaseConfirmationMessages.whatHappensNext}'" in {
          elementText("#whatHappensNext") mustBe CancelCaseConfirmationMessages.whatHappensNext
        }

        s"have the paragraph of '${CancelCaseConfirmationMessages.whatHappensNextParagraph}'" in {
          elementText(
            "#main-content > div > div > p:nth-child(3)"
          ) mustBe CancelCaseConfirmationMessages.whatHappensNextParagraph
        }
      }

      s"have the '${CancelCaseConfirmationMessages.anotherDisclosure}' link" in {
        elementText("#discloseAnotherUnderpayment") mustBe CancelCaseConfirmationMessages.anotherDisclosure
        elementAttributes("#discloseAnotherUnderpayment").get("href") mustBe
          Some(controllers.serviceEntry.routes.WhatDoYouWantToDoController.onLoad().url)
      }

      s"have the '${CancelCaseConfirmationMessages.helpImproveServiceLink}' link" in {
        elementText("#helpImproveServiceLink") mustBe CancelCaseConfirmationMessages.helpImproveServiceLink
      }
    }
  }
}
