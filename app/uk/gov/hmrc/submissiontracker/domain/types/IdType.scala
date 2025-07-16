/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.submissiontracker.domain.types

import eu.timepit.refined.refineV
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.api.Refined
import play.api.mvc.{PathBindable, QueryStringBindable}
import uk.gov.hmrc.submissiontracker.domain.types.GenericRefinedBinders.{pathBindable, queryStringBindable}

type ValidIdType = MatchesRegex["(nino)|(utr)"]

type RefinedIdType = String Refined ValidIdType

final case class IdType private (value: RefinedIdType)

object IdType {
  def fromStringToId(s: String): Either[String, IdType] =
    refineV[ValidIdType](s).map(IdType(_))

  given QueryStringBindable[IdType] =
    queryStringBindable(fromStringToId)(_.value.value)

  given PathBindable[IdType] =
    pathBindable(fromStringToId)(_.value.value)

}
