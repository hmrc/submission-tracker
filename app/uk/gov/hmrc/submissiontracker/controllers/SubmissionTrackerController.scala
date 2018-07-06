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

package uk.gov.hmrc.submissiontracker.controllers

import com.google.inject.Singleton
import javax.inject.{Inject, Named}
import play.api.libs.json.Json._
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import uk.gov.hmrc.submissiontracker.controllers.action.AccessControl
import uk.gov.hmrc.submissiontracker.services.SubmissionTrackerService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SubmissionTrackerController @Inject()(override val authConnector: AuthConnector,
                                            val service: SubmissionTrackerService,
                                            @Named("controllers.confidenceLevel") override val confLevel: Int)
  extends BaseController with AccessControl with ErrorHandling {

  def trackingData(id: String, idType: String, journeyId: Option[String] = None): Action[AnyContent] =
    validateAcceptWithAuth(acceptHeaderValidationRules, Some(Nino(id))).async {
      implicit request =>
        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, None)
        errorWrapper(service.trackingData(id, idType).map(as => Ok(toJson(as))))
    }
}
