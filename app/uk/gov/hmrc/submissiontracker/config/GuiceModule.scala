/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.submissiontracker.config

import com.google.inject.AbstractModule
import com.google.inject.name.Names.named
import play.api.{Configuration, Environment}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.play.audit.model.Audit
import uk.gov.hmrc.play.bootstrap.auth.DefaultAuthConnector
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.submissiontracker.controllers.api.ApiAccess

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  val servicesConfig = new ServicesConfig(configuration)

  override def configure(): Unit = {
    bind(classOf[Audit]).to(classOf[MicroserviceAudit])
    bind(classOf[AuthConnector]).to(classOf[DefaultAuthConnector])

    bind(classOf[ApiAccess]).toInstance(ApiAccess("PRIVATE"))

    bindConfigInt("controllers.confidenceLevel")
    bind(classOf[String])
      .annotatedWith(named("trackingUrl"))
      .toInstance(servicesConfig.baseUrl("tracking"))

    bind(classOf[String])
      .annotatedWith(named("mobile-shuttering"))
      .toInstance(servicesConfig.baseUrl("mobile-shuttering"))
  }

  /** Binds a configuration value using the `path` as the name for the binding. Throws an exception if the configuration value does not exist or
    * cannot be read as an Int.
    */
  private def bindConfigInt(path: String): Unit =
    bindConstant()
      .annotatedWith(named(path))
      .to(configuration.underlying.getInt(path))
}
