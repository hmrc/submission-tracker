/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.submissiontracker.controllers.action

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{ActionBuilder, Request, Result, Results}
import uk.gov.hmrc.api.controllers._
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.submissiontracker.controllers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case object ErrorUnauthorizedMicroService extends ErrorResponse(401, "UNAUTHORIZED", "Unauthorized to access resource")

case object ErrorUnauthorizedWeakCredStrength extends ErrorResponse(401, "WEAK_CRED_STRENGTH", "Credential Strength on account does not allow access")

case object ErrorUnauthorized extends ErrorResponse(401, "UNAUTHORIZED", "Invalid request")

trait AccessControl extends HeaderValidator with Results with Authorisation {

  lazy val requiresAuth: Boolean = true

  def validateAcceptWithAuth(rules: Option[String] ⇒ Boolean, taxId: Option[Nino]): ActionBuilder[Request] = new ActionBuilder[Request] {

    def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
      if (rules(request.headers.get("Accept"))) {
        if (requiresAuth) invokeAuthBlock(request, block, taxId)
        else block(request)
      }
      else Future.successful(Status(ErrorAcceptHeaderInvalid.httpStatusCode)(Json.toJson(ErrorAcceptHeaderInvalid)))
    }
  }

  def invokeAuthBlock[A](request: Request[A], block: Request[A] => Future[Result], taxId: Option[Nino]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, None)

    grantAccess(taxId.getOrElse(Nino(""))).flatMap { _ =>
      block(request)
    }.recover {
      case _: uk.gov.hmrc.http.Upstream4xxResponse =>
        Logger.info("Unauthorized! Failed to grant access since 4xx response!")
        Unauthorized(Json.toJson(ErrorUnauthorizedMicroService))

      case _: NinoNotFoundOnAccount =>
        Logger.info("Unauthorized! NINO not found on account!")
        Unauthorized(Json.toJson(ErrorUnauthorizedNoNino))

      case _: FailToMatchTaxIdOnAuth =>
        Logger.info("Unauthorized! Failure to match URL NINO against Auth NINO")
        Status(ErrorUnauthorized.httpStatusCode)(Json.toJson(ErrorUnauthorized))

      case _: AccountWithLowCL =>
        Logger.info("Unauthorized! Account with low CL!")
        Unauthorized(Json.toJson(ErrorUnauthorizedLowCL))

      case _: AccountWithWeakCredStrength =>
        Logger.info("Unauthorized! Account with weak cred strength!")
        Unauthorized(Json.toJson(ErrorUnauthorizedWeakCredStrength))
    }
  }
}