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

package uk.gov.hmrc.submissiontracker.controller

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.ConfidenceLevel._
import uk.gov.hmrc.auth.core.syntax.retrieved._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submissiontracker.controllers.LiveSubmissionTrackerController
import uk.gov.hmrc.submissiontracker.domain.{Milestone, TrackingData, TrackingDataSeq}
import uk.gov.hmrc.submissiontracker.stub.{TestSetup, TestSubmissionTrackingController}


class TestSubmissionTrackingSpec extends TestSetup {

  "trackingData Live" should {

    "return the tracking data successfully" in new mocks {
      stubAuthorisationGrantAccess(Some(nino) and L200)
      val milestones =  Seq(Milestone("one","open"))
      val trackingData = TrackingDataSeq(Some(Seq(TrackingData("E4H-384D-EFZ", "Claim a tax refund", "ref1", "some-business", "20160801", "20160620", milestones))))
      when(mockLivesubmissiontrackerService.trackingData(any[String](), any[String]())(any[HeaderCarrier]()))
        .thenReturn(trackingData)
      val controller = new TestSubmissionTrackingController(mockAuthConnector, 200, mockLivesubmissiontrackerService)
      val result: Result = await(controller.trackingData(nino, "some-id-type").apply(fakeRequest))

      status(result) shouldBe 200
      contentAsJson(result) shouldBe Json.toJson(trackingData)
    }

    "return the tracking data successfully when journeyId is supplied" in new mocks {
      stubAuthorisationGrantAccess(Some(nino) and L200)
      val milestones =  Seq(Milestone("one","open"))
      val trackingData = TrackingDataSeq(Some(Seq(TrackingData("E4H-384D-EFZ", "Claim a tax refund", "ref1", "some-business", "20160801", "20160620", milestones))))
      when(mockLivesubmissiontrackerService.trackingData(any[String](), any[String]())(any[HeaderCarrier]()))
        .thenReturn(trackingData)
      val controller = new TestSubmissionTrackingController(mockAuthConnector, 200, mockLivesubmissiontrackerService)
      val result: Result = await(controller.trackingData(nino, "some-id-type", Some("unique-journey-id")).apply(fakeRequest))

      status(result) shouldBe 200
      contentAsJson(result) shouldBe Json.toJson(trackingData)
    }

    "return unauthorized when authority record does not contain a NINO" in new mocks {
      stubAuthorisationGrantAccess(Some("") and L200)
      val controller = new LiveSubmissionTrackerController(mockAuthConnector, 200)
      status(await(controller.trackingData(nino, "some-id-type").apply(fakeRequest))) shouldBe 401
    }

    "return 401 when the nino in the request does not match the authority nino" in new mocks {
      stubAuthorisationGrantAccess(Some("") and L200)
      val controller = new LiveSubmissionTrackerController(mockAuthConnector, 200)
      status(await(controller.trackingData(incorrectNino.value, "some-id-type").apply(fakeRequest))) shouldBe 401
    }

    "return forbidden when authority record does not have correct confidence level" in new mocks {
      stubAuthorisationGrantAccess(Some(nino) and L100)
      val controller = new LiveSubmissionTrackerController(mockAuthConnector, 200)
      status(await(controller.trackingData(incorrectNino.value, "some-id-type").apply(fakeRequest))) shouldBe  403
    }

//    "return status code 406 when the headers are invalid" in new Success {
//      val result = await(controller.trackingData(nino.value, "some-id-type")(emptyRequest))
//
//      status(result) shouldBe 406
//
//      testService.saveDetails shouldBe Map.empty
//    }
//  }
//
//  "trackingData Sandbox" should {
//
//    "return the summary response from a static value" in new SandboxSuccess {
//      val result = await(controller.trackingData(nino.value, "some-id-type")(emptyRequestWithAcceptHeader))
//
//      status(result) shouldBe 200
//      contentAsJson(result) shouldBe Json.toJson(trackingData)
//
//      testService.saveDetails shouldBe Map.empty
//    }
//
//    "return status code 406 when the Accept header is invalid" in new Success {
//      val  result = await(controller.trackingData(nino.value, "some-id-type")(emptyRequest))
//
//      status(result) shouldBe 406
//
//      testService.saveDetails shouldBe Map.empty
//    }
//
  }
}
