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

import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.submissiontracker.connector.TrackingConnector
import uk.gov.hmrc.submissiontracker.controllers.LiveSubmissionTrackerController
import uk.gov.hmrc.submissiontracker.services.{LivesubmissiontrackerService, SubmissiontrackerService}

trait TestSetup extends MockitoSugar with UnitSpec with WithFakeApplication with AuthorisationStub {

  trait mocks {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val mockAuthConnector: AuthConnector = mock[AuthConnector]
    implicit val mockAuditConnector: AuditConnector = mock[AuditConnector]
    implicit val mockLivesubmissiontrackerService: LivesubmissiontrackerService = mock[LivesubmissiontrackerService]
    implicit val mockTrackingConnector: TrackingConnector = mock[TrackingConnector]
  }

  val noNinoFoundOnAccount = Json.parse("""{"code":"UNAUTHORIZED","message":"NINO does not exist on account"}""")
  val lowConfidenceLevelError = Json.parse("""{"code":"LOW_CONFIDENCE_LEVEL","message":"Confidence Level on account does not allow access"}""")

  val nino = "CS700100A"
  val incorrectNino = Nino("SC100700A")
  val acceptHeader = "Accept" -> "application/vnd.hmrc.1.0+json"

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(
    "AuthToken" -> "Some Header"
  ).withHeaders(
    acceptHeader,
    "Authorization" -> "Some Header"
  )
  lazy val requestInvalidHeaders: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(
    "AuthToken" -> "Some Header"
  ).withHeaders(
    "Authorization" -> "Some Header"
  )
}

class TestSubmissionTrackingController(override val authConnector: AuthConnector, override val confLevel: Int,
                                       submissionTrackerService: LivesubmissiontrackerService)
  extends LiveSubmissionTrackerController(authConnector: AuthConnector, confLevel: Int) {

  override val service = submissionTrackerService
}
