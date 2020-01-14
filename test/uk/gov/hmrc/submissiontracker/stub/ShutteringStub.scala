/*
 * Copyright 2020 HM Revenue & Customs
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

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.submissiontracker.connectors.ShutteringConnector
import uk.gov.hmrc.submissiontracker.domain.Shuttering
import uk.gov.hmrc.submissiontracker.domain.types.ModelTypes.JourneyId

import scala.concurrent.{ExecutionContext, Future}

trait ShutteringStub extends MockFactory {

  def stubShutteringResponse(
                              response: Shuttering
                            )(
                              implicit shutteringConnector: ShutteringConnector
                            ): CallHandler[Future[Shuttering]] =
    (shutteringConnector
      .getShutteringStatus(_: JourneyId)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returning(Future successful response)
}
