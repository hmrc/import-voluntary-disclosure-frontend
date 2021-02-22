package pages

import play.api.libs.json.JsPath

case object RepresentativeNamePage extends QuestionPage[String] {

  def path: JsPath = JsPath \ toString

  override def toString: String = "representative-name"

}