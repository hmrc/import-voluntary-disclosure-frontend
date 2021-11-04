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

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.ViewUtils.hint

sealed trait SubmissionType

object SubmissionType extends Enumerable.Implicits[SubmissionType] {

  case object CreateCase extends WithName("createCase") with SubmissionType

  case object UpdateCase extends WithName("updateCase") with SubmissionType

  case object CancelCase extends WithName("cancelCase") with SubmissionType

  val values: Seq[SubmissionType] = Seq(
    CreateCase,
    UpdateCase,
    CancelCase
  )

  def options(form: Form[_], cancelCase: Boolean, updateCase: Boolean)(implicit messages: Messages): Seq[RadioItem] = {
    val values = (cancelCase, updateCase) match {
      case (true, true)  => Seq(CreateCase, UpdateCase, CancelCase)
      case (true, false) => Seq(CreateCase, CancelCase)
      case (false, true) => Seq(CreateCase, UpdateCase)
      case _             => Seq.empty
    }
    values.map { value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(messages(s"whatDoYouWantToDo.${value.toString}")),
        hint = if (value == CreateCase) {
          Some(hint(messages("whatDoYouWantToDo.createCaseHint")))
        } else None,
        checked = form("value").value.contains(value.toString)
      )
    }
  }

  implicit val enumerable: Enumerable[SubmissionType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
