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

package uk.gov.hmrc.submissiontracker.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submissiontracker.stub.TestSetup

import scala.concurrent.{ExecutionContext, Future}

class SubmissionTrackerServiceSpec extends TestSetup {
  val service = new SubmissionTrackerService(mockTrackingConnector, mockAuditConnector, configuration)

  "trackingData(id: String, idType: String)" should {
    "return trackingDataSeq with valid date formats" in {
      stubAuditTrackingData(nino.value, idType)
      (mockTrackingConnector.getUserTrackingData(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(nino.value, idType, *, *).returns(Future successful trackingData)

      await(service.trackingData(nino.value, idType)) shouldBe trackingDataWithCorrectDateFormat
    }

    "return an IllegalArgumentException with incorrect received date format" in {
      stubAuditTrackingData(nino.value, idType)
      (mockTrackingConnector.getUserTrackingData(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(nino.value, idType, *, *).returns(Future successful trackingDataWithCorrectDateFormat)

      intercept[IllegalArgumentException] {
        await(service.trackingData(nino.value, idType))
      }
    }
  }
}