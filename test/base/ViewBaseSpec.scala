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

package base

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.Assertion

import scala.jdk.CollectionConverters._

trait ViewBaseSpec extends SpecBase {

  final val emptyString              = ""
  final val govErrorSummaryListClass = ".govuk-error-summary__list"
  final val titleSuffix              = " - Disclose an underpayment of Customs Duty or import VAT - GOV.UK"

  def elementText(selector: String)(implicit document: Document): String =
    element(selector).text()

  def elementAttributes(cssSelector: String)(implicit document: Document): Map[String, String] = {
    val attributes = element(cssSelector).attributes.asList().asScala.toList
    attributes.map(attribute => (attribute.getKey, attribute.getValue)).toMap
  }

  def elementExtinct(cssSelector: String)(implicit document: Document): Assertion = {
    val elements = document.select(cssSelector)

    if (elements.size == 0) {
      succeed
    } else {
      fail(s"Element with selector '$cssSelector' was found!")
    }
  }

  def element(cssSelector: String)(implicit document: Document): Element = {
    val elements = document.select(cssSelector)

    if (elements.size == 0) {
      fail(s"No element exists with the selector '$cssSelector'")
    }

    elements.first()
  }

  def formatHtml(markup: String): String = Jsoup.parseBodyFragment(s"\n$markup\n").toString.trim

  def paragraph(index: Int)(implicit document: Document): String = elementText(s"article > p:nth-of-type($index)")

  def bullet(index: Int)(implicit document: Document): String = elementText(s"article li:nth-of-type($index)")

  def checkPageTitle(title: String)(implicit document: Document): Unit =
    "have the correct page title" in {
      document.title mustBe title + titleSuffix
    }

}
