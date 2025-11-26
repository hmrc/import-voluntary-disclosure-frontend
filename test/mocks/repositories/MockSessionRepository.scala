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

package mocks.repositories

import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar.mock
import repositories.SessionRepository

import scala.concurrent.Future

trait MockSessionRepository {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  object MockedSessionRepository {

    def get(response: Future[Option[UserAnswers]]): OngoingStubbing[Future[Option[UserAnswers]]] =
      when(mockSessionRepository.get(any())(any()))
        .thenReturn(response)

    def set(response: Future[Boolean]): OngoingStubbing[Future[Boolean]] =
      when(mockSessionRepository.set(any())(any()))
        .thenReturn(response)

    def remove(response: Future[String]): OngoingStubbing[Future[String]] =
      when(mockSessionRepository.remove(any())(any()))
        .thenReturn(response)

  }

}
