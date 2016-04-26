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

import play.api.libs.json.{Json, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.auth.microservice.connectors.ConfidenceLevel
import uk.gov.hmrc.play.http.{Upstream4xxResponse, HeaderCarrier, HttpGet}
import uk.gov.hmrc.submissiontracker.connector.{AuthConnector, TrackingConnector}
import uk.gov.hmrc.submissiontracker.controllers.SubmissionTrackerController
import uk.gov.hmrc.submissiontracker.controllers.action.{AccountAccessControlForSandbox, AccountAccessControlWithHeaderCheck, AccountAccessControl}
import uk.gov.hmrc.submissiontracker.domain.{TrackingData, Milestone, Accounts, TrackingDataSeq}
import uk.gov.hmrc.submissiontracker.services.{SubmissiontrackerService, SandboxsubmissiontrackerService, LivesubmissiontrackerService}

import scala.concurrent.{ExecutionContext, Future}

class TestTrackingConnector(trackingDetails:TrackingDataSeq) extends TrackingConnector {
  override def httpGet: HttpGet = ???

  override def getUserTrackingData(id: String,idType:String)(implicit hc: HeaderCarrier): Future[TrackingDataSeq] = {
    Future.successful(trackingDetails)
  }
}

class TestAuthConnector(nino:Option[Nino]) extends AuthConnector {
  override val serviceUrl: String = "someUrl"

  override def serviceConfidenceLevel: ConfidenceLevel = ???

  override def http: HttpGet = ???

  override def accounts()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Accounts] = Future(Accounts(nino, None))

  override def grantAccess()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = Future(Unit)
}

class TestSubmissionTrackingService(testAuthConnector:TestAuthConnector, testTrackingConnector:TestTrackingConnector) extends LivesubmissiontrackerService {
  var saveDetails:Map[String, String]=Map.empty

  override def authConnector: AuthConnector = testAuthConnector

  override def ping()(implicit hc: HeaderCarrier): Future[Boolean] = ???

  override val trackingConnector = testTrackingConnector

  override def audit(method:String, details:Map[String, String])(implicit hc: HeaderCarrier): Unit = {
    saveDetails=details
  }

}

class TestAccessCheck(testAuthConnector: TestAuthConnector) extends AccountAccessControl {
  override val authConnector: AuthConnector = testAuthConnector
}

class TestAccountAccessControlWithAccept(testAccessCheck:AccountAccessControl) extends AccountAccessControlWithHeaderCheck {
  override val accessControl: AccountAccessControl = testAccessCheck
}


trait Setup {
  implicit val hc = HeaderCarrier()

  val milestones =  Seq(Milestone("one","open"))
  val trackingData = TrackingDataSeq(Some(Seq(TrackingData("formId", "formName", "ref1", "some-business", "20150801", "20150801", milestones))))
  val nino = Nino("CS700100A")

  def fakeRequest(body:JsValue) = FakeRequest(POST, "url").withBody(body)
    .withHeaders("Content-Type" -> "application/json")

  val emptyRequest = FakeRequest()

  val emptyRequestWithAcceptHeader = FakeRequest().withHeaders(
    "Accept" -> "application/vnd.hmrc.1.0+json")

  lazy val badRequest = fakeRequest(Json.toJson("Something Incorrect")).withHeaders(
    "Accept" -> "application/vnd.hmrc.1.0+json")

  val authConnector = new TestAuthConnector(Some(nino))
  val trackingConnector = new TestTrackingConnector(trackingData)
  val testAccess = new TestAccessCheck(authConnector)
  val testCompositeAction = new TestAccountAccessControlWithAccept(testAccess)
  val testService = new TestSubmissionTrackingService(authConnector, trackingConnector)

  val testSandboxPersonalIncomeService = SandboxsubmissiontrackerService
  val sandboxCompositeAction = AccountAccessControlForSandbox
}

trait Success extends Setup {
  val controller = new SubmissionTrackerController {
    override val service: SubmissiontrackerService = testService
    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
  }
}

trait AuthWithoutNino extends Setup {

  override val authConnector =  new TestAuthConnector(None) {
    override def grantAccess()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = Future.failed(new Upstream4xxResponse("Error", 401, 401))
  }

  override val testAccess = new TestAccessCheck(authConnector)
  override val trackingConnector = new TestTrackingConnector(trackingData)
  override val testCompositeAction = new TestAccountAccessControlWithAccept(testAccess)
  override val testService = new TestSubmissionTrackingService(authConnector, trackingConnector)

  val controller = new SubmissionTrackerController {
    override val service: SubmissiontrackerService = testService
    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
  }
}

trait SandboxSuccess extends Setup {
  val controller = new SubmissionTrackerController {
    override val service: SubmissiontrackerService = testSandboxPersonalIncomeService
    override val accessControl: AccountAccessControlWithHeaderCheck = sandboxCompositeAction
  }
}
