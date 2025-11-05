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

import models.upscan.FileUpload
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar.mock
import repositories.FileUploadRepository

import scala.concurrent.Future

trait MockFileUploadRepository {

  val mockFileUploadRepository: FileUploadRepository = mock[FileUploadRepository]

  object MockedFileUploadRepository {

    def updateRecord(response: Future[Boolean]): OngoingStubbing[Future[Boolean]] =
      when(mockFileUploadRepository.updateRecord(any())(any()))
        .thenReturn(response)

    def getRecord(response: Future[Option[FileUpload]]): OngoingStubbing[Future[Option[FileUpload]]] =
      when(mockFileUploadRepository.getRecord(any())(any()))
        .thenReturn(response)

  }

}
