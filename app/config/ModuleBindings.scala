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

package config

import controllers.actions._
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import repositories._

class ModuleBindings extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[IdentifierAction].to(classOf[AuthenticatedIdentifierAction]),
    bind[PrivateIndividualAuthAction].to(classOf[PrivateIndividualAuthenticationAction]),
    bind[DataRequiredAction].to(classOf[DataRequiredActionImpl]),
    bind[DataRetrievalAction].to(classOf[DataRetrievalActionImpl]),
    bind[AppConfig].to(classOf[AppConfigImpl]),
    bind[SessionRepository].to(classOf[UserAnswersRepository]),
    bind[FileUploadRepository].to(classOf[FileUploadRepositoryImpl])
  )

}
