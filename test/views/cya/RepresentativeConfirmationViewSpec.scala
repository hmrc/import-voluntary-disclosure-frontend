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
import messages.cya.RepresentativeConfirmationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import viewmodels.cya.ConfirmationViewData
import views.html.cya.RepresentativeConfirmationView

class RepresentativeConfirmationViewSpec extends ViewBaseSpec {

  private lazy val injectedView: RepresentativeConfirmationView =
    app.injector.instanceOf[RepresentativeConfirmationView]
  private val referenceNumber: String = "C18-101"

  val data: ConfirmationViewData =
    ConfirmationViewData("123-123456Q-01/01/2021", "Test User", "GB123456789", "GB123456789")
  val dataNoEori: ConfirmationViewData = ConfirmationViewData("123-123456Q-01/01/2021", "Test User", "GB123456789", "")

  "Rendering the Confirmation page" when {

    "no errors exist" should {
      lazy val view: Html =
        injectedView(referenceNumber, isPayByDeferment = true, isSingleEntry = true, data, Seq.empty)(
          fakeRequest,
          messages
        )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(RepresentativeConfirmationMessages.pageTitle)

      s"have the correct Entry number value" in {
        elementText(".govuk-panel__body > strong") mustBe referenceNumber
      }
    }

    "it" should {

      lazy val view: Html =
        injectedView(referenceNumber, isPayByDeferment = true, isSingleEntry = true, data, Seq.empty)(
          fakeRequest,
          messages
        )
      lazy implicit val document: Document = Jsoup.parse(view.body)
      s"have the correct page heading of '${RepresentativeConfirmationMessages.heading}'" in {
        elementText("h1") mustBe RepresentativeConfirmationMessages.heading
      }

      s"have the correct reference number message of '${RepresentativeConfirmationMessages.referenceNumber}'" in {
        elementText("#entryNumber") mustBe RepresentativeConfirmationMessages.referenceNumber
      }

      "The what happens next section - Deferment selected" should {

        s"have the h2 message of '${RepresentativeConfirmationMessages.whatHappensNext}'" in {
          elementText("#whatHappensNext") mustBe RepresentativeConfirmationMessages.whatHappensNext
        }

        s"have the p2 message of '${RepresentativeConfirmationMessages.p2Deferment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(3)"
          ) mustBe RepresentativeConfirmationMessages.p2Deferment
        }

        s"have the p3 message of for bulk entry '${RepresentativeConfirmationMessages.p3Deferment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(4)"
          ) mustBe RepresentativeConfirmationMessages.p3Deferment
        }
      }

      "The what happens next section - Other payment selected" should {
        lazy val view: Html =
          injectedView(referenceNumber, isPayByDeferment = false, isSingleEntry = true, data, Seq.empty)(
            fakeRequest,
            messages
          )
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the p2 message of '${RepresentativeConfirmationMessages.p2OtherPayment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(3)"
          ) mustBe RepresentativeConfirmationMessages.p2OtherPayment
        }

        s"have the p3 message of for bulk entry '${RepresentativeConfirmationMessages.p3OtherPayment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(4)"
          ) mustBe RepresentativeConfirmationMessages.p3OtherPayment
        }

        s"have the p4 message of for bulk entry '${RepresentativeConfirmationMessages.p4OtherPayment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(5)"
          ) mustBe RepresentativeConfirmationMessages.p4OtherPayment
        }
      }

      "The what you should do next section" should {

        s"have the h2 message of '${RepresentativeConfirmationMessages.whatYouShouldDoNext}'" in {
          elementText("#whatYouShouldDoNext") mustBe RepresentativeConfirmationMessages.whatYouShouldDoNext
        }

        s"have the correct Print and Save message of '${RepresentativeConfirmationMessages.printSave}'" in {
          elementText("#printSave") mustBe RepresentativeConfirmationMessages.printSave
        }

        s"have the correct Print and Save Rest of Message '${RepresentativeConfirmationMessages.printSaveRestOfMessage}'" in {
          elementText("#printSaveRestOfMessage") mustBe RepresentativeConfirmationMessages.printSaveRestOfMessage
        }

        s"have the paragraph message of '${RepresentativeConfirmationMessages.contactInfo}'" in {
          elementText(
            "#main-content > div > div > div:nth-child(1) > p:nth-child(9)"
          ) mustBe RepresentativeConfirmationMessages.contactInfo
        }

        s"have the correct email link" in {
          elementAttributes("#main-content > div > div > div:nth-child(1) > p:nth-child(9) > a")
            .get("href") mustBe Some(s"mailto:${appConfig.c18EmailAddress}")
        }

        s"have the correct noscript paragraph" in {
          elementText("#main-content > div > div > div > noscript") mustBe
            RepresentativeConfirmationMessages.printSave + " " + RepresentativeConfirmationMessages.printSaveRestOfMessage
        }

        s"have a button message of '${RepresentativeConfirmationMessages.discloseAnotherUnderpayment}'" in {
          elementText(
            "#discloseAnotherUnderpayment"
          ) mustBe RepresentativeConfirmationMessages.discloseAnotherUnderpayment
        }
      }

      s"have the exit survey content" in {
        document.getElementsByClass("govuk-heading-m").text() must include(
          RepresentativeConfirmationMessages.exitSurveyHeading
        )
        document.getElementsByClass("govuk-body").text() must include(
          RepresentativeConfirmationMessages.exitSurveyParagraph
        )
        document.getElementsByClass("govuk-body").text() must include(RepresentativeConfirmationMessages.exitSurveyLink)
        document.getElementsByClass("govuk-body").text() must include(
          RepresentativeConfirmationMessages.exitSurveyParagraph2
        )
      }

    }

  }

}
