///*
// * Copyright 2017 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package uk.gov.hmrc.submissiontracker.controller
//
//import java.util.UUID
//
//import play.api.libs.json.{JsValue, Json}
//import play.api.test.FakeRequest
//import play.api.test.Helpers._
//import uk.gov.hmrc.auth.core.AuthConnector
//import uk.gov.hmrc.domain.Nino
//import uk.gov.hmrc.http._
//import uk.gov.hmrc.submissiontracker.connector.TrackingConnector
//import uk.gov.hmrc.submissiontracker.controllers.SubmissionTrackerController
//import uk.gov.hmrc.submissiontracker.domain.{Milestone, TrackingData, TrackingDataSeq}
//import uk.gov.hmrc.submissiontracker.services.{LivesubmissiontrackerService, SandboxsubmissiontrackerService, SubmissiontrackerService}
//
//import scala.concurrent.{ExecutionContext, Future}
//
//class TestTrackingConnector(trackingDetails:TrackingDataSeq) extends TrackingConnector {
//  override def httpGet: CoreGet = throw new Exception("Should not be called")
//
//  override def getUserTrackingData(id: String,idType:String)(implicit hc: HeaderCarrier, executionContext: ExecutionContext): Future[TrackingDataSeq] = {
//    Future.successful(trackingDetails)
//  }
//}
//
//class TestSubmissionTrackingService(authConnector:AuthConnector, testTrackingConnector:TestTrackingConnector) extends LivesubmissiontrackerService {
//  var saveDetails:Map[String, String]=Map.empty
//
//  override val trackingConnector = testTrackingConnector
//
//  override def audit(method:String, details:Map[String, String])(implicit hc: HeaderCarrier): Unit = {
//    saveDetails=details
//  }
//}
//
//trait Setup {
//  implicit val hc = HeaderCarrier()
//
//  val journeyId = Option(UUID.randomUUID().toString)
//
//  val milestones =  Seq(Milestone("one","open"))
//  val trackingData = TrackingDataSeq(Some(Seq(TrackingData("E4H-384D-EFZ", "Claim a tax refund", "ref1", "some-business", "20160801", "20160620", milestones))))
//  val trackingDataConnector = TrackingDataSeq(Some(Seq(TrackingData("E4H-384D-EFZ", "Claim a tax refund", "ref1", "some-business", "01 Aug 2016", "20 June 2016", milestones))))
//  val nino = Nino("CS700100A")
//
//  def fakeRequest(body:JsValue) = FakeRequest(POST, "url").withBody(body)
//    .withHeaders("Content-Type" -> "application/json")
//
//  val emptyRequest = FakeRequest()
//
//  val emptyRequestWithAcceptHeader = FakeRequest().withHeaders(
//    "Accept" -> "application/vnd.hmrc.1.0+json")
//
//  lazy val badRequest = fakeRequest(Json.toJson("Something Incorrect")).withHeaders(
//    "Accept" -> "application/vnd.hmrc.1.0+json")
//
//  val authConnector =
//  val trackingConnector = new TestTrackingConnector(trackingDataConnector)
//  val testAccess = new TestAccessCheck(authConnector)
//  val testCompositeAction = new TestAccountAccessControlWithAccept(testAccess)
//  val testService = new TestSubmissionTrackingService(authConnector, trackingConnector)
//
//  val testSandboxPersonalIncomeService = SandboxsubmissiontrackerService
//  val sandboxCompositeAction = AccountAccessControlCheckOff
//}
//
//trait Success extends Setup {
//  val controller = new SubmissionTrackerController {
//    override val service: SubmissiontrackerService = testService
//    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
//  }
//}
//
//trait AuthWithoutNino extends Setup {
//
//  override val authConnector =  new TestAuthConnector(None) {
//    override def grantAccess(taxId:Option[Nino])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
//      Future.failed(new Upstream4xxResponse("Error", 401, 401))
//  }
//
//  override val testAccess = new TestAccessCheck(authConnector)
//  override val trackingConnector = new TestTrackingConnector(trackingDataConnector)
//  override val testCompositeAction = new TestAccountAccessControlWithAccept(testAccess)
//  override val testService = new TestSubmissionTrackingService(authConnector, trackingConnector)
//
//  val controller = new SubmissionTrackerController {
//    override val service: SubmissiontrackerService = testService
//    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
//  }
//}
//
//trait SandboxSuccess extends Setup {
//  val controller = new SubmissionTrackerController {
//    override val service: SubmissiontrackerService = testSandboxPersonalIncomeService
//    override val accessControl: AccountAccessControlWithHeaderCheck = sandboxCompositeAction
//  }
//}
//
//trait AccessCheck extends Setup {
//  override val authConnector = new TestAuthConnector(Some(Nino("CS123456A")), Some(new FailToMatchTaxIdOnAuth("controlled explosion")))
//  override val testAccess = new TestAccessCheck(authConnector)
//  override val testCompositeAction = new TestAccountAccessControlWithAccept(testAccess)
//
//  val controller = new SubmissionTrackerController {
//    override val service: SubmissiontrackerService = testSandboxPersonalIncomeService
//    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
//  }
//}
//
//trait AuthWithLowCL extends Setup {
//
//  override val authConnector =  new TestAuthConnector(None) {
//    override def grantAccess(taxId:Option[Nino])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {
//      Future.failed(new ForbiddenException("Error"))
//    }
//  }
//
//  override val testAccess = new TestAccessCheck(authConnector)
//  override val trackingConnector = new TestTrackingConnector(trackingDataConnector)
//  override val testCompositeAction = new TestAccountAccessControlWithAccept(testAccess)
//  override val testService = new TestSubmissionTrackingService(authConnector, trackingConnector)
//
//
//  val controller = new SubmissionTrackerController {
//    override val service: SubmissiontrackerService = testService
//    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
//  }
//
//}
