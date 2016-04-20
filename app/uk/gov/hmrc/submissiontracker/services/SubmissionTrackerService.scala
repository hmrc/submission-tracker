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

package uk.gov.hmrc.submissiontracker.services

import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.submissiontracker.config.MicroserviceAuditConnector
import uk.gov.hmrc.submissiontracker.connector._
import uk.gov.hmrc.play.audit.model.DataEvent
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.submissiontracker.domain.{TrackingData, Milestone, TrackingDataSeq}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait SubmissiontrackerService {
  def ping()(implicit hc:HeaderCarrier): Future[Boolean]

  def trackingData(id: String, idType:String)(implicit hc:HeaderCarrier): Future[TrackingDataSeq]
}

trait LivesubmissiontrackerService extends SubmissiontrackerService {
  def authConnector: AuthConnector
  def trackingConnector: TrackingConnector


  def audit(service:String, details:Map[String, String])(implicit hc:HeaderCarrier) = {
    def auditResponse(): Unit = {
      MicroserviceAuditConnector.sendEvent(
        DataEvent("mobile-messages", "ServiceResponseSent",
          tags = Map("transactionName" -> service),
          detail = details))
    }
  }

  def withAudit[T](service: String, details: Map[String, String])(func:Future[T])(implicit hc:HeaderCarrier) = {
    audit(service, details) // No need to wait!
    func
  }

  def ping()(implicit hc:HeaderCarrier): Future[Boolean]

  def trackingData(id: String, idType:String)(implicit hc:HeaderCarrier): Future[TrackingDataSeq] = {
    withAudit("trackingData", Map("id" -> id, "idType" -> idType)) {
      trackingConnector.getUserTrackingData(id, idType)
    }
  }

}

object SandboxsubmissiontrackerService extends SubmissiontrackerService with FileResource {

  def ping()(implicit hc:HeaderCarrier): Future[Boolean] = Future.successful(true)

  def trackingData(id: String, idType:String)(implicit hc:HeaderCarrier): Future[TrackingDataSeq] = {
    val milestones =  Seq(Milestone("one","open"))
    val trackingData = TrackingDataSeq(Some(Seq(TrackingData("formId", "formName", "ref1", "some-business", "20150801", "20150801", milestones))))
    Future.successful(trackingData)
  }


}

object LivesubmissiontrackerService extends LivesubmissiontrackerService {
  override val authConnector: AuthConnector = AuthConnector
  override val trackingConnector = TrackingConnector

  def ping()(implicit hc:HeaderCarrier): Future[Boolean] = Future.successful(true)
}