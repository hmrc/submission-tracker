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

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import org.joda.time.format.DateTimeFormat
import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.model.{Audit, DataEvent}
import uk.gov.hmrc.submissiontracker.connector._
import uk.gov.hmrc.submissiontracker.domain.{Milestone, TrackingData, TrackingDataSeq}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait SubmissiontrackerService {
  def trackingData(id: String, idType:String)(implicit hc:HeaderCarrier): Future[TrackingDataSeq]
}

@ImplementedBy(classOf[LivesubmissiontrackerServiceImpl])
trait LivesubmissiontrackerService extends SubmissiontrackerService {
  def trackingConnector: TrackingConnector
  val inFormat = DateTimeFormat.forPattern("dd MMM yyyy")
  val outFormat = DateTimeFormat.forPattern("yyyyMMdd")
  val auditing: Audit


  def audit(service:String, details:Map[String, String])(implicit hc:HeaderCarrier) = {
    def auditResponse(): Unit = {
      auditing.sendDataEvent(
        DataEvent("submission-tracker", "ServiceResponseSent",
          tags = Map("transactionName" -> service),
          detail = details))
    }
  }

  def withAudit[T](service: String, details: Map[String, String])(func:Future[T])(implicit hc:HeaderCarrier) = {
    audit(service, details) // No need to wait!
    func
  }

  private def convert(in:String) = outFormat.print(inFormat.parseDateTime(in))
  private def convertData(data:TrackingDataSeq): TrackingDataSeq = {
    data.submissions.fold(data){ found =>
      TrackingDataSeq(Some(found.map(item => {
        item.copy(completionDate = convert(item.completionDate), receivedDate = convert(item.receivedDate))
      })))
    }
  }

  def trackingData(id: String, idType:String)(implicit hc:HeaderCarrier): Future[TrackingDataSeq] = {
    withAudit("trackingData", Map("id" -> id, "idType" -> idType)) {
      trackingConnector.getUserTrackingData(id, idType).map(data => convertData(data))
    }
  }

}

object SandboxsubmissiontrackerService extends SubmissiontrackerService with FileResource {

  def trackingData(id: String, idType:String)(implicit hc:HeaderCarrier): Future[TrackingDataSeq] = {
    val milestones =  Seq(
      Milestone("Received","complete"),
      Milestone("Acquired","complete"),
      Milestone("InProgress","current"),
      Milestone("Done","incomplete"))
    val trackingData = TrackingDataSeq(Some(Seq(
      TrackingData("ref1", "Claim a tax refund", "E4H-384D-EFZ", "some-business", "20160801", "20160620", milestones))))
    Future.successful(trackingData)
  }
}

@Singleton
class LivesubmissiontrackerServiceImpl @Inject()(override val trackingConnector: TrackingConnector, val auditing: Audit) extends LivesubmissiontrackerService
