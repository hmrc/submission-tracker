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

package uk.gov.hmrc.submissiontracker.connectors

import uk.gov.hmrc.http._
import uk.gov.hmrc.submissiontracker.domain.TrackingDataSeq
import uk.gov.hmrc.submissiontracker.stub.TestSetup

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class TrackingConnectorSpec extends TestSetup {

  val connector = new TrackingConnector("someUrl", mockHttp)

  def trackingGetSuccess(response: TrackingDataSeq): Unit =
    (mockHttp.GET(_: String)(_: HttpReads[TrackingDataSeq], _: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *).returns(Future successful response)

  def trackingGetFailure(response: Exception): Unit =
    (mockHttp.GET(_: String)(_: HttpReads[TrackingDataSeq], _: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *).returns(Future failed response)

  "trackingConnector" should {

    "throw BadRequestException when a 400 response is returned" in {
      trackingGetFailure(new BadRequestException("bad request"))

      intercept[BadRequestException] {
        await(connector.getUserTrackingData(nino.value, "some-id-type"))
      }
    }

    "throw Upstream5xxResponse when a 500 response is returned" in {
      trackingGetFailure(Upstream5xxResponse("Error", 500, 500))

      intercept[Upstream5xxResponse] {
        await(connector.getUserTrackingData(nino.value, "some-id-type"))
      }
    }

    "return a valid response when a 200 response is received" in {
      trackingGetSuccess(trackingData)

      await(connector.getUserTrackingData(nino.value, "some-id-type")) shouldBe trackingData
    }
  }
}