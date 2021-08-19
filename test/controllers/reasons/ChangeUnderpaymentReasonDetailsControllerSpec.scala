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

package controllers.reasons

import base.ControllerSpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.reasons.UnderpaymentReasonAmendmentFormProvider
import mocks.repositories.MockSessionRepository
import models.UserAnswers
import models.reasons.BoxNumber.BoxNumber
import models.reasons.{BoxNumber, ChangeUnderpaymentReason, UnderpaymentReason}
import pages.reasons.ChangeUnderpaymentReasonPage
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, defaultAwaitTimeout, redirectLocation, status}
import views.html.reasons._

import scala.concurrent.Future

class ChangeUnderpaymentReasonDetailsControllerSpec extends ControllerSpecBase {

  private final lazy val fifty: String = "GBP50"
  private final lazy val sixtyFive: String = "GBP65.01"

  private def fakeRequestGenerator(original: String, amended: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequest.withFormUrlEncodedBody(
      "original" -> original,
      "amended" -> amended
    )

  trait Test extends MockSessionRepository {
    def underpayment(boxNumber: BoxNumber, itemNumber: Int = 0, original: String = "60", amended: String = "70"): UnderpaymentReason = {
      UnderpaymentReason(boxNumber, itemNumber, original, amended)
    }

    lazy val controller = new ChangeUnderpaymentReasonDetailsController(
      authenticatedAction,
      dataRetrievalAction,
      dataRequiredAction,
      mockSessionRepository,
      messagesControllerComponents,
      form,
      textAmendmentView,
      weightAmendmentView,
      currencyAmendmentView,
      otherReasonAmendmentView,
      ec
    )
    lazy val textAmendmentView: TextAmendmentView = app.injector.instanceOf[TextAmendmentView]
    lazy val weightAmendmentView: WeightAmendmentView = app.injector.instanceOf[WeightAmendmentView]
    lazy val currencyAmendmentView: CurrencyAmendmentView = app.injector.instanceOf[CurrencyAmendmentView]
    lazy val otherReasonAmendmentView: OtherReasonAmendmentView = app.injector.instanceOf[OtherReasonAmendmentView]
    private lazy val dataRetrievalAction = new FakeDataRetrievalAction(userAnswers)
    val userAnswers: Option[UserAnswers] = Some(
      UserAnswers("some-cred-id")
        .set(ChangeUnderpaymentReasonPage, ChangeUnderpaymentReason(
          underpayment(boxNumber = BoxNumber.Box35, itemNumber = 1),
          underpayment(boxNumber = BoxNumber.Box35, itemNumber = 1))).success.value
    )
    val formProvider: UnderpaymentReasonAmendmentFormProvider = injector.instanceOf[UnderpaymentReasonAmendmentFormProvider]
    MockedSessionRepository.set(Future.successful(true))
    val form: UnderpaymentReasonAmendmentFormProvider = formProvider
  }

  "GET onLoad" should {
    "return OK" in new Test {
      val result: Future[Result] = controller.onLoad(22)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return HTML for valid box number" in new Test {
      val result: Future[Result] = controller.onLoad(22)(fakeRequest)
      contentType(result) mustBe Some("text/html")
      charset(result) mustBe Some("utf-8")
    }

    "throw an exception for invalid box number" in new Test {
      val result: RuntimeException = intercept[RuntimeException](await(controller.onLoad(0)(fakeRequest)))
      assert(result.getMessage.contains("Invalid Box Number"))
    }

    "redirect the back button to Change underpayment reasons" in new Test {
      controller.backLink(BoxNumber.Box22) mustBe Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad())
    }

    "redirect the back button to previous page in browsers history" in new Test {
      controller.backLink(BoxNumber.Box33) mustBe None
    }
  }

  "POST onSubmit" when {

    "payload contains valid data" should {
      "return a SEE OTHER response when correct data is sent" in new Test {
        lazy val result: Future[Result] = controller.onSubmit(22)(fakeRequestGenerator(fifty, sixtyFive))
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.reasons.routes.ConfirmChangeReasonDetailController.onLoad().url)
      }

      "update the UserAnswers in session" in new Test {
        await(controller.onSubmit(22)(fakeRequestGenerator(fifty, sixtyFive)))
        verifyCalls()
      }
    }

    "payload contains invalid data" should {
      "return Ok form with errors when invalid data is sent" in new Test {
        val result: Future[Result] = controller.onSubmit(62)(fakeRequest)
        status(result) mustBe Status.BAD_REQUEST
      }

      "return Bad Request for form with errors when only original value is sent" in new Test {
        val result: Future[Result] = controller.onSubmit(22)(
          fakeRequest.withFormUrlEncodedBody(
            "original" -> fifty
          )
        )
        status(result) mustBe Status.BAD_REQUEST
      }

      "return Bad Request for form with errors when only key is empty" in new Test {
        val result: Future[Result] = controller.onSubmit(22)(
          fakeRequest.withFormUrlEncodedBody(
            "original" -> fifty, "amended" -> fifty
          )
        )
        status(result) mustBe Status.BAD_REQUEST
      }

      "return RuntimeException for invalid box number" in new Test {
        val result: RuntimeException = intercept[RuntimeException](await(controller.onSubmit(0)(fakeRequest)))
        assert(result.getMessage.contains("Invalid Box Number"))
      }
    }

  }

  "routeToView for Text Amendment" when {
    def checkRoute(boxNumber: BoxNumber, itemNumber: Int, back: Option[Call], expectedInputClass: Option[String] = Some("govuk-input--width-10")) = {
      s"render the view using the textAmendmentView for box ${boxNumber}" in new Test {
        val formAction = controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onSubmit(boxNumber.id)
        val result = controller.routeToView(boxNumber, itemNumber, form.apply(boxNumber))(fakeRequest)
        result mustBe textAmendmentView(form.apply(boxNumber), formAction, boxNumber, itemNumber, back, inputClass = expectedInputClass)(fakeRequest, messages)
      }
    }

    "called with entry level box 22" should {
      checkRoute(BoxNumber.Box22, 0, Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad()))
    }
    "called with entry level box 62" should {
      checkRoute(BoxNumber.Box62, 0, Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad()))
    }
    "called with entry level box 63" should {
      checkRoute(BoxNumber.Box63, 0, Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad()))
    }
    "called with entry level box 66" should {
      checkRoute(BoxNumber.Box66, 0, Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad()))
    }
    "called with entry level box 67" should {
      checkRoute(BoxNumber.Box67, 0, Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad()))
    }
    "called with entry level box 68" should {
      checkRoute(BoxNumber.Box68, 0, Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad()))
    }
    //
    "called with item level box 33" should {
      checkRoute(BoxNumber.Box33, 1, None, Some("govuk-input--width-20"))
    }
    "called with item level box 34" should {
      checkRoute(BoxNumber.Box34, 1, None, Some("govuk-input--width-3"))
    }
    "called with item level box 36" should {
      checkRoute(BoxNumber.Box36, 1, None, Some("govuk-input--width-3"))
    }
    "called with item level box 37" should {
      checkRoute(BoxNumber.Box37, 1, None)
    }
    "called with item level box 39" should {
      checkRoute(BoxNumber.Box39, 1, None)
    }
    "called with item level box 41" should {
      checkRoute(BoxNumber.Box41, 1, None)
    }
    "called with item level box 45" should {
      checkRoute(BoxNumber.Box45, 1, None, Some("govuk-input--width-4"))
    }
  }

  "routeToView for Weight Amendment" when {
    def checkRoute(boxNumber: BoxNumber, itemNumber: Int, back: Option[Call], expectedInputClass: Option[String] = Some("govuk-input--width-10")) = {
      s"render the view using the weightAmendmentView for box ${boxNumber}" in new Test {
        val formAction = controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onSubmit(boxNumber.id)
        val result = controller.routeToView(boxNumber, itemNumber, form.apply(boxNumber))(fakeRequest)
        result mustBe weightAmendmentView(form.apply(boxNumber), formAction, boxNumber, itemNumber, back, inputClass = expectedInputClass)(fakeRequest, messages)
      }
    }

    "called with item level box 35" should {
      checkRoute(BoxNumber.Box35, 1, None)
    }
    "called with item level box 38" should {
      checkRoute(BoxNumber.Box38, 1, None)
    }
  }

  "routeToView for Currency Amendment" when {
    def checkRoute(boxNumber: BoxNumber, itemNumber: Int, back: Option[Call], expectedInputClass: Option[String] = Some("govuk-input--width-10")) = {
      s"render the view using the currencyAmendmentView for box ${boxNumber}" in new Test {
        val formAction = controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onSubmit(boxNumber.id)
        val result = controller.routeToView(boxNumber, itemNumber, form.apply(boxNumber))(fakeRequest)
        result mustBe currencyAmendmentView(form.apply(boxNumber), formAction, boxNumber, itemNumber, back, inputClass = expectedInputClass)(fakeRequest, messages)
      }
    }

    "called with item level box 46" should {
      checkRoute(BoxNumber.Box46, 1, None)
    }
  }

  "routeToView for other reason Amendment" when {
    def checkRoute(boxNumber: BoxNumber, itemNumber: Int, back: Option[Call], expectedInputClass: Option[String] = Some("govuk-input--width-10")) = {
      s"render the view using the otherReasonAmendmentView for box ${boxNumber}" in new Test {
        val formAction = controllers.reasons.routes.ChangeUnderpaymentReasonDetailsController.onSubmit(boxNumber.id)
        val result = controller.routeToView(boxNumber, itemNumber, form.apply(boxNumber))(fakeRequest)
        result mustBe otherReasonAmendmentView(form.apply(boxNumber), formAction, boxNumber, itemNumber, back, inputClass = expectedInputClass)(fakeRequest, messages)
      }
    }

    "called with entry level box 99" should {
      checkRoute(BoxNumber.OtherItem, 0, Some(controllers.reasons.routes.ChangeUnderpaymentReasonController.onLoad()))
    }
  }

}
