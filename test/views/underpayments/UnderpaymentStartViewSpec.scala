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

package views.underpayments

import base.ViewBaseSpec
import messages.BaseMessages
import messages.underpayments.UnderpaymentStartMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.underpayments.UnderpaymentStartView

class UnderpaymentStartViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UnderpaymentStartView = app.injector.instanceOf[UnderpaymentStartView]

  val nameOfImporter               = "ABC ltd"
  val representativeNameOfImporter = "Fast Food ltd"

  "Rendering the Underpayment start page" when {
    "no errors exist for importer" should {
      lazy val view: Html = injectedView(
        controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad(),
        true,
        true,
        false,
        nameOfImporter
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UnderpaymentStartMessages.pageTitle)

      "have the correct details heading" in {
        elementText(
          "#main-content > div > div > details > summary > span"
        ) mustBe UnderpaymentStartMessages.importerDetailsHeader
      }

      "have the correct details paragraph" in {
        elementText(
          "#main-content > div > div > details > div"
        ) mustBe UnderpaymentStartMessages.importerDetailsParagraph
      }
    }

    "no errors exist for representative" should {
      lazy val view: Html = injectedView(
        controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad(),
        true,
        true,
        true,
        representativeNameOfImporter
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UnderpaymentStartMessages.pageTitle)

      "have the correct details heading" in {
        elementText(
          "#main-content > div > div > details > summary > span"
        ) mustBe UnderpaymentStartMessages.representativeImporterDetailsHeader
      }

      "have the correct details paragraph" in {
        elementText(
          "#main-content > div > div > details > div"
        ) mustBe UnderpaymentStartMessages.representativeImporterDetailsParagraph
      }
    }

    "when in change mode back button" should {
      lazy val view: Html = injectedView(
        controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad(),
        true,
        false,
        false,
        nameOfImporter
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not be displayed" in {
        elementExtinct("#back-link")
      }
    }

  }

  "Dynamic bullet points depending on one entry or bulk" when {
    "one entry" should {
      lazy val view: Html = injectedView(
        controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad(),
        true,
        true,
        false,
        nameOfImporter
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page text of '${UnderpaymentStartMessages.oneEntryBullet1}'" in {
        elementText("#main-content li:nth-of-type(1)") mustBe UnderpaymentStartMessages.oneEntryBullet1
      }

      s"have the correct page text of '${UnderpaymentStartMessages.oneEntryBullet2}'" in {
        elementText("#main-content li:nth-of-type(2)") mustBe UnderpaymentStartMessages.oneEntryBullet2
      }
    }

    "bulk entry" should {
      lazy val view: Html = injectedView(
        controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad(),
        false,
        true,
        false,
        nameOfImporter
      )(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct page text of '${UnderpaymentStartMessages.bulkBullet1}'" in {
        elementText("#main-content li:nth-of-type(1)") mustBe UnderpaymentStartMessages.bulkBullet1
      }

      s"have the correct page text of '${UnderpaymentStartMessages.bulkBullet2}'" in {
        elementText("#main-content li:nth-of-type(2)") mustBe UnderpaymentStartMessages.bulkBullet2
      }
    }

  }

  "it" should {
    lazy val view: Html = injectedView(
      controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad(),
      true,
      true,
      false,
      nameOfImporter
    )(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)
    s"have the correct page heading of '${UnderpaymentStartMessages.pageTitle}'" in {
      elementText("h1") mustBe UnderpaymentStartMessages.pageTitle
    }

    s"have the correct page text of '${UnderpaymentStartMessages.p1}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UnderpaymentStartMessages.p1
    }

    s"render the details block" in {
      document.select("#main-content > div > div > details > summary > span").size mustBe 1
    }

    "render a continue button with the correct URL " in {
      elementAttributes(".govuk-button") must contain(
        "href" -> controllers.underpayments.routes.UnderpaymentTypeController.onLoad().url
      )
    }

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain(
        "href" -> controllers.importDetails.routes.EnterCustomsProcedureCodeController.onLoad().url
      )
    }
  }
}
