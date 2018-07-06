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

package uk.gov.hmrc.submissiontracker.controllers

import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.ConfidenceLevel._
import uk.gov.hmrc.auth.core.syntax.retrieved._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submissiontracker.stub.TestSetup

import scala.concurrent.Future

class SubmissionTrackerControllerSpec extends TestSetup {

  "trackingData Live" should {
    val controller = new SubmissionTrackerController(mockAuthConnector, mockSubmissionTrackerService, L200.level)

    "return the tracking data successfully" in {
      stubAuthorisationGrantAccess(Some(nino.value) and L200)
      (mockSubmissionTrackerService.trackingData(_: String, _: String)(_: HeaderCarrier))
        .expects(nino.value, idType, *).returns(Future successful trackingData)

      val result: Result = await(controller.trackingData(nino.value, idType)(requestWithAcceptHeader))

      status(result) shouldBe 200
      contentAsJson(result) shouldBe Json.toJson(trackingData)
    }

    "return the tracking data successfully when journeyId is supplied" in {
      stubAuthorisationGrantAccess(Some(nino.value) and L200)
      (mockSubmissionTrackerService.trackingData(_: String, _: String)(_: HeaderCarrier))
        .expects(nino.value, idType, *).returns(Future successful trackingData)


      val result: Result = await(controller.trackingData(nino.value, idType, Some("unique-journey-id"))(requestWithAcceptHeader))

      status(result) shouldBe 200
      contentAsJson(result) shouldBe Json.toJson(trackingData)
    }

    "return unauthorized when authority record does not contain a NINO" in {
      stubAuthorisationGrantAccess(Some("") and L200)
      status(await(controller.trackingData(nino.value, idType)(requestWithAcceptHeader))) shouldBe 401
    }

    "return 401 when the nino in the request does not match the authority nino" in {
      stubAuthorisationGrantAccess(Some("") and L200)
      status(await(controller.trackingData(incorrectNino.value, idType)(requestWithAcceptHeader))) shouldBe 401
    }

    "return forbidden when authority record does not have correct confidence level" in {
      stubAuthorisationGrantAccess(Some(nino.value) and L100)
      status(await(controller.trackingData(incorrectNino.value, idType)(requestWithAcceptHeader))) shouldBe 401
    }

    "return status code 406 when the accept header is missing" in {
      status(await(controller.trackingData(incorrectNino.value, idType)(requestWithoutAcceptHeader))) shouldBe 406
    }
  }

  "trackingData Sandbox" should {

    val controller = new SandboxSubmissionTrackerController

    "return the summary response from a static value" in {
      val result = await(controller.trackingData(nino.value, idType)(requestWithAcceptHeader))

      status(result) shouldBe 200
      contentAsJson(result) shouldBe Json.toJson(trackingDataWithCorrectDateFormat)
    }

    "return status code 406 when the Accept header is missing" in {
      val result = await(controller.trackingData(nino.value, idType)(requestWithoutAcceptHeader))

      status(result) shouldBe 406

    }
  }
}