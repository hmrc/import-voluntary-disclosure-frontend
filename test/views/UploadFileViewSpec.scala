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
import messages.UploadFileMessages
import mocks.config.MockAppConfig
import models.{FileUploadInfo, OptionalDocument}
import models.OptionalDocument._
import models.upscan.{FileUpload, Reference, UpScanInitiateResponse, UploadFormTemplate}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.data.Forms.text
import play.api.mvc.Call
import play.twirl.api.Html
import views.html.UploadFileView

class UploadFileViewSpec extends ViewBaseSpec {

  private lazy val injectedView: UploadFileView = app.injector.instanceOf[UploadFileView]
  private lazy val initiateResponse: UpScanInitiateResponse =
    UpScanInitiateResponse(Reference("Upscan Ref"), UploadFormTemplate("url", Map.empty))
  private val backLink: Call = Call("GET", "url")
  private val maxOptDocs: Seq[OptionalDocument] = Seq(ImportAndEntry, AirwayBill, OriginProof, Other)

  val form: Form[_] = Form("fileName" -> text)


  "Rendering the UploadFile page" when {
    "Optional Documents have been selected" should {
      lazy val view: Html = injectedView(form, initiateResponse, Some(backLink), maxOptDocs)(fakeRequest, MockAppConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct form action" in {
        elementAttributes("form").get("action").get mustBe initiateResponse.uploadFormTemplate.href
      }

      "have the correct file upload control" in {
        element(".govuk-file-upload").attr("id") mustBe UploadFileMessages.fileUploadId
      }

      "have the correct file upload control file types" in {
        element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.upScanAcceptedFileTypes
      }

      "have heading for optional docs" in {
        elementText("#main-content p:nth-of-type(2)") mustBe UploadFileMessages.mayInclude
      }

      "have the correct text for optional file bullet 1" in {
        elementText("#main-content ul:nth-of-type(2) li:nth-of-type(1)") mustBe UploadFileMessages.mayIncludeFile1
      }

      "have the correct text for optional file bullet 2" in {
        elementText("#main-content ul:nth-of-type(2) li:nth-of-type(2)") mustBe UploadFileMessages.mayIncludeFile2
      }

      "have the correct text for optional file bullet 3" in {
        elementText("#main-content ul:nth-of-type(2) li:nth-of-type(3)") mustBe UploadFileMessages.mayIncludeFile3
      }

      "have the correct text for optional file bullet 4" in {
        elementText("#main-content ul:nth-of-type(2) li:nth-of-type(4)") mustBe UploadFileMessages.mayIncludeFile4
      }

      "an error exists (no file has been uploaded)" should {
        lazy val form: Form[_] = form.bind(Map("file" -> "unknown"))
        lazy val view: Html = injectedView(form, initiateResponse, Some(backLink), maxOptDocs)(fakeRequest, MockAppConfig, messages)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        checkPageTitle(UploadFileMessages.errorPrefix + UploadFileMessages.title)

        "render an error summary with the correct message" in {
          elementText("div.govuk-error-summary > div") mustBe "valueUnknown"
        }

        "render an error message against the field" in {
          elementText("#value-error") mustBe UploadFileMessages.errorPrefix + "valueUnknown"
        }

      }
    }

    "Optional Documents have not been selected" should {
      lazy val view: Html = injectedView(form, initiateResponse, Some(backLink), Seq.empty)(fakeRequest, MockAppConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct form action" in {
        elementAttributes("form").get("action").get mustBe initiateResponse.uploadFormTemplate.href
      }

      "have the correct file upload control" in {
        element(".govuk-file-upload").attr("id") mustBe UploadFileMessages.fileUploadId
      }

      "have the correct file upload control file types" in {
        element(".govuk-file-upload").attr("accept") mustBe MockAppConfig.upScanAcceptedFileTypes
      }

      "have heading for optional docs" in {
        elementExtinct("#main-content p:nth-of-type(2)")
      }
    }

    "In check mode and all supporting documents have been removed" should {
      lazy val view: Html = injectedView(form, initiateResponse, None, Seq.empty)(fakeRequest, MockAppConfig, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "no back button displayed" in {
        elementExtinct("#back-link")
      }
    }

  }

  it should {
    lazy val view: Html = injectedView(form, initiateResponse, Some(backLink), maxOptDocs)(fakeRequest, MockAppConfig, messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    checkPageTitle(UploadFileMessages.title)

    "render a back link with the correct URL" in {
      elementAttributes("#back-link") must contain("href" -> "url")
    }

    s"have the correct h1 of '${UploadFileMessages.h1}'" in {
      elementText("h1") mustBe UploadFileMessages.h1
    }

    s"have the correct text of '${UploadFileMessages.mustInclude}'" in {
      elementText("#main-content p:nth-of-type(1)") mustBe UploadFileMessages.mustInclude
    }

    "have the correct text for mandatory file bullet 1" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(1)") mustBe UploadFileMessages.mustIncludeFile1
    }

    "have the correct text for mandatory file bullet 2" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(2)") mustBe UploadFileMessages.mustIncludeFile2
    }

    "have the correct text for mandatory file bullet 3" in {
      elementText("#main-content ul:nth-of-type(1) li:nth-of-type(3)") mustBe UploadFileMessages.mustIncludeFile3
    }

    s"have the correct fileRequirementsHeader of '${UploadFileMessages.fileRequirementsHeader}'" in {
      elementText("h2") mustBe UploadFileMessages.fileRequirementsHeader
    }

    "have the correct text for file requirements bullet 1" in {
      elementText("#main-content ul:nth-of-type(3) li:nth-of-type(1)") mustBe UploadFileMessages.fileFormats
    }

    "have the correct text for file requirements bullet 2" in {
      elementText("#main-content ul:nth-of-type(3) li:nth-of-type(2)") mustBe UploadFileMessages.fileSize
    }

    "have the correct text for file requirements bullet 3" in {
      elementText("#main-content ul:nth-of-type(3) li:nth-of-type(3)") mustBe UploadFileMessages.oneFileAtTime
    }

    "have the correct Continue button" in {
      elementText(".govuk-button") mustBe UploadFileMessages.uploadFile
    }

  }
}
