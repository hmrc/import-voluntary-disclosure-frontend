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
import forms.shared.UploadAnotherFileFormProvider
import messages.{BaseMessages, UploadAnotherFileMessages}
import models.Index
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.ActionItemHelper
import views.html.updateCase.UploadSupportingDocumentationSummaryView

class UploadSupportingDocumentationSummaryViewSpec extends ViewBaseSpec with BaseMessages {

  private lazy val injectedView: UploadSupportingDocumentationSummaryView = app.injector.instanceOf[UploadSupportingDocumentationSummaryView]

  val formProvider: UploadAnotherFileFormProvider = injector.instanceOf[UploadAnotherFileFormProvider]

  val singleFileSummaryList = SummaryList(
    classes = "govuk-!-margin-bottom-9",
    rows = Seq(
      SummaryListRow(
        key = Key(content = Text("fileName"), classes = s"govuk-!-width-one-third govuk-!-font-weight-regular".trim),
        actions = Some(
          Actions(
            items = Seq(
              ActionItemHelper.createDeleteActionItem(
                controllers.updateCase.routes.RemoveSupportingDocumentationController.onLoad(Index(1)).url,
                s"Remove fileName"
              )
            )
          )
        )
      )
    )
  )

  val twoFilesSummaryList = SummaryList(
    classes = "govuk-!-margin-bottom-9",
    rows = Seq(
      SummaryListRow(
        key = Key(content = Text("fileName"), classes = s"govuk-!-width-one-third govuk-!-font-weight-regular".trim),
        actions = Some(
          Actions(
            items = Seq(
              ActionItemHelper.createDeleteActionItem(
                controllers.updateCase.routes.RemoveSupportingDocumentationController.onLoad(Index(1)).url,
                s"Remove fileName"
              )
            )
          )
        )
      ),
      SummaryListRow(
        key = Key(content = Text("fileName2"), classes = s"govuk-!-width-one-third govuk-!-font-weight-regular".trim),
        actions = Some(
          Actions(
            items = Seq(
              ActionItemHelper.createDeleteActionItem(
                controllers.updateCase.routes.RemoveSupportingDocumentationController.onLoad(Index(1)).url,
                s"Remove fileName2"
              )
            )
          )
        )
      )
    )
  )

  "Rendering the UploadSupportingDocumentationSummary page" when {
    "no errors exist when one file is present" should {

      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, singleFileSummaryList)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UploadAnotherFileMessages.title("1", "file"))

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      "remove link is present" in {
        document.select("#main-content > div > div > form > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a > span:nth-child(1)").size mustBe 1
      }

      "first remove contains the correct text" in {
        document.select("#main-content > div > div > form > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a > span.govuk-visually-hidden").text mustBe
          UploadAnotherFileMessages.remove
      }

    }

    "no errors exist when two files are present" should {
      val form: Form[Boolean] = formProvider.apply()
      lazy val view: Html = injectedView(form, twoFilesSummaryList)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UploadAnotherFileMessages.title("2", "files"))

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }

      "remove link is present for fileName" in {
        document.select("#main-content > div > div > form > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a > span:nth-child(1)").size mustBe 1
      }

      "remove link is present for fileName2" in {
        document.select("#main-content > div > div > form > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a > span:nth-child(1)").size mustBe 1
      }

      "first remove contains the correct text" in {
        document.select("#main-content > div > div > form > dl > div:nth-child(1) > dd.govuk-summary-list__actions > a > span.govuk-visually-hidden").text mustBe
          UploadAnotherFileMessages.remove
      }

      "second remove contains the correct text" in {
        document.select("#main-content > div > div > form > dl > div:nth-child(2) > dd.govuk-summary-list__actions > a > span.govuk-visually-hidden").text mustBe
          UploadAnotherFileMessages.remove2
      }
    }

    "an error exists (no option has been selected)" should {
      lazy val form: Form[Boolean] = formProvider().bind(Map("value" -> ""))
      lazy val view: Html = injectedView(form, singleFileSummaryList)(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(UploadAnotherFileMessages.errorPrefix + UploadAnotherFileMessages.title("1", "file"))

      "render an error summary with the correct message" in {
        elementText("div.govuk-error-summary > div") mustBe UploadAnotherFileMessages.requiredError
      }

      "render an error message against the field" in {
        elementText("#value-error") mustBe UploadAnotherFileMessages.errorPrefix + UploadAnotherFileMessages.requiredError
      }

    }
  }

  it should {

    val form: Form[Boolean] = formProvider.apply()
    lazy val view: Html = injectedView(form, singleFileSummaryList)(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct h1 of '${UploadAnotherFileMessages.h1("1", "file")}'" in {
      elementText("h1") mustBe UploadAnotherFileMessages.h1("1", "file")
    }

    s"have the correct value for the first radio button of '${UploadAnotherFileMessages.siteYes}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(1)") mustBe UploadAnotherFileMessages.siteYes
    }

    s"have the correct value for the second radio button of '${UploadAnotherFileMessages.siteNo}'" in {
      elementText("#main-content > div > div > form > div > fieldset > div > div:nth-child(2)") mustBe UploadAnotherFileMessages.siteNo
    }

    s"have the correct Continue button" in {
      elementText(".govuk-button") mustBe continue
    }

  }
}
