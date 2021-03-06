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

package mocks.services

import models.{ErrorModel, UpdateCaseResponse}
import org.scalamock.scalatest.MockFactory
import services.UpdateCaseService

import scala.concurrent.Future

trait MockUpdateCaseService extends MockFactory {

  val mockUpdateCaseService: UpdateCaseService = mock[UpdateCaseService]

  def setupMockUpdateCase(response: Either[ErrorModel, UpdateCaseResponse]): Unit = {
    (mockUpdateCaseService.updateCase: () => Future[Either[ErrorModel, UpdateCaseResponse]])
      .expects()
      .returns(Future.successful(response))
  }
}
