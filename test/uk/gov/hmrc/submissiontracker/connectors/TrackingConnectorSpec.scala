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

package uk.gov.hmrc.submissiontracker.connectors

import org.scalamock.handlers.CallHandler
import org.scalatest.TestSuite
import play.api.libs.json.{JsResultException, Json}
import uk.gov.hmrc.http.*
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.submissiontracker.domain.TrackingDataSeq
import uk.gov.hmrc.submissiontracker.stub.TestSetup

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class TrackingConnectorSpec extends TestSetup { this: TestSuite =>

  val mockHttpClient:     HttpClientV2   = mock[HttpClientV2]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]
  val trackingBaseUrl = "https://someUrl"
  val connector       = new TrackingConnector(trackingBaseUrl, mockHttpClient)

  def trackingGet[T]: CallHandler[Future[T]] = {
    (mockHttpClient
      .get(_: URL)(_: HeaderCarrier))
      .expects(url"${s"$trackingBaseUrl/tracking-data/user/${idType.value}/${nino.value}"}", *)
      .returns(mockRequestBuilder)

    (mockRequestBuilder
      .execute[T](using _: HttpReads[T], _: ExecutionContext))
      .expects(*, *)

  }

  "trackingConnector" should {

    "throw BadRequestException when a 400 response is returned" in {
      trackingGet.returns(Future failed new BadRequestException("bad request"))

      intercept[BadRequestException] {
        await(connector.getUserTrackingData(nino.value, idType))
      }
    }

    "throw UpstreamErrorResponse when a 500 response is returned" in {
      trackingGet.returns(Future failed UpstreamErrorResponse("Error", 500, 500))

      intercept[UpstreamErrorResponse] {
        await(connector.getUserTrackingData(nino.value, idType))
      }
    }

    "return a valid response when a 200 response is received" in {
      trackingGet.returns(Future successful trackingData)
      await(connector.getUserTrackingData(nino.value, idType)) shouldBe trackingData
    }

    "return a error when DateTime is malformed" in {
      intercept[JsResultException] {
        Json.toJson(trackingDataWithIncorrectDateFormat).as[TrackingDataSeq]
      }
    }
  }
}
