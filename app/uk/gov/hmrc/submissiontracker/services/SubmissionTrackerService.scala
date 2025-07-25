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

package uk.gov.hmrc.submissiontracker.services

import javax.inject.{Inject, Named, Singleton}
import play.api.Configuration
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.submissiontracker.connectors.TrackingConnector
import uk.gov.hmrc.submissiontracker.domain.types.IdType
import uk.gov.hmrc.submissiontracker.domain.{Milestone, TrackingDataResponse, TrackingDataSeq, TrackingDataSeqResponse}
import uk.gov.hmrc.submissiontracker.utils.WelshFormNameTranslator

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionTrackerService @Inject() (val trackingConnector: TrackingConnector,
                                          val auditConnector: AuditConnector,
                                          val formNameService: FormNameService,
                                          val configuration: Configuration,
                                          @Named("appName") val appName: String,
                                          auditService: AuditService
                                         )(implicit ec: ExecutionContext)
    extends WelshFormNameTranslator {

  def trackingData(
    id: String,
    idType: IdType
  )(implicit hc: HeaderCarrier): Future[TrackingDataSeqResponse] =
    auditService.withAudit("trackingData", Map("id" -> id, "idType" -> idType.value.toString)) {
      trackingConnector.getUserTrackingData(id, idType).map(data => convertData(data))
    }

  private def getCurrentMilestone(milestones: Seq[Milestone]): String =
    milestones.find(milestone => milestone.status.toLowerCase == "current") match {
      case Some(currentMilestone) => currentMilestone.milestone
      case None                   => throw new IllegalStateException("No Milestone with a status of current returned from Tracking")
    }

  private def convertData(data: TrackingDataSeq): TrackingDataSeqResponse =
    data.submissions.fold(TrackingDataSeqResponse.noSubmissions) { found =>
      TrackingDataSeqResponse(Some(found.map { item =>
        TrackingDataResponse(
          formId                 = item.formId,
          formName               = formNameService.getFormName(item.formId),
          formNameCy             = getFormNameInWelsh(item.formId),
          dfsSubmissionReference = item.dfsSubmissionReference,
          receivedDate           = item.receivedDate,
          completionDate         = item.completionDate,
          milestone              = getCurrentMilestone(item.milestones),
          milestones             = item.milestones
        )
      }))
    }
}
