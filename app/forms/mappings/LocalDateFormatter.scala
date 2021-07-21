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

package forms.mappings

import play.api.data.FormError
import play.api.data.format.Formatter

import java.time.LocalDate
import scala.util.{Failure, Success, Try}


private[mappings] class LocalDateFormatter(invalidKey: String,
                                           allRequiredKey: String,
                                           twoRequiredKey: String,
                                           requiredKey: String,
                                           dayMonthLengthKey: String,
                                           yearLengthKey: String,
                                           validatePastKey: Option[String],
                                           validateAfterKey: Option[String],
                                           args: Seq[String] = Seq.empty)
  extends Formatter[LocalDate] with Formatters with Constraints {

  private val fieldKeys: List[String] = List("day", "month", "year")

  private val yearLength = 4
  private val dayMonthLengthMax = 2

  private def toDate(key: String, day: Int, month: Int, year: Int): Either[Seq[FormError], LocalDate] =
    Try(LocalDate.of(year, month, day)) match {
      case Success(date) =>
        Right(date)
      case Failure(_) =>
        Left(Seq(FormError(s"$key.day", invalidKey, fieldKeys ++ args)))
    }

  private def formatDate(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {

    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = invalidKey,
      args
    )

    val date = for {
      day <- int.bind(s"$key.day", data).right
      month <- int.bind(s"$key.month", data).right
      year <- int.bind(s"$key.year", data).right
      date <- toDate(key, day, month, year).right
    } yield date

    if (validatePastKey.isDefined) {
      date.fold(
        err => Left(err),
        dt =>
          if (dt.isAfter(LocalDate.now)) {
            Left(List(FormError(s"$key.day", validatePastKey.get, fieldKeys)))
          } else if (LocalDate.of(1900, 1, 2).isAfter(dt)) {
            Left(List(FormError(s"$key.day", validateAfterKey.get, fieldKeys)))
          } else {
            Right(dt)
          }
      )
    } else {
      date
    }
  }

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
    val sanitizedFields = data.mapValues(filter).filter(_._2.nonEmpty)
    val missingKeys = fieldKeys.toSet -- sanitizedFields.keys.map(_.stripPrefix(s"$key."))

    val day = sanitizedFields.get(s"$key.day")
    val month = sanitizedFields.get(s"$key.month")
    val year = sanitizedFields.get(s"$key.year")

    List(day, month, year).count(_.isDefined) match {
      case 3 =>
        val validDay = day.filter(_.length <= dayMonthLengthMax).toRight(FormError(s"$key.day", dayMonthLengthKey, Seq("day")))
        val validMonth = month.filter(_.length <= dayMonthLengthMax).toRight(FormError(s"$key.month", dayMonthLengthKey, Seq("month")))
        val validYear = year.filter(_.length == yearLength).toRight(FormError(s"$key.year", yearLengthKey, Seq("year")))

        List(validDay, validMonth, validYear).collect { case Left(err) => err } match {
          case Nil => formatDate(key, data)
          case error :: rem => Left(List(error.copy(args = error.args ++ rem.flatMap(_.args))))
        }
      case 2 =>
        Left(List(FormError(s"$key.${missingKeys.head}", requiredKey, missingKeys.toSeq ++ args)))
      case 1 =>
        Left(List(FormError(s"$key.${missingKeys.head}", twoRequiredKey, missingKeys.toSeq ++ args)))
      case _ =>
        Left(List(FormError(s"$key.day", allRequiredKey, fieldKeys ++ args)))
    }
  }

  override def unbind(key: String, value: LocalDate): Map[String, String] =
    Map(
      s"$key.day" -> value.getDayOfMonth.toString,
      s"$key.month" -> value.getMonthValue.toString,
      s"$key.year" -> value.getYear.toString
    )
}
