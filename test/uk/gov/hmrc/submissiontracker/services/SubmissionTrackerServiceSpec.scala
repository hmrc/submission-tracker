/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.submissiontracker.domain.types.ModelTypes.IdType
import uk.gov.hmrc.submissiontracker.stub.TestSetup

import scala.concurrent.{ExecutionContext, Future}

class SubmissionTrackerServiceSpec extends TestSetup {

  val service =
    new SubmissionTrackerService(mockTrackingConnector, mockAuditConnector, mockFormNameService, configuration, appName)

  "trackingData(id: String, idType: String)" should {
    "return trackingDataSeq with valid date formats" in {
      stubAuditTrackingData(nino.value, idType.value)
      (mockTrackingConnector
        .getUserTrackingData(_: String, _: IdType)(_: HeaderCarrier, _: ExecutionContext))
        .expects(nino.value, idType, *, *)
        .returns(Future successful trackingData)

      (mockFormNameService
        .getFormName(_: String))
        .expects(*)
        .returns("Claim a tax refund")

      await(service.trackingData(nino.value, idType)) shouldBe trackingDataResponseWithCorrectDateFormat
    }
  }
}
