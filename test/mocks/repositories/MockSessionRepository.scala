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

package mocks.repositories

import base.RepositorySpecBase
import models.UserAnswers
import org.scalamock.handlers.CallHandler
import repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

trait MockSessionRepository extends RepositorySpecBase {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  object MockedSessionRepository {

    def get(response: Future[Option[UserAnswers]]): CallHandler[Future[Option[UserAnswers]]] =
      (mockSessionRepository.get(_: String)(_: ExecutionContext))
        .expects(*, *)
        .returning(response)

    def set(response: Future[Boolean]): CallHandler[Future[Boolean]] =
      (mockSessionRepository.set(_: UserAnswers)(_: ExecutionContext))
        .expects(*, *)
        .returning(response)

    def remove(response: Future[String]): CallHandler[Future[String]] =
      (mockSessionRepository.remove(_: String)(_: ExecutionContext))
        .expects(*, *)
        .returning(response)

  }

}
