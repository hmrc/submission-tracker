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

package uk.gov.hmrc.submissiontracker.controllers.action

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.api.controllers._
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter.fromRequest
import uk.gov.hmrc.submissiontracker.controllers._

import scala.concurrent.{ExecutionContext, Future}

case object ErrorUnauthorizedMicroService extends ErrorResponse(401, "UNAUTHORIZED", "Unauthorized to access resource")

case object ErrorUnauthorizedWeakCredStrength
    extends ErrorResponse(401, "WEAK_CRED_STRENGTH", "Credential Strength on account does not allow access")

case object ErrorUnauthorized extends ErrorResponse(401, "UNAUTHORIZED", "Invalid request")

trait AccessControl extends HeaderValidator with Results with Authorisation {
  outer =>

  implicit val executionContext: ExecutionContext
  val parser:                    BodyParser[AnyContent]
  val logger: Logger = Logger(this.getClass)

  lazy val requiresAuth: Boolean = true

  def validateAcceptWithAuth(
    rules: Option[String] ⇒ Boolean,
    taxId: Option[Nino]
  ): ActionBuilder[Request, AnyContent] =
    new ActionBuilder[Request, AnyContent] {

      def invokeBlock[A](
        request: Request[A],
        block:   Request[A] => Future[Result]
      ): Future[Result] =
        if (rules(request.headers.get("Accept"))) {
          if (requiresAuth) invokeAuthBlock(request, block, taxId)
          else block(request)
        } else Future.successful(Status(ErrorAcceptHeaderInvalid.httpStatusCode)(Json.toJson(ErrorAcceptHeaderInvalid)))

      override def parser: BodyParser[AnyContent] = outer.parser

      override protected def executionContext: ExecutionContext = outer.executionContext
    }

  def invokeAuthBlock[A](
    request: Request[A],
    block:   Request[A] => Future[Result],
    taxId:   Option[Nino]
  ): Future[Result] = {
    implicit val hc: HeaderCarrier = fromRequest(request)

    grantAccess(taxId.getOrElse(Nino("")))
      .flatMap { _ =>
        block(request)
      }
      .recover {
        case _: uk.gov.hmrc.http.Upstream4xxResponse =>
          logger.info("Unauthorized! Failed to grant access since 4xx response!")
          Unauthorized(Json.toJson(ErrorUnauthorizedMicroService))

        case _: NinoNotFoundOnAccount =>
          logger.info("Unauthorized! NINO not found on account!")
          Unauthorized(Json.toJson(ErrorUnauthorizedNoNino))

        case _: FailToMatchTaxIdOnAuth =>
          logger.info("Unauthorized! Failure to match URL NINO against Auth NINO")
          Status(ErrorUnauthorized.httpStatusCode)(Json.toJson(ErrorUnauthorized))

        case _: AccountWithLowCL =>
          logger.info("Unauthorized! Account with low CL!")
          Unauthorized(Json.toJson(ErrorUnauthorizedLowCL))

        case _: AccountWithWeakCredStrength =>
          logger.info("Unauthorized! Account with weak cred strength!")
          Unauthorized(Json.toJson(ErrorUnauthorizedWeakCredStrength))
      }
  }
}
