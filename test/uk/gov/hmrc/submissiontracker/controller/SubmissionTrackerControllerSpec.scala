/*
 * Copyright 2016 HM Revenue & Customs
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

import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeApplication
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}


class TestSubmissionTrackingSpec extends UnitSpec with WithFakeApplication with ScalaFutures with StubApplicationConfiguration {

  override lazy val fakeApplication = FakeApplication(additionalConfiguration = config)

  "trackingData Live" should {

    "return the tracking data successfully" in new Success {
      val result: Result = await(controller.trackingData("some-id", "some-id-type")(emptyRequestWithAcceptHeader))

      status(result) shouldBe 200
      contentAsJson(result) shouldBe Json.toJson(trackingData)
      testService.saveDetails shouldBe Map("id" -> "some-id", "idType" -> "some-id-type")
    }

    "return unauthorized when authority record does not contain a NINO" in new AuthWithoutNino {
      val result = await(controller.trackingData("some-id", "some-id-type")(emptyRequestWithAcceptHeader))

      status(result) shouldBe 401
      testService.saveDetails shouldBe Map.empty
    }

    "return status code 406 when the headers are invalid" in new Success {
      val result = await(controller.trackingData("some-id", "some-id-type")(emptyRequest))

      status(result) shouldBe 406

      testService.saveDetails shouldBe Map.empty
    }
  }

  "trackingData Sandbox" should {

    "return the summary response from a static value" in new SandboxSuccess {
      val result = await(controller.trackingData("some-id", "some-id-type")(emptyRequestWithAcceptHeader))

      status(result) shouldBe 200
      contentAsJson(result) shouldBe Json.toJson(trackingData)

      testService.saveDetails shouldBe Map.empty
    }

    "return status code 406 when the Accept header is invalid" in new Success {
      val  result = await(controller.trackingData("some-id", "some-id-type")(emptyRequest))

      status(result) shouldBe 406

      testService.saveDetails shouldBe Map.empty
    }

  }
}
