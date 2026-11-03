/*
 * Copyright 2026 HM Revenue & Customs
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
import controllers.actions.DataRetrievalAction
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.Application
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.twirl.api.Html
import repositories.{FileUploadRepository, SessionRepository}
import uk.gov.hmrc.http.client.HttpClientV2
import viewmodels.cya.ConfirmationViewData
import views.html.cancelCase.{CancelCaseCheckYourAnswersView, CancelCaseConfirmationView}
import views.html.cya.{ImporterConfirmationView, RepresentativeConfirmationView}
import views.html.updateCase.UpdateCaseConfirmationView

class UserResearchBannerSpec extends ViewBaseSpec {

  private final val bannerUrlEn = "https://banner-en"
  private final val bannerUrlCy = "https://banner-cy"

  private val data = ConfirmationViewData("123-123456Q-01/01/2021", "Test User", "GB123456789", "")

  private def appWith(bannerEnabled: Boolean): Application =
    GuiceApplicationBuilder()
      .configure(
        "features.user-research-banner" -> bannerEnabled,
        "urls.user-research-banner-en"  -> bannerUrlEn,
        "urls.user-research-banner-cy"  -> bannerUrlCy
      )
      .overrides(bind[FileUploadRepository].toInstance(mock[FileUploadRepository]))
      .overrides(bind[SessionRepository].toInstance(mock[SessionRepository]))
      .overrides(bind[DataRetrievalAction].toInstance(mock[DataRetrievalAction]))
      .overrides(bind[HttpClientV2].toInstance(mock[HttpClientV2]))
      .build()

  override lazy val app: Application = appWith(bannerEnabled = true)
  private lazy val bannerOff         = appWith(bannerEnabled = false)

  private def messagesFor(application: Application, lang: String): Messages =
    application.injector.instanceOf[MessagesApi].preferred(Seq(Lang(lang)))

  private def optedInPages(application: Application, messages: Messages): Seq[(String, Html)] = {
    val injector = application.injector
    Seq(
      "ImporterConfirmationView" -> injector
        .instanceOf[ImporterConfirmationView]
        .apply("C18-101", isPayByDeferment = true, isSingleEntry = true, data, Seq.empty)(fakeRequest, messages),
      "RepresentativeConfirmationView" -> injector
        .instanceOf[RepresentativeConfirmationView]
        .apply("C18-101", isPayByDeferment = true, isSingleEntry = true, data, Seq.empty)(fakeRequest, messages),
      "UpdateCaseConfirmationView" -> injector
        .instanceOf[UpdateCaseConfirmationView]
        .apply("C18-101")(fakeRequest, messages),
      "CancelCaseConfirmationView" -> injector
        .instanceOf[CancelCaseConfirmationView]
        .apply("C18-101")(fakeRequest, messages)
    )
  }

  private def banners(view: Html): org.jsoup.select.Elements = parse(view).select(".hmrc-user-research-banner")

  private def parse(view: Html): Document = Jsoup.parse(view.body)

  "The user research banner" should {

    "be shown once, directly in the header, with the English link on every confirmation page" in {
      optedInPages(app, messagesFor(app, "en")).foreach { case (name, view) =>
        withClue(s"$name: ") {
          val banner = banners(view)
          banner.size mustBe 1
          banner.first.parent.tagName mustBe "header"
          banner.select("a.hmrc-user-research-banner__link").attr("href") mustBe bannerUrlEn
          banner.select("a.hmrc-user-research-banner__link").text mustBe "Join our research panel (opens in new tab)"
        }
      }
    }

    "use the Welsh link and copy on every confirmation page when the language is Welsh" in {
      optedInPages(app, messagesFor(app, "cy")).foreach { case (name, view) =>
        withClue(s"$name: ") {
          val link = banners(view).select("a.hmrc-user-research-banner__link")
          link.attr("href") mustBe bannerUrlCy
          link.text mustBe "Ymunwch â’n panel ymchwil (yn agor tab newydd)"
        }
      }
    }

    "not be shown on any confirmation page when the feature is switched off" in {
      optedInPages(bannerOff, messagesFor(bannerOff, "en")).foreach { case (name, view) =>
        withClue(s"$name: ") {
          banners(view).size mustBe 0
        }
      }
    }

    "not be shown on a page that does not opt in" in {
      val view = app.injector
        .instanceOf[CancelCaseCheckYourAnswersView]
        .apply(Seq.empty)(fakeRequest, messagesFor(app, "en"))

      banners(view).size mustBe 0
    }
  }
}
