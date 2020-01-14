/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.submissiontracker.support

import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient

import scala.language.postfixOps

class BaseISpec
    extends WordSpecLike
    with Matchers
    with WsScalaTestClient
    with GuiceOneServerPerSuite
    with WireMockSupport {

  override implicit lazy val app: Application = appBuilder
    .build()

  def config: Map[String, Any] = Map(
    "auditing.enabled"                             -> false,
    "microservice.services.auth.port"              -> wireMockPort,
    "microservice.services.tracking.port"          -> wireMockPort,
    "microservice.services.datastream.port"        -> wireMockPort,
    "microservice.services.mobile-shuttering.port" -> wireMockPort
  )

  protected def appBuilder: GuiceApplicationBuilder = new GuiceApplicationBuilder().configure(config)

  protected implicit lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]

  protected val nino   = "CS700100A"
  protected val idType = "nino"
  protected val acceptJsonHeader:   (String, String) = "Accept"           -> "application/vnd.hmrc.1.0+json"
  protected val mobileUserIdHeader: (String, String) = "X-MOBILE-USER-ID" -> "208606423740"
  protected val journeyIdUrlVar = "?journeyId=decf6382-0c09-4ea8-8225-d59d188db41f"

}
