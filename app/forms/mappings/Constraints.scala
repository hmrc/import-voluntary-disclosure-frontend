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

package forms.mappings

import forms.mappings.filters.InputFilter
import play.api.data.validation.{Constraint, Invalid, Valid}

trait Constraints extends InputFilter {
  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint { input =>
      constraints
        .map(_.apply(input))
        .find(_ != Valid)
        .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input >= minimum) {
        Valid
      } else {
        Invalid(errorKey, minimum)
      }
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input <= maximum) {
        Valid
      } else {
        Invalid(errorKey, maximum)
      }
    }

  protected def inRange[A](minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input >= minimum && input <= maximum) {
        Valid
      } else {
        Invalid(errorKey, minimum, maximum)
      }
    }

  protected def lengthBetween(minimum: Int, maximum: Int, errorKey: String): Constraint[String] =
    Constraint { input =>
      if (input.length >= minimum && input.length <= maximum) {
        Valid
      } else {
        Invalid(errorKey, minimum, maximum)
      }
    }

  protected def regexp(regex: String, errorKey: String, args: Seq[Any] = Seq.empty): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }

  protected def maxLength(
    maximum: Int,
    errorKey: String,
    transformation: String => String = identity
  ): Constraint[String] =
    Constraint {
      case str if transformation(str.trim).length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def uniqueEntry(values: Seq[String], idx: Int, errorKey: String): Constraint[String] = {

    val filteredValues = values.zipWithIndex.filterNot(_._2 == idx - 1).map(_._1)

    Constraint {
      case str if filteredValues.contains(str) =>
        Invalid(errorKey, values)
      case _ =>
        Valid
    }
  }

  protected def nonEmptySet(errorKey: String): Constraint[Set[_]] =
    Constraint {
      case set if set.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  def nonEmptySeq(errorMessage: String): Constraint[Seq[_]] =
    Constraint {
      case seq if seq.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorMessage)
    }

}
