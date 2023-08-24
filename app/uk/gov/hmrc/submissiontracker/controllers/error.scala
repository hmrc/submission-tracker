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

package uk.gov.hmrc.submissiontracker.controllers

import play.api.libs.json.Json.toJson
import play.api.mvc.{BaseController, Result}
import play.api.{Logger, mvc}
import uk.gov.hmrc.api.controllers.{ErrorInternalServerError, ErrorNotFound, ErrorResponse, ErrorUnauthorizedLowCL}
import uk.gov.hmrc.http._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case object ErrorUnauthorizedNoNino extends ErrorResponse(401, "UNAUTHORIZED", "NINO does not exist on account")

class GrantAccessException(message: String) extends HttpException(message, 401)

class FailToMatchTaxIdOnAuth extends GrantAccessException("Unauthorised! Failure to match URL NINO against Auth NINO")

class NinoNotFoundOnAccount extends GrantAccessException("Unauthorised! NINO not found on account!")

class AccountWithLowCL extends GrantAccessException("Unauthorised! Account with low CL!")

class AccountWithWeakCredStrength(message: String) extends uk.gov.hmrc.http.HttpException(message, 401)

trait ErrorHandling {
  self: BaseController =>

  val logger: Logger = Logger(this.getClass)

  def errorWrapper(func: => Future[mvc.Result]): Future[Result] =
    func.recover {
      case _: NotFoundException => Status(ErrorNotFound.httpStatusCode)(toJson[ErrorResponse](ErrorNotFound))

      case ex: UpstreamErrorResponse if ex.statusCode == 401 => Unauthorized(toJson[ErrorResponse](ErrorUnauthorizedNoNino))

      case ex: UpstreamErrorResponse if ex.statusCode == 403 => Unauthorized(toJson[ErrorResponse](ErrorUnauthorizedLowCL))

      case e: Throwable =>
        logger.error(s"Internal server error: ${e.getMessage}", e)
        Status(ErrorInternalServerError.httpStatusCode)(toJson[ErrorResponse](ErrorInternalServerError))
    }
}
