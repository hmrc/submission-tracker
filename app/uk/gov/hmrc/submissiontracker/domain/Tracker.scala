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

package uk.gov.hmrc.submissiontracker.domain

import play.api.libs.json.{Json, Format}

case class Milestone (milestone: String, status: String)

object Milestone {
  implicit val milestoneFormat: Format[Milestone] = Json.format[Milestone]
}

case class TrackingData ( formId: String,
                          formName: String,
                          dfsSubmissionReference: String,
                          businessArea: String,
                          receivedDate: String,
                          completionDate: String,
                          milestones: Seq[Milestone])

object TrackingData {
  implicit val trackingDataFormat: Format[TrackingData] = Json.format[TrackingData]
}

case class TrackingDataPresenter(trackingData: TrackingData, latestMilestone:String)

object TrackingDataPresenter{
  implicit val trackingDataPresenterFormat: Format[TrackingDataPresenter] = Json.format[TrackingDataPresenter]
}

case class TrackingDataSeq (submissions: Option[Seq[TrackingData]])

object TrackingDataSeq {
  implicit val trackingDataSeqFormat: Format[TrackingDataSeq] = Json.format[TrackingDataSeq]
}
