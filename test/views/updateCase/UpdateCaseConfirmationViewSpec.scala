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

package views.updateCase

import base.ViewBaseSpec
import messages.UpdateCaseConfirmationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.updateCase.UpdateCaseConfirmationView

class UpdateCaseConfirmationViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UpdateCaseConfirmationView = app.injector.instanceOf[UpdateCaseConfirmationView]
  private val referenceNumber: String = "C181234567890123456789"

  "Rendering the Update Case Confirmation page" when {

    "no errors exist" should {
      lazy val view: Html = injectedView(referenceNumber)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UpdateCaseConfirmationMessages.pageTitle)
    }

    "it" should {
      lazy val view: Html = injectedView(referenceNumber)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)
      s"have the correct page heading of '${UpdateCaseConfirmationMessages.heading}'" in {
        elementText("h1") mustBe UpdateCaseConfirmationMessages.heading
      }

      s"have the correct paragraph of '${UpdateCaseConfirmationMessages.paragraph(referenceNumber)}'" in {
        elementText("#main-content > div > div > p:nth-child(2)") mustBe UpdateCaseConfirmationMessages.paragraph(referenceNumber)
      }

      "The what happens next section" should {

        s"have the h2 message of '${UpdateCaseConfirmationMessages.whatHappensNext}'" in {
          elementText("#whatHappensNext") mustBe UpdateCaseConfirmationMessages.whatHappensNext
        }

        s"have the paragraph of '${UpdateCaseConfirmationMessages.whatHappensNextParagraph}'" in {
          elementText("#main-content > div > div > p:nth-child(4)") mustBe UpdateCaseConfirmationMessages.whatHappensNextParagraph
        }
      }

      "The what you should do next section" should {

        s"have the h2 message of '${UpdateCaseConfirmationMessages.whatYouShouldDoNext}'" in {
          elementText("#whatYouShouldDoNext") mustBe UpdateCaseConfirmationMessages.whatYouShouldDoNext
        }

        s"have the paragraph of '${UpdateCaseConfirmationMessages.whatYouShouldDoNextParagraph}'" in {
          elementText("#main-content > div > div > p:nth-child(6)") mustBe UpdateCaseConfirmationMessages.whatYouShouldDoNextParagraph
        }

        s"have the correct email link" in {
          elementAttributes("#main-content > div > div > p:nth-child(6) > a")
            .get("href") mustBe Some("mailto:customsaccountingrepayments@hmrc.gov.uk")
        }
      }

      s"have the '${UpdateCaseConfirmationMessages.helpImproveServiceLink}' sub-heading" in {
        elementText("#helpImproveServiceLink") mustBe UpdateCaseConfirmationMessages.helpImproveServiceLink
      }
    }
  }
}
