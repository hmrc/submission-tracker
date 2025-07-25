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

package uk.gov.hmrc.submissiontracker.domain

import play.api.libs.json.*

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class Milestone(milestone: String, status: String)

object Milestone {
  implicit val milestoneFormat: Format[Milestone] = Json.format[Milestone]
}

case class TrackingData(formId: String,
                        formName: String,
                        dfsSubmissionReference: String,
                        businessArea: String,
                        receivedDate: LocalDateTime,
                        completionDate: LocalDateTime,
                        milestones: Seq[Milestone]
                       )

object TrackingData {
  implicit val trackingDataFormat: Format[TrackingData] = Json.format[TrackingData]
}

case class TrackingDataSeq(submissions: Option[Seq[TrackingData]])

object TrackingDataSeq {
  implicit val trackingDataSeqFormat: Format[TrackingDataSeq] = Json.format[TrackingDataSeq]
}

case class TrackingDataResponse(formId: String,
                                formName: String,
                                formNameCy: String,
                                dfsSubmissionReference: String,
                                receivedDate: LocalDateTime,
                                completionDate: LocalDateTime,
                                milestone: String,
                                milestones: Seq[Milestone]
                               )

object TrackingDataResponse {
  private val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
  implicit val dateTimeWriter: Writes[LocalDateTime] = (dateTime: LocalDateTime) => JsString(dateTime.format(df))
  implicit val trackingDataFormat: Format[TrackingDataResponse] = Json.format[TrackingDataResponse]
}

case class TrackingDataSeqResponse(submissions: Option[Seq[TrackingDataResponse]])

object TrackingDataSeqResponse {
  implicit val trackingDataSeqFormat: Format[TrackingDataSeqResponse] = Json.format[TrackingDataSeqResponse]

  val noSubmissions: TrackingDataSeqResponse = apply(None)
}
