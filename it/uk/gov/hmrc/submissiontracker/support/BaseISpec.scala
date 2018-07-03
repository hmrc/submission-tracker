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

import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import uk.gov.hmrc.play.test.UnitSpec

import scala.language.postfixOps

class BaseISpec extends UnitSpec with WsScalaTestClient with GuiceOneServerPerSuite with WireMockSupport {
  override implicit lazy val app: Application = appBuilder
    .build()

  protected def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "auditing.enabled" -> false,
        "microservice.services.service-locator.enabled" -> false,
        "microservice.services.service-locator.port" -> wireMockPort,
        "microservice.services.auth.port" -> wireMockPort,
        "microservice.services.tracking.port" -> wireMockPort,
        "microservice.services.datastream.port" -> wireMockPort
      )

  protected implicit lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]

  protected val nino = "CS700100A"
  protected val idType = "some-id-type"
  protected val acceptJsonHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1.0+json"
  protected val mobileUserIdHeader: (String, String) = "X-MOBILE-USER-ID" -> "208606423740"

}
