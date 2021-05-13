/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.submissiontracker.controllers

import com.google.inject.Singleton
import play.api.Logger

import javax.inject.{Inject, Named}
import play.api.libs.json.Json._
import play.api.mvc.{Action, AnyContent, BodyParser, ControllerComponents}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter.fromRequest
import uk.gov.hmrc.submissiontracker.connectors.ShutteringConnector
import uk.gov.hmrc.submissiontracker.controllers.action.AccessControl
import uk.gov.hmrc.submissiontracker.domain.types.ModelTypes.{IdType, JourneyId}
import uk.gov.hmrc.submissiontracker.services.SubmissionTrackerService

import scala.concurrent.ExecutionContext

@Singleton
class SubmissionTrackerController @Inject() (
  override val authConnector:                                   AuthConnector,
  val service:                                                  SubmissionTrackerService,
  @Named("controllers.confidenceLevel") override val confLevel: Int,
  cc:                                                           ControllerComponents,
  shutteringConnector:                                          ShutteringConnector
)(implicit val executionContext:                                ExecutionContext)
    extends BackendController(cc)
    with AccessControl
    with ErrorHandling
    with ControllerChecks {

  override val logger: Logger = Logger(this.getClass)

  def trackingData(
    id:        String,
    idType:    IdType,
    journeyId: JourneyId
  ): Action[AnyContent] =
    validateAcceptWithAuth(acceptHeaderValidationRules, Some(Nino(id))).async { implicit request =>
      implicit val hc: HeaderCarrier = fromRequest(request)
      shutteringConnector.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          errorWrapper {
            service.trackingData(id, idType).map(as => Ok(toJson(as)))
          }
        }
      }
    }
  override val parser: BodyParser[AnyContent] = cc.parsers.anyContent
}
