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

package uk.gov.hmrc.submissiontracker.connectors

import javax.inject.{Inject, Named, Singleton}
import play.api.*
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.submissiontracker.domain.TrackingDataSeq
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.submissiontracker.domain.types.IdType

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TrackingConnector @Inject() (@Named("trackingUrl") val trackingBaseUrl: String, val http: HttpClientV2) {

  val logger: Logger = Logger(this.getClass)

  def getUserTrackingData(
    id: String,
    idType: IdType
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrackingDataSeq] = {
    logger.debug("submission-tracker: Requesting tracking data")
    http
      .get(url"${trackingDataLink(id, idType)}")
      .execute[TrackingDataSeq]
  }

  private def trackingDataLink(
    id: String,
    idType: IdType
  ): String = s"$trackingBaseUrl/tracking-data/user/${idType.value}/$id"

}
