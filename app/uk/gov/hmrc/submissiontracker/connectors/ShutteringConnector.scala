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

import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet}
import uk.gov.hmrc.submissiontracker.domain.Shuttering
import uk.gov.hmrc.submissiontracker.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ShutteringConnector @Inject() (
  http:                                   HttpGet,
  @Named("mobile-shuttering") serviceUrl: String) {

  val logger: Logger = Logger(this.getClass)

  def getShutteringStatus(
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    ex:                     ExecutionContext
  ): Future[Shuttering] =
    http
      .GET[JsValue](s"$serviceUrl/mobile-shuttering/service/submission-tracker/shuttered-status?journeyId=$journeyId")
      .map { json =>
        (json).as[Shuttering]
      } recover {
      case e =>
        logger.warn(s"Call to mobile-shuttering failed:\n $e \nAssuming unshuttered.")
        Shuttering.shutteringDisabled
    }
}
