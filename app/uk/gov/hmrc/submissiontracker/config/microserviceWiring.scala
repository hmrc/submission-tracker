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

package uk.gov.hmrc.submissiontracker.config

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javax.inject.{Inject, Named}
import play.api.Configuration
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.hooks.HttpHooks
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.ws._

trait Hooks extends HttpHooks with HttpAuditing {
  val hooks = Seq(AuditingHook)
}

trait WSHttp extends HttpClient with WSGet with WSPut with WSPost with WSDelete with WSPatch with Hooks

class WSHttpImpl @Inject() (
  override val wsClient:         WSClient,
  @Named("appName") val appName: String,
  val auditConnector:            AuditConnector,
  val actorSystem:               ActorSystem,
  config:                        Configuration)
    extends HttpClient
    with WSGet
    with WSPut
    with WSPost
    with WSDelete
    with WSPatch
    with Hooks {
  override val configuration: Option[Config] = Option(config.underlying)
}

class MicroserviceAudit @Inject() (
  @Named("appName") val applicationName: String,
  val auditConnector:                    AuditConnector)
    extends Audit(applicationName, auditConnector)
