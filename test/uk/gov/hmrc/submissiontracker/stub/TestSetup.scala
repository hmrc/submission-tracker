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

package uk.gov.hmrc.submissiontracker.stub

import org.scalamock.scalatest.MockFactory
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.submissiontracker.domain.{Milestone, TrackingData, TrackingDataSeq}
import uk.gov.hmrc.submissiontracker.services.SubmissionTrackerService

trait TestSetup extends MockFactory with UnitSpec with WithFakeApplication with AuthorisationStub {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val mockAuthConnector: AuthConnector = mock[AuthConnector]
  implicit val mockAuditConnector: AuditConnector = mock[AuditConnector]
  implicit val mockSubmissionTrackerService: SubmissionTrackerService = mock[SubmissionTrackerService]

  implicit val mockHttp: HttpGet = mock[HttpGet]

  lazy val requestWithAcceptHeader: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(acceptHeader)
  lazy val requestWithoutAcceptHeader: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val noNinoFoundOnAccount: JsValue = Json.parse("""{"code":"UNAUTHORIZED","message":"NINO does not exist on account"}""")
  val lowConfidenceLevelError: JsValue = Json.parse("""{"code":"LOW_CONFIDENCE_LEVEL","message":"Confidence Level on account does not allow access"}""")

  val nino = Nino("CS700100A")
  val incorrectNino = Nino("SC100700A")
  val acceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1.0+json"

  val milestones =  Seq(
    Milestone("Received","complete"),
    Milestone("Acquired","complete"),
    Milestone("InProgress","current"),
    Milestone("Done","incomplete"))

  val trackingData = TrackingDataSeq(Some(Seq(TrackingData("ref1", "Claim a tax refund", "E4H-384D-EFZ", "some-business", "20160801", "20160620", milestones))))

}