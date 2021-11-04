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

package views.cya

import base.ViewBaseSpec
import messages.cya.ImporterConfirmationMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import viewmodels.cya.ConfirmationViewData
import views.html.cya.ImporterConfirmationView

class ImporterConfirmationViewSpec extends ViewBaseSpec {

  private lazy val injectedView: ImporterConfirmationView = app.injector.instanceOf[ImporterConfirmationView]
  private val referenceNumber: String                     = "C18-101"
  val data: ConfirmationViewData = ConfirmationViewData("123-123456Q-01/01/2021", "Test User", "GB123456789", "")

  "Rendering the Confirmation page" when {

    "no errors exist" should {
      lazy val view: Html =
        injectedView(referenceNumber, isPayByDeferment = true, isSingleEntry = true, data, Seq.empty)(
          fakeRequest,
          messages
        )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(ImporterConfirmationMessages.pageTitle)

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
      s"have the correct page heading of '${ImporterConfirmationMessages.heading}'" in {
        elementText("h1") mustBe ImporterConfirmationMessages.heading
      }

      s"have the correct reference number message of '${ImporterConfirmationMessages.referenceNumber}'" in {
        elementText("#entryNumber") mustBe ImporterConfirmationMessages.referenceNumber
      }

      "The what happens next section - Deferment selected" should {

        s"have the h2 message of '${ImporterConfirmationMessages.whatHappensNext}'" in {
          elementText("#whatHappensNext") mustBe ImporterConfirmationMessages.whatHappensNext
        }

        s"have the p2 message of '${ImporterConfirmationMessages.p2Deferment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(3)"
          ) mustBe ImporterConfirmationMessages.p2Deferment
        }

        s"have the p3 message of for bulk entry '${ImporterConfirmationMessages.p3Deferment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(4)"
          ) mustBe ImporterConfirmationMessages.p3Deferment
        }
      }

      "The what happens next section - Other payment selected" should {
        lazy val view: Html =
          injectedView(referenceNumber, isPayByDeferment = false, isSingleEntry = true, data, Seq.empty)(
            fakeRequest,
            messages
          )
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"have the p2 message of '${ImporterConfirmationMessages.p2OtherPayment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(3)"
          ) mustBe ImporterConfirmationMessages.p2OtherPayment
        }

        s"have the p3 message of for bulk entry '${ImporterConfirmationMessages.p3OtherPayment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(4)"
          ) mustBe ImporterConfirmationMessages.p3OtherPayment
        }

        s"have the p4 message of for bulk entry '${ImporterConfirmationMessages.p4OtherPayment}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(5)"
          ) mustBe ImporterConfirmationMessages.p4OtherPayment
        }
      }

      "The what you should do next section" should {

        s"have the h2 message of '${ImporterConfirmationMessages.whatYouShouldDoNext}'" in {
          elementText("#whatYouShouldDoNext") mustBe ImporterConfirmationMessages.whatYouShouldDoNext
        }

        s"have the correct Print and Save message of '${ImporterConfirmationMessages.printSave}'" in {
          elementText("#printSave") mustBe ImporterConfirmationMessages.printSave
        }

        s"have the correct Print and Save Rest of Message '${ImporterConfirmationMessages.printSaveRestOfMessage}'" in {
          elementText("#printSaveRestOfMessage") mustBe ImporterConfirmationMessages.printSaveRestOfMessage
        }

        s"have the paragraph of '${ImporterConfirmationMessages.contactInfo}'" in {
          elementText(
            "#main-content > div > div > div > p:nth-child(8)"
          ) mustBe ImporterConfirmationMessages.contactInfo
        }

        s"have the correct email link" in {
          elementAttributes("#main-content > div > div > div > p:nth-child(8) > a")
            .get("href") mustBe Some(s"mailto:${appConfig.c18EmailAddress}")
        }

        s"have the noscript paragraph" in {
          elementText("#main-content > div > div > div > noscript") mustBe
            ImporterConfirmationMessages.printSave + " " + ImporterConfirmationMessages.printSaveRestOfMessage
        }

        s"have a link message of '${ImporterConfirmationMessages.discloseAnotherUnderpayment}'" in {
          elementText("#discloseAnotherUnderpayment") mustBe ImporterConfirmationMessages.discloseAnotherUnderpayment
        }
      }

      s"have the '${ImporterConfirmationMessages.helpImproveServiceLink}' sub-heading" in {
        elementText("#helpImproveServiceLink") mustBe ImporterConfirmationMessages.helpImproveServiceLink
      }

    }

  }

}
