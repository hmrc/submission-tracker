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

import javax.inject.{Inject, Named}

import com.google.inject.Singleton
import play.api.libs.json._
import play.api.{Logger, mvc}
import uk.gov.hmrc.api.controllers._
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{ForbiddenException, HeaderCarrier, NotFoundException, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.submissiontracker.controllers.action.AccessControl
import uk.gov.hmrc.submissiontracker.services.{LivesubmissiontrackerService, SandboxsubmissiontrackerService, SubmissiontrackerService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ErrorHandling {
  self:BaseController =>

  def errorWrapper(func: => Future[mvc.Result])(implicit hc:HeaderCarrier) = {
    func.recover {
      case ex:NotFoundException => Status(ErrorNotFound.httpStatusCode)(Json.toJson(ErrorNotFound))

      case ex:UnauthorizedException => Unauthorized(Json.toJson(ErrorUnauthorizedNoNino))

      case ex:ForbiddenException => Unauthorized(Json.toJson(ErrorUnauthorizedLowCL))

      case e: Throwable =>
        Logger.error(s"Internal server error: ${e.getMessage}", e)
        Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError))
    }
  }
}

trait SubmissionTrackerController extends BaseController with AccessControl with ErrorHandling {

  val service: SubmissiontrackerService

  final def trackingData(id:String, idType:String, journeyId: Option[String] = None) = validateAcceptWithAuth(acceptHeaderValidationRules, Some(Nino(id))).async {
    implicit request =>
      implicit val hc = HeaderCarrierConverter.fromHeadersAndSession(request.headers, None)
      errorWrapper(service.trackingData(id, idType).map(as => Ok(Json.toJson(as))))
  }
}

@Singleton
class SandboxSubmissionTrackerController @Inject()(override val authConnector: AuthConnector,
                                                  @Named("controllers.confidenceLevel") override val confLevel: Int)
  extends SubmissionTrackerController {
  override val service = SandboxsubmissiontrackerService
}

@Singleton
class LiveSubmissionTrackerController @Inject()(override val authConnector: AuthConnector,
                                                @Named("controllers.confidenceLevel") override val confLevel: Int)
  extends SubmissionTrackerController {
  override val service: LivesubmissiontrackerService = LivesubmissiontrackerService
}
