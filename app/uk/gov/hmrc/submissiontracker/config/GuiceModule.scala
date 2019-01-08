/*
 * Copyright 2019 HM Revenue & Customs
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
import com.google.inject.name.Names
import com.google.inject.name.Names.named
import javax.inject.Provider
import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import uk.gov.hmrc.api.connector.{ApiServiceLocatorConnector, ServiceLocatorConnector}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.{CorePost, HttpGet}
import uk.gov.hmrc.play.audit.model.Audit
import uk.gov.hmrc.play.bootstrap.auth.DefaultAuthConnector
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.{AppName, ServicesConfig}
import uk.gov.hmrc.submissiontracker.controllers.api.ApiAccess
import uk.gov.hmrc.submissiontracker.tasks.ServiceLocatorRegistrationTask

import scala.collection.JavaConverters._

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule with ServicesConfig {

  override protected lazy val mode: Mode = environment.mode
  override protected lazy val runModeConfiguration: Configuration = configuration

  override def configure(): Unit = {

    bind(classOf[ServiceLocatorConnector]).to(classOf[ApiServiceLocatorConnector])
    bind(classOf[HttpGet]).to(classOf[WSHttpImpl])
    bind(classOf[CorePost]).to(classOf[WSHttpImpl])
    bind(classOf[HttpClient]).to(classOf[WSHttpImpl])
    bind(classOf[ServiceLocatorRegistrationTask]).asEagerSingleton()

    bind(classOf[Audit]).to(classOf[MicroserviceAudit])
    bind(classOf[AuthConnector]).to(classOf[DefaultAuthConnector])

    bind(classOf[ApiAccess]).toInstance(
      ApiAccess("PRIVATE", configuration.underlying.getStringList("api.access.white-list.applicationIds").asScala))

    bindConfigInt("controllers.confidenceLevel")
    bind(classOf[String]).annotatedWith(named("trackingUrl")).toInstance(baseUrl("tracking"))

    bind(classOf[String]).annotatedWith(Names.named("appName")).toProvider(AppNameProvider)
  }

  private object AppNameProvider extends Provider[String] {
    def get(): String = AppName(configuration).appName
  }

  /**
    * Binds a configuration value using the `path` as the name for the binding.
    * Throws an exception if the configuration value does not exist or cannot be read as an Int.
    */
  private def bindConfigInt(path: String): Unit = {
    bindConstant().annotatedWith(named(path))
      .to(configuration.underlying.getInt(path))
  }
}