/*
 * Copyright 2022 HM Revenue & Customs
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

package base

import controllers.actions._

trait ControllerSpecBase extends SpecBase {
  lazy val authenticatedAction: IdentifierAction =
    FakeIdentifierAction.identifierAction(
      messagesControllerComponents.parsers.anyContent,
      "some_external_id",
      "GB987654321000"
    )

  lazy val privateIndividualAuthAction: PrivateIndividualAuthAction =
    FakePrivateIndividualAuthAction.identifierAction(
      messagesControllerComponents.parsers.anyContent,
      "some_external_id",
      "GB987654321000"
    )

  lazy val dataRequiredAction: DataRequiredAction = injector.instanceOf[DataRequiredActionImpl]
}
