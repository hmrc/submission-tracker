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
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.model.{Audit, DataEvent}
import uk.gov.hmrc.submissiontracker.connectors._
import uk.gov.hmrc.submissiontracker.connectors.TrackingConnector
import uk.gov.hmrc.submissiontracker.domain.TrackingDataSeq

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SubmissionTrackerService @Inject()(val trackingConnector: TrackingConnector,
                                         val auditing: Audit) {
  val inFormat: DateTimeFormatter = DateTimeFormat.forPattern("dd MMM yyyy")
  val outFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

  private def audit(service: String, details: Map[String, String])(implicit hc: HeaderCarrier): Unit = {
    def auditResponse(): Unit = {
      auditing.sendDataEvent(
        DataEvent("submission-tracker", "ServiceResponseSent",
          tags = Map("transactionName" -> service),
          detail = details))
    }
  }

  private def withAudit[T](service: String, details: Map[String, String])(func: Future[T])(implicit hc: HeaderCarrier): Future[T] = {
    audit(service, details) // No need to wait!
    func
  }

  private def convert(in: String): String = outFormat.print(inFormat.parseDateTime(in))

  private def convertData(data: TrackingDataSeq): TrackingDataSeq = {
    data.submissions.fold(data) { found =>
      TrackingDataSeq(Some(found.map(item => {
        item.copy(completionDate = convert(item.completionDate), receivedDate = convert(item.receivedDate))
      })))
    }
  }

  def trackingData(id: String, idType: String)(implicit hc: HeaderCarrier): Future[TrackingDataSeq] = {
    withAudit("trackingData", Map("id" -> id, "idType" -> idType)) {
      trackingConnector.getUserTrackingData(id, idType).map(data => convertData(data))
    }
  }
}