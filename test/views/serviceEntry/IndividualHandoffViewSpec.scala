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

package views.serviceEntry

import base.ViewBaseSpec
import messages.serviceEntry.IndividualHandoffMessages._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.serviceEntry.IndividualHandoffView

class IndividualHandoffViewSpec extends ViewBaseSpec {

  private lazy val injectedView: IndividualHandoffView = app.injector.instanceOf[IndividualHandoffView]

  "Rendering the Deferment page" when {

    "no errors exist VAT only" should {
      lazy val view: Html = injectedView()(fakeRequest, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      checkPageTitle(title)

      "not render an error summary" in {
        document.select("div.govuk-error-summary").size mustBe 0
      }

      "not render an error message against the field" in {
        document.select("#value-error").size mustBe 0
      }
    }

  }

  it should {

    lazy val view: Html = injectedView()(fakeRequest, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct page heading" in {
      elementText("h1") mustBe heading
    }

    s"have the correct value for '${p1}'" in {
      elementText("#main-content > div > div > p:nth-child(2)") mustBe p1
    }

    s"have the correct value for p2" in {
      elementText("#main-content > div > div > p:nth-child(3)") mustBe
        p2Part1 + " " + "customsaccountingrepayments@hmrc.gov.uk" + " " + p2Part2
    }
  }

}
