/*
 * Copyright 2016 HM Revenue & Customs
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

import uk.gov.hmrc.api.controllers.ErrorResponse

case object ErrorNinoInvalid extends ErrorResponse(400, "NINO_INVALID", "The provided NINO is invalid")

case object ErrorUnauthorizedNoNino extends ErrorResponse(401, "UNAUTHORIZED", "NINO does not exist on account")

case object ForbiddenAccess extends ErrorResponse(403, "UNAUTHORIZED", "Access denied!")
