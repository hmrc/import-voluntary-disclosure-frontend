package pages.serviceEntry

import pages.QuestionPage
import play.api.libs.json.JsPath

case object CustomsDeclarationPage extends QuestionPage[Boolean] {

  def path: JsPath = JsPath \ toString

  override def toString: String = "customs-declaration"

}
