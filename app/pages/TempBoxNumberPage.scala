package pages

import models.UnderpaymentReason
import play.api.libs.json.JsPath

case object TempBoxNumberPage extends QuestionPage[UnderpaymentReason] {

  def path: JsPath = JsPath \ toString

  override def toString: String = "temp-box-number"

}

