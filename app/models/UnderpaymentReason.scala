package models

import play.api.libs.json.Json

case class UnderpaymentReason(
                               boxNumber: Int,
                               itemNumber: Int = 0,
                               original: String,
                               amended: String
                             )

object UnderpaymentReason {
  implicit val format = Json.format[UnderpaymentReason]
}
