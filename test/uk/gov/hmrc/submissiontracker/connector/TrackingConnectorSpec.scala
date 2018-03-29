/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.submissiontracker.connector

import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.Json
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.submissiontracker.domain.{Milestone, TrackingData, TrackingDataSeq}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TrackingConnectorSpec
  extends UnitSpec
          with ScalaFutures {

  private trait Setup {

    implicit lazy val hc = HeaderCarrier()

    val milestones =  Seq(Milestone("one","open"))
    val trackingData = TrackingDataSeq(Some(Seq(TrackingData("ref1", "Claim a tax refund", "E4H-384D-EFZ", "some-business", "20160801", "20160620", milestones))))
    val nino = Nino("CS700100A")

    lazy val http500Response = Future.failed(new Upstream5xxResponse("Error", 500, 500))
    lazy val http400Response = Future.failed(new BadRequestException("bad request"))
    lazy val http200Response = Future.successful(HttpResponse(200, Some(Json.toJson(trackingData))))

    lazy val response: Future[HttpResponse] = http400Response

    val connector = new TrackingConnector {
      override lazy val trackingBaseUrl="someurl"

      override lazy val httpGet: HttpGet = new HttpGet {
        override val hooks: Seq[HttpHook] = NoneRequired
        override def doGet(url: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
          response
        }
        override def configuration = None
      }
    }
  }

  "trackingConnector" should {

    "throw BadRequestException when a 400 response is returned" in new Setup {
      override lazy val response = http400Response
        intercept[BadRequestException] {
          await(connector.getUserTrackingData(nino.value, "some-id-type"))
      }
    }

    "throw Upstream5xxResponse when a 500 response is returned" in new Setup {
      override lazy val response = http500Response
      intercept[Upstream5xxResponse] {
        await(connector.getUserTrackingData(nino.value, "some-id-type"))
      }
    }

    "return a valid response when a 200 response is received" in new Setup {
      override lazy val response = http200Response
      val result: TrackingDataSeq = await(connector.getUserTrackingData(nino.value, "some-id-type"))
      result shouldBe trackingData
    }
  }

}
