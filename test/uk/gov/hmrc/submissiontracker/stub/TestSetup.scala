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

package uk.gov.hmrc.submissiontracker.stub

import eu.timepit.refined.auto.*
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.submissiontracker.connectors.{ShutteringConnector, TrackingConnector}
import uk.gov.hmrc.submissiontracker.domain.*
import uk.gov.hmrc.submissiontracker.domain.types.{IdType, JourneyId}
import uk.gov.hmrc.submissiontracker.services.{AuditService, FormNameService, SubmissionTrackerService}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

trait TestSetup
    extends AnyWordSpecLike
    with MockFactory
    with Matchers
    with AuthorisationStub
    with AuditStub
    with ScalaFutures
    with FutureAwaits
    with DefaultAwaitTimeout
    with ShutteringStub { this: TestSuite =>

  implicit val hc:                           HeaderCarrier            = HeaderCarrier()
  implicit val mockAuthConnector:            AuthConnector            = mock[AuthConnector]
  implicit val mockAuditConnector:           AuditConnector           = mock[AuditConnector]
  implicit val mockShutteringConnector:      ShutteringConnector      = mock[ShutteringConnector]
  val mockSubmissionTrackerService: SubmissionTrackerService = mock[SubmissionTrackerService]
  val mockTrackingConnector:        TrackingConnector        = mock[TrackingConnector]
  val mockFormNameService:          FormNameService          = mock[FormNameService]
  val mockAuditService:             AuditService             = new AuditService("submission-tracker", mockAuditConnector)

  val shuttered: Shuttering =
    Shuttering(shuttered = true, Some("Shuttered"), Some("Form Tracker is currently not available"))
  val notShuttered: Shuttering = Shuttering.shutteringDisabled

  val configuration: Configuration = mock[Configuration]

  lazy val requestWithAcceptHeader:    FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(acceptHeader)
  lazy val requestWithoutAcceptHeader: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val noNinoFoundOnAccount: JsValue =
    Json.parse("""{"code":"UNAUTHORIZED","message":"NINO does not exist on account"}""")

  val lowConfidenceLevelError: JsValue =
    Json.parse("""{"code":"LOW_CONFIDENCE_LEVEL","message":"Confidence Level on account does not allow access"}""")

  val nino: Nino = Nino("CS700100A")
  val incorrectNino: Nino = Nino("SC100700A")
  val acceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1.0+json"
  val idType:       IdType           = IdType.fromStringToId("nino").toOption.get
  val journeyId:    JourneyId        = JourneyId.from("decf6382-0c09-4ea8-8225-d59d188db41f").toOption.get

  val milestones: Seq[Milestone] =
    Seq(
      Milestone("Received", "complete"),
      Milestone("Acquired", "complete"),
      Milestone("InProgress", "current"),
      Milestone("Done", "incomplete")
    )

  val trackingData: TrackingDataSeq = TrackingDataSeq(
    Some(
      Seq(
        TrackingData("R39_EN",
                     "Claim a tax refund",
                     "111-ABCD-456",
                     "PSA",
                     LocalDateTime.of(2015, 4, 12, 0, 0),
                     LocalDateTime.of(2015, 5, 17, 0, 0),
                     milestones)
      )
    )
  )

  val trackingDataWithIncorrectDateFormat: JsValue =
    Json.parse("""
                 |{
                 |   "submissions":[
                 |      {
                 |         "formId":"R39_EN",
                 |         "formName":"Claim a tax refund",
                 |         "dfsSubmissionReference":"111-ABCD-456",
                 |         "businessArea":"PSA",
                 |         "receivedDate":"INVALIDDATE",
                 |         "completionDate":"2015-05-17T00:00:00.000+01:00",
                 |         "milestones":[
                 |            {
                 |               "milestone":"Received",
                 |               "status":"complete"
                 |            },
                 |            {
                 |               "milestone":"Acquired",
                 |               "status":"complete"
                 |            },
                 |            {
                 |               "milestone":"InProgress",
                 |               "status":"current"
                 |            },
                 |            {
                 |               "milestone":"Done",
                 |               "status":"incomplete"
                 |            }
                 |         ]
                 |      }
                 |   ]
                 |}
                 |""".stripMargin)

  val trackingDataResponse: TrackingDataSeqResponse = TrackingDataSeqResponse(
    Some(
      Seq(
        TrackingDataResponse(
          "R39_EN",
          "Claim a tax refund",
          "Hawlio ad-daliad treth",
          "111-ABCD-456",
          LocalDateTime.of(2015, 4, 12, 0, 0),
          LocalDateTime.of(2015, 5, 17, 0, 0),
          "InProgress",
          milestones
        )
      )
    )
  )

  val trackingDataResponseWithCorrectDateFormat: TrackingDataSeqResponse = TrackingDataSeqResponse(
    Some(
      Seq(
        TrackingDataResponse(
          "R39_EN",
          "Claim a tax refund",
          "Hawlio ad-daliad treth",
          "111-ABCD-456",
          LocalDateTime.of(2015, 4, 12, 0, 0),
          LocalDateTime.of(2015, 5, 17, 0, 0),
          "InProgress",
          milestones
        )
      )
    )
  )

  val sandboxTrackingDataResponseWithCorrectDateFormat: TrackingDataSeqResponse = TrackingDataSeqResponse(
    Some(
      Seq(
        TrackingDataResponse(
          "R39_EN",
          "Claim a tax refund",
          "Hawlio ad-daliad treth",
          "111-ABCD-456",
          LocalDateTime.now().minusDays(3),
          LocalDateTime.now().plusDays(5),
          "InProgress",
          milestones
        )
      )
    )
  )

}
